package com.inspur.eip.util.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.ReturnMsg;
import com.inspur.eip.entity.fw.Firewall;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
public class CommonUtil {

    public static boolean isDebug = true;
    public static boolean qosDebug = false;


    public static final Set<String> ipType = Stream.of("mobile", "unicom","telecom","radiotv","BGP").collect(Collectors.toSet());

    @Setter
    private static org.json.JSONObject KeyClockInfo;

//    @Value("${scheduleTime}")
//    private String scheduleTime;

    @Value("${firewall.ip}")
    private String firewallIp;

    @Value("${firewall.port}")
    private String firewallPort;

    @Value("${firewall.user}")
    private String firewallUser;

    @Value("${firewall.password}")
    private String firewallPasswd;

    @Value("${firewall.id}")
    private String firewallId;


    private static  String secretKey = "EbfYkitulv73I2p0mXI50JMXoaxZTKJ7";
    private static  Map<String, Firewall> firewallConfigMap = new HashMap<>();
    private static  String configFirewallId ;
    private static Config config = Config.newConfig().withSSLVerificationDisabled();
    private static Map<String,String> userConfig = new HashMap<>(16);

    @PostConstruct
    public void init(){
        Firewall fireWallConfig = new Firewall();

        fireWallConfig.setUser(JaspytUtils.decyptPwd(secretKey, firewallUser));
        fireWallConfig.setPasswd(JaspytUtils.decyptPwd(secretKey, firewallPasswd));
        fireWallConfig.setIp(firewallIp);
        fireWallConfig.setPort(firewallPort);
        configFirewallId = firewallId;
        firewallConfigMap.put(firewallId, fireWallConfig);
        log.error("firewall init,id:{} ip:{}, ", firewallId, firewallIp);
    }

    public static Firewall getFireWallById(String id) {
        if (!firewallConfigMap.containsKey(id)) {
            log.error("===============================Can not get firewall by id:{}", id);
            return firewallConfigMap.get(configFirewallId);
        }
        return firewallConfigMap.get(id);
    }
    public static List<Firewall> getAllFireWall() {
        Collection<Firewall> firewalls = firewallConfigMap.values();
        Iterator it =  firewalls.iterator();
        List<Firewall> firewallList = new ArrayList<>();
        for(;it.hasNext();){
            firewallList.add((Firewall)it.next());
        }
        return  firewallList;
    }
    //    Greenwich mean time
    public static Date getGmtDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new Date();
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
            if (!param.getChargeMode().equalsIgnoreCase(HsConstants.BANDWIDTH) && !param.getChargeMode().equals(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH)) {
                errorMsg = errorMsg + "Only Bandwidth,SharedBandwidth is allowed. ";
            }
            if (param.getChargeMode().equals(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH) && (null == param.getSbwId())) {
                errorMsg = errorMsg + "SharedBandwidth id is needed in SharedBandwidth charge mode and sbwId not null ";
            }
        }

        if (null != param.getBillType()) {
            if (!param.getBillType().equals(HsConstants.MONTHLY)
                    && !param.getBillType().equals(HsConstants.HOURLYSETTLEMENT)
                    && !HsConstants.HOURLYNETFLOW.equals(param.getBillType())) {
                errorMsg = errorMsg + "Only monthly,hourlySettlement,hourlyNetflow is allowed. ";
            }
        }
        if (param.getRegion().isEmpty()) {
            errorMsg = errorMsg + "can not be blank.";
        }
        String tp = param.getIpType();
        if (null != tp) {
            if (!tp.equals("mobile") && !tp.equals("radiotv") && !tp.equals("telecom") &&
                    !tp.equals("unicom") && !tp.equals("BGP")) {
                errorMsg = errorMsg + "Only mobile,radiotv, telecom, unicom ,  BGP is allowed. ";
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
        String errorMsg = "";
        if(null == param){
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,"Failed to get param.");
        }
        if((5 > param.getBandwidth()) || (param.getBandwidth() > 500)){
            errorMsg = "value must be 5-500.";
        }

        if(null != param.getBillType()) {
            if (!param.getBillType().equals(HsConstants.MONTHLY) && !param.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                errorMsg = errorMsg + "Only monthy,hourlySettlement, is allowed.";
            }
        }
        if (null != param.getIpType()){
            if (!ipType.contains(param.getIpType())){
                errorMsg = errorMsg + "Only mobile,radiotv, telecom, unicom ,  BGP is allowed.";
            }
        }
        if(param.getRegion().isEmpty()){
            errorMsg = errorMsg + "can not be blank.";
        }
        if(errorMsg.equals("")) {
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


    public static String getOSClientProjectId(String userRegion,  OSClient.OSClientV3 os) throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            log.info("can't get token, use default project admin 140785795de64945b02363661eb9e769");
            return userConfig.get("projectIdS");
        }else{
            try{
//                OSClientV3 os= getOsClientV3Util(userRegion);
                String projectid_client=os.getToken().getProject().getId();
                log.info("get OSClient ProjectId:{}", projectid_client);
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

        String token = getKeycloackToken();
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
        String projectIdInToken=null;
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        if(jsonObject.has("project_id")){
            projectIdInToken = (String) jsonObject.get("project_id");
        } else {
            projectIdInToken = (String) jsonObject.get("sub");
        }
        if(projectIdInToken != null && projectIdInToken.equals(projectId)){
            return true;
        }

        log.error("User has no right to operation.{}", jsonObject.toString());
        return false;

    }

    public static boolean verifyToken(String token, String projectIdInDb){
        String projectIdInToken = null;
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        if(jsonObject.has("project_id")){
            projectIdInToken = (String) jsonObject.get("project_id");
        } else {
            projectIdInToken = (String) jsonObject.get("sub");
        }
        if(projectIdInToken != null && projectIdInDb.equals(projectIdInToken)){
            log.debug("project_id:{}", projectIdInToken);
            return true;
        }
        log.error("request projectId:{} ,DB projectId:{}",projectIdInToken, projectIdInDb);
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
            log.info("Not admin account.{}", jsonObject.toString());
            return false;
        }
    }

    public static boolean isParentOrChildAccount(String token) throws KeycloakTokenException {
        if(null == token){
            throw new KeycloakTokenException(CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_NULL));
        }
        org.json.JSONObject jsonObject = Base64Util.decodeUserInfo(token);
        if(jsonObject.has("project_id")){
            return false;
        } else {
            return true;
        }
    }


}
