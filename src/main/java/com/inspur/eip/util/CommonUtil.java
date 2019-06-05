package com.inspur.eip.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.EipAllocateParam;
import com.inspur.eip.entity.ReturnMsg;
import com.inspur.eip.entity.ReturnSbwMsg;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.icp.common.util.Base64Util;
import com.inspur.icp.common.util.OSClientUtil;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.inspur.eip.util.HsConstants.SCHEDULETIME;

@Slf4j
public class CommonUtil {

    public static boolean isDebug = true;
    public static boolean qosDebug = false;


    public static String getGmtDateString() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static String authUrl = "https://10.110.25.117:5000/v3"; //endpoint Url
    private static String user = "admin";
    private static String password = "89rqdHLMN5rm0x1P";
    private static String projectId = "140785795de64945b02363661eb9e769";
    private static String userDomainId = "default";
    private static String region="RegionOne";
    private static String region1="cn-north-3";


    /**
     * get the Keycloak authorization token  from httpHeader;
     * @return  string string
     */
    public static String getKeycloackToken() {
        //important
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null != requestAttributes) {
            HttpServletRequest request = requestAttributes.getRequest();
            String keyCloackToken = request.getHeader("authorization");

            if (keyCloackToken == null) {
                log.error("Failed to get token,request:{}",request);
                return null;
            } else {
                log.debug("Get token:{}",keyCloackToken);
                return keyCloackToken;
            }
        }
        return null;
    }

    public static String getUserId()throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        }else{
            JSONObject jsonObject = decodeUserInfo(token);
            if (jsonObject !=null){
                String sub = (String) jsonObject.get("sub");
                if (sub != null) {
                    log.debug("getUserId:{}", sub);
                    return sub;
                } else {
                    throw new KeycloakTokenException("400-Bad request:can't get user info from header,please check");
                }
            }else {
                log.info("jsonObject is null");
                throw new KeycloakTokenException("400-Bad request:can't get jsonObject info from header,please check");
            }
        }
    }
    public static String getUserId(String token)throws KeycloakTokenException {

        if(null == token){
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        }else{
            JSONObject jsonObject = decodeUserInfo(token);
            if (jsonObject !=null){
                String sub = (String) jsonObject.get("sub");
                if (sub != null) {
                    log.info("getUserId:{}", sub);
                    return sub;
                } else {
                    throw new KeycloakTokenException("400-Bad request:can't get user info from header,please check");
                }
            }else {
                log.info("jsonObject is null");
                throw new KeycloakTokenException("400-Bad request:can't get jsonObject info from header,please check");
            }
        }
    }

    private static JSONObject decodeUserInfo(String keycloakToken) {
        Base64.Decoder decoder = Base64.getDecoder();
        // keycloak 的 token 被 '.' 分隔符分成了三段，其中第二段包含了用户信息，也就是 targetStr
        String targetStr = keycloakToken.substring((keycloakToken.indexOf(".") + 1), keycloakToken.lastIndexOf("."));
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(new String(decoder.decode(targetStr)));
            new JSONObject();
        } catch (NullPointerException e) {
            log.error("null userInfo");
        }
        return jsonObject;
    }



    public static String readRequestAsChars(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            log.error("ReadAsChars exception", e);
        }
        return sb.toString();
    }


    public static String getUsername()throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        }else{
            JSONObject jsonObject = decodeUserInfo(token);
            if (jsonObject != null) {
                String username = (String) jsonObject.get("preferred_username");
            if(username!=null){
                log.info("getUsername:{}", username);
                return username;
            }else{
                throw new KeycloakTokenException("400-Bad request:can't get user info from header,please check");
            }
            }else {
                log.info("jsonObject is null");
                throw new KeycloakTokenException("400-Bad request:can't get jsonObject info from header,please check");
            }
        }
    }

    public static JSONObject handlerResopnse(ReturnResult response){

        if(response!=null) {

            try {
                JSONObject returnInfo;
                if(response.getCode() == HttpStatus.SC_OK) {
                    returnInfo = JSONObject.parseObject(response.getMessage());
                    returnInfo.put("statusCode", HttpStatus.SC_OK);
                }else{
                    returnInfo = new JSONObject();
                    returnInfo.put("statusCode", response.getCode());
                    returnInfo.put("message", response.getMessage());
                }

                log.info("RETURN ==>{}", returnInfo);
                return returnInfo;
            }catch (Exception e){
                log.error("handlerResopnse exception:", e);
            }
        }
        JSONObject result=new JSONObject();
        result.put(HsConstants.SUCCESS,false);
        result.put("statusCode", HttpStatus.SC_NOT_IMPLEMENTED);
        result.put("message","Can not get return result.");
        return result;
    }


    public static ReturnMsg preCheckParam(EipAllocateParam param){
        String errorMsg = " ";
        if(null == param){
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,"Failed to get param.");
        }
        if((0 >= param.getBandwidth()) || (param.getBandwidth() > 500)){
            errorMsg = "value must be 1-500.";
        }
        if(null != param.getChargemode()) {
            if (!param.getChargemode().equalsIgnoreCase(HsConstants.BANDWIDTH) &&
                    !param.getChargemode().equals(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH)) {
                errorMsg = errorMsg + "Only Bandwidth,SharedBandwidth is allowed. ";
            }
            if(param.getChargemode().equals(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH)
                    && (null == param.getSbwId())) {
                errorMsg = errorMsg + "SharedBandwidth id is needed in sharedbandwidth charge mode. ";
            }
        }

        if(null != param.getBillType()) {
            if (!param.getBillType().equals(HsConstants.MONTHLY) && !param.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                errorMsg = errorMsg + "Only monthly,hourlySettlement is allowed. ";
            }
        }
        if(param.getRegion().isEmpty()){
            errorMsg = errorMsg + "can not be blank.";
        }
        String tp = param.getIptype();
        if(null != tp) {
            if (!tp.equals("5_bgp") && !tp.equals("5_sbgp") && !tp.equals("5_telcom") &&
                    !tp.equals("5_union") && !tp.equals("BGP")) {
                errorMsg = errorMsg + "Only 5_bgp,5_sbgp, 5_telcom, 5_union ,  BGP is allowed. ";
            }
        }else{
            errorMsg = errorMsg + "Only 5_bgp,5_sbgp, 5_telcom, 5_union ,  BGP is allowed. ";
        }
        if(errorMsg.equals(" ")) {
            return ReturnMsgUtil.error(ReturnStatus.SC_OK, errorMsg);
        }else {
            log.error(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,errorMsg);
        }
    }

    /**
     * sbw param check
     * @param param param
     * @return return
     */
    public static ReturnMsg preSbwCheckParam(SbwUpdateParam param){
        String errorMsg = " ";
        if(null == param){
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,"Failed to get param.");
        }
        if((5 > param.getBandwidth()) || (param.getBandwidth() > 500)){
            errorMsg = "value must be 5-500.";
        }

        if(null != param.getBillType()) {
            if (!param.getBillType().equals(HsConstants.MONTHLY) && !param.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                errorMsg = errorMsg + "Only monthly,hourlySettlement is allowed. ";
            }
        }
        if(param.getRegion().isEmpty()){
            errorMsg = errorMsg + "can not be blank.";
        }
        if(errorMsg.equals(" ")) {
            log.info(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_OK, errorMsg);
        }else {
            log.error(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,errorMsg);
        }
    }


    public static String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    //    Greenwich mean time
    public static Date getGmtDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new Date();
    }

    /*
     *获取当天日期:yyyy-MM-dd
     */
    public static String getToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-ss");
        return formatter.format(currentTime);
    }


    @Setter
    private static org.json.JSONObject KeyClockInfo;
    @Value("${openstackIp}")
    private String openstackIp;
    @Value("${openstackUrl}")
    private String openstackUrl;
    @Value("${userNameS}")
    private String userNameS;
    @Value("${passwordS}")
    private String passwordS;
    @Value("${projectIdS}")
    private String projectIdS;
    @Value("${userDomainIdS}")
    private String userDomainIdS;
    @Value("${debugRegionS}")
    private String debugRegionS;

    @Value("${scheduleTime}")
    private String scheduleTime;

    private static Config config = Config.newConfig().withSSLVerificationDisabled();
    private static Map<String,String> userConfig = new HashMap<>(16);

    @PostConstruct
    public void init(){
        userConfig.put("openstackIp",openstackIp);
        userConfig.put("userNameS",userNameS);
        userConfig.put("passwordS",passwordS);
        userConfig.put("projectIdS",projectIdS);
        userConfig.put("userDomainIdS",userDomainIdS);
        userConfig.put("debugRegionS",debugRegionS);
        userConfig.put("openstackUrl",openstackUrl);
        userConfig.put(SCHEDULETIME, scheduleTime);
    }

    public static Map<String, String> getUserConfig(){
        return userConfig;
    }

    //administrator rights
    public static OSClient.OSClientV3 getOsClientV3(){
        //String token = getKeycloackToken();
        return OSFactory.builderV3()
                .endpoint(userConfig.get("openstackUrl"))
                .credentials(userConfig.get("userNameS"), userConfig.get("passwordS"),
                        Identifier.byId(userConfig.get("userDomainIdS")))
                .withConfig(config)
                .scopeToProject(Identifier.byId(userConfig.get("projectIdS")))
                .authenticate().useRegion(userConfig.get("debugRegionS"));
    }


    public static OSClient.OSClientV3 getOsClientV3Util(String userRegion) throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            log.error("can't get token.");
            return getOsClientV3();
        }

        if(isDebug){
            userRegion = userConfig.get("debugRegionS");
            log.debug("=============={}", userRegion);
        }
        if(token.startsWith("Bearer Bearer")){
            token = token.substring(7);
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        setKeyClockInfo(jsonObject);
        log.info("decode::"+jsonObject);
        if(jsonObject.has("project")){
            String project = (String) jsonObject.get("project");
            log.debug("Get openstack ip:{}, region:{}, project:{}.",userConfig.get("openstackIp"), userRegion, project);
            return OSClientUtil.getOSClientV3(userConfig.get("openstackIp"),token,project,userRegion);
        }else {
            String clientId = jsonObject.getString("clientId");
            if(null != clientId && clientId.equalsIgnoreCase("iaas-server")){
                log.info("Client token, User has right to operation, client:{}", clientId);
                return getOsClientV3();
            }else{
                log.error("User has no right to operation.{}", jsonObject.toString());
                throw new KeycloakTokenException(CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_NO_PROJECT));
            }
        }

    }


    public static OSClient.OSClientV3 getOsClientV3Util(String userRegion, String token) throws KeycloakTokenException {

        if(null == token){
            log.error("can't get token.");
            return getOsClientV3();
        }

        if(isDebug){
            userRegion = userConfig.get("debugRegionS");
            log.debug("=============={}", userRegion);
        }
        if(token.startsWith("Bearer Bearer")){
            token = token.substring(7);
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        setKeyClockInfo(jsonObject);
        log.info("decode::"+jsonObject);
        if(jsonObject.has("project")){
            String project = (String) jsonObject.get("project");
            log.debug("Get openstack ip:{}, region:{}, project:{}.",userConfig.get("openstackIp"), userRegion, project);
            return OSClientUtil.getOSClientV3(userConfig.get("openstackIp"),token,project,userRegion);
        }else {
            String clientId = jsonObject.getString("clientId");
            if(null != clientId && clientId.equalsIgnoreCase("iaas-server")){
                log.info("Client token, User has right to operation, client:{}", clientId);
                return getOsClientV3();
            }else{
                log.error("User has no right to operation.{}", jsonObject.toString());
                throw new KeycloakTokenException(CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_NO_PROJECT));
            }
        }

    }


    public static org.json.JSONObject getTokenInfo(){

        return KeyClockInfo;

    }


    public static String getProjectId(String userRegion,  OSClient.OSClientV3 os) throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            log.info("can't get token, use default project admin 140785795de64945b02363661eb9e769");
            return userConfig.get("projectIdS");
        }else{
            try{
//                OSClientV3 os= getOsClientV3Util(userRegion);
                String projectid_client=os.getToken().getProject().getId();
                log.info("getProjectId:{}", projectid_client);
                return projectid_client;
            }catch (Exception e){
                log.error("get projectid from token error");
                throw new KeycloakTokenException(CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_TOKEN_EXPIRED));
            }
        }
    }

    public static String getRegionName() {

        return userConfig.get("debugRegionS");
    }
    public static String getProjectName()throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            throw new KeycloakTokenException(CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_NULL));
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        String projectName = null;
        if (jsonObject.has("project")) {
            projectName = (String) jsonObject.get("project");
        } else if (jsonObject.has("preferred_username")) {
            projectName = (String) jsonObject.get("preferred_username");
        }
        if (projectName != null) {
            log.info("getProjectName:{}", projectName);
        }
        return projectName;
    }
    public static String getProjectName(String token)throws KeycloakTokenException {

        if(null == token){
            throw new KeycloakTokenException(CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_NULL));
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        String projectName = null;
        if (jsonObject.has("project")) {
            projectName = (String) jsonObject.get("project");
        } else if (jsonObject.has("preferred_username")) {
            projectName = (String) jsonObject.get("preferred_username");
        }
        if (projectName != null) {
            log.info("getProjectName:{}", projectName);
        }
        return projectName;
    }
    public static boolean isAuthoried(String projectId) {

        String token = getKeycloackToken();
        if(null == token){
            log.error("User has no token.");
            return false;
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        String userId = (String) jsonObject.get("sub");
        if(userId.equals(projectId)){
            return true;
        }
        String clientId = null;
        if(jsonObject.has("clientId")) {
            clientId = jsonObject.getString("clientId");
        }
        if(null != clientId && clientId.equalsIgnoreCase("iaas-server")){
            log.info("Client token, User has right to operation, client:{}", clientId);
            return true;
        }else{
            log.error("User has no right to operation.{}", jsonObject.toString());
            return false;
        }
    }

    public static boolean verifyToken(String token, String userId){
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);

        String userIdInToken = (String) jsonObject.get("sub");
        if(userIdInToken.equals(userId)){
            return true;
        }

        return false;

    }
    /**
     * 是否是超级管理员权限
     * @return
     */
    public static boolean isSuperAccount() {

        String token = getKeycloackToken();
        if(null == token){
            log.error("User has no token.");
            return false;
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        String  realmAccess = null;
        if (jsonObject.has("realm_access")){
            realmAccess = jsonObject.getJSONObject("realm_access").toString();
        }
        if (realmAccess!= null && realmAccess.contains("OPERATE_ADMIN")){
            log.info("Client token, User has right to operation, realmAccess:{}", realmAccess);
            return true;
        }else{
            log.error("User has no right to operation.{}", jsonObject.toString());
            return false;
        }
    }



}
