package com.inspur.eip.util.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.ReturnMsg;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import com.inspur.iam.adapter.util.SecurityContextUtil;
import com.inspur.icp.common.util.Base64Util;
import com.inspur.icp.common.util.OSClientUtil;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@Component
public class CommonUtil {

    public static boolean isDebug = true;
    public static boolean qosDebug = false;

    @Setter
    private static org.json.JSONObject KeyClockInfo;
//    @Value("${openstackIp}")
//    private String openstackIp;
//    @Value("${openstackUrl}")
//    private String openstackUrl;
//    @Value("${userNameS}")
//    private String userNameS;
//    @Value("${passwordS}")
//    private String passwordS;
//    @Value("${projectIdS}")
//    private String projectIdS;
//    @Value("${userDomainIdS}")
//    private String userDomainIdS;
//    @Value("${debugRegionS}")
//    private String debugRegionS;
//
//    @Value("${scheduleTime}")
//    private String scheduleTime;

    private static Config config = Config.newConfig().withSSLVerificationDisabled();
    private static Map<String,String> userConfig = new HashMap<>(16);

//    @PostConstruct
//    public void init(){
//        userConfig.put("openstackIp",openstackIp);
//        userConfig.put("userNameS",userNameS);
//        userConfig.put("passwordS",passwordS);
//        userConfig.put("projectIdS",projectIdS);
//        userConfig.put("userDomainIdS",userDomainIdS);
//        userConfig.put("debugRegionS",debugRegionS);
//        userConfig.put("openstackUrl",openstackUrl);
//        userConfig.put(SCHEDULETIME, scheduleTime);
//    }


    //    Greenwich mean time
    public static Date getGmtDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new Date();
    }

    /**
     * 计算当前时间与上一个整点时间的分钟差
     * @return
     */
    public static int countMinuteFromPoint() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return Integer.parseInt(min);
    }

    /**
     * get the Keycloak authorization token  from httpHeader;
     *
     * @return string string
     */
    public static String getKeycloackToken() {
        //important
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            HttpServletRequest request = requestAttributes.getRequest();
            String keyCloackToken = request.getHeader("authorization");

            if (keyCloackToken == null) {
                log.error("Failed to get token,request:{}", request);
                return null;
            } else {
                return keyCloackToken;
            }
        }
        return null;
    }

    public static String getUserId() throws KeycloakTokenException {

        String token = getKeycloackToken();
        if (null == token) {
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        } else {
            return getUserId(token);
        }
    }

    public static String getUserId(String token) throws KeycloakTokenException {

        if (null == token) {
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        } else {
            JSONObject jsonObject = decodeUserInfo(token);
            if (jsonObject != null) {
                String sub = (String) jsonObject.get("sub");
                if (sub != null) {
                    log.debug("getUserId:{}", sub);
                    return sub;
                } else {
                    throw new KeycloakTokenException("400-Bad request:can't get user info from header,please check");
                }
            } else {
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

    public static String getUsername(String token) throws KeycloakTokenException {

        if (null == token) {
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        } else {
            JSONObject jsonObject = decodeUserInfo(token);
            if (jsonObject != null) {
                String username = (String) jsonObject.get("preferred_username");
                if (username != null) {
                    log.info("getUsername:{}", username);
                    return username;
                } else {
                    throw new KeycloakTokenException("400-Bad request:can't get user info from header,please check");
                }
            } else {
                log.info("jsonObject is null");
                throw new KeycloakTokenException("400-Bad request:can't get jsonObject info from header,please check");
            }
        }
    }


    public static ReturnMsg preCheckParam(EipAllocateParam param) {
        String errorMsg = " ";
        if (null == param) {
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, "Failed to get param.");
        }
        if ((0 >= param.getBandwidth()) || (param.getBandwidth() > 500)) {
            errorMsg = "value must be 1-500.";
        }
        if (null != param.getChargeMode()) {
            if (!param.getChargeMode().equalsIgnoreCase(HsConstants.BANDWIDTH) && !param.getChargeMode().equals(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH)
                    && !param.getChargeMode().equals(HsConstants.CHARGE_MODE_TRAFFIC)) {
                errorMsg = errorMsg + "Only Bandwidth,SharedBandwidth,Traffic is allowed. ";
            }
            if (param.getChargeMode().equals(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH) && (null == param.getSbwId())) {
                errorMsg = errorMsg + "SharedBandwidth id is needed in SharedBandwidth charge mode and sbwId not null ";
            }
        }

        if (null != param.getBillType()) {
            if (!param.getBillType().equals(HsConstants.MONTHLY) && !param.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                errorMsg = errorMsg + "Only monthly,hourlySettlement is allowed. ";
            }
        }
        if (param.getRegion().isEmpty()) {
            errorMsg = errorMsg + "can not be blank.";
        }
        String tp = param.getIpType();
        if (null != tp) {
            if (!tp.equals("5_bgp") && !tp.equals("5_sbgp") && !tp.equals("5_telcom") &&
                    !tp.equals("5_union") && !tp.equals("BGP")) {
                errorMsg = errorMsg + "Only 5_bgp,5_sbgp, 5_telcom, 5_union ,  BGP is allowed. ";
            }
        } else {
            errorMsg = errorMsg + "Only 5_bgp,5_sbgp, 5_telcom, 5_union ,  BGP is allowed. ";
        }
        if (errorMsg.equals(" ")) {
            return ReturnMsgUtil.error(ReturnStatus.SC_OK, errorMsg);
        } else {
            log.error(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, errorMsg);
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
            log.debug(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_OK, errorMsg);
        }else {
            log.error(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,errorMsg);
        }
    }

    /**
     * 格林尼治时间
     * @return
     */
    public static String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    /**
     * 北京时间
     * @return
     */
    public static String getBeiJTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return dateFormat.format(date);
    }
    public synchronized static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String uuidStr=uuid.toString();
        return uuidStr;
    }

    public static Map<String, String> getUserConfig(){
        return userConfig;
    }

    //administrator rights2.0
    public static OSClient.OSClientV3 getOsClientV3(){
//          调公共包的AdminClient（administrator rights）
        return OSClientUtil.getClient();
    }

//    //administrator rights1.0
//    public static OSClient.OSClientV3 getOsClientV3(){
//        //String token = getKeycloackToken();
////        return OSFactory.builderV3()
////                .endpoint(userConfig.get("openstackUrl"))
////                .credentials(userConfig.get("userNameS"), userConfig.get("passwordS"),
////                        Identifier.byId(userConfig.get("userDomainIdS")))
////                .withConfig(config)
////                .scopeToProject(Identifier.byId(userConfig.get("projectIdS")))
////                .authenticate().useRegion(userConfig.get("debugRegionS"));
//    }



    public static OSClient.OSClientV3 getOsClientV3Util(String userRegion) throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            log.error("can't get token.");
            return getOsClientV3();
        }
        return getOsClientV3Util(userRegion, token);
    }


    public static OSClient.OSClientV3 getOsClientV3Util(String userRegion, String token) throws KeycloakTokenException {

        if(null == token){
            log.error("can't get token.");
            return getOsClientV3();
        }

        if(isDebug){
            userRegion = userConfig.get("debugRegionS");
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
            return OSClientUtil.getClient(token);
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

    public static String getProjectId()throws KeycloakTokenException {

        String token = SecurityContextUtil.getAccessToken();
        if(null == token){
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        }else {
            return getProjectId(token);
        }
    }
    public static String getProjectId(String token)throws KeycloakTokenException {

        if(null == token){
            throw new KeycloakTokenException(CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_NULL));
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        String projectId = null;
        if(jsonObject != null){
            if(!jsonObject.has("project_id")){
                projectId = (String) jsonObject.get("sub");
            } else {
                projectId = (String) jsonObject.get("project_id");
            }
        }

        if (projectId != null) {
            log.debug("project_id:{}", projectId);
        }
        return projectId;
    }

    public static boolean isAuthoried(String projectId) {

        String token = getKeycloackToken();
        if(null == token){
            log.error("User has no token.");
            return false;
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        String projectIdToken = (String) jsonObject.get("sub");
        if(projectIdToken.equals(projectId)){
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

    public static boolean verifyToken(String token, String projectId){
        String projectIdInToken = null;
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        if(jsonObject.has("project_id")){
            projectIdInToken = (String) jsonObject.get("project_id");
        } else {
            projectIdInToken = (String) jsonObject.get("sub");
        }
        if(projectIdInToken != null){
            log.debug("project_id:{}", projectIdInToken);
        }
        if(projectIdInToken.equals(projectId)){
            return true;
        }

        return false;

    }

    /**
     * 是否是超级管理员权限
     * @return
     */
    public static boolean isSuperAccount(String token) {

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
            log.info("admin account, realmAccess:{}", realmAccess);
            return true;
        }else{
            log.error("Not admin account.{}", jsonObject.toString());
            return false;
        }
    }


}
