package com.inspur.eip.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipAllocateParam;
import com.inspur.eip.entity.ReturnMsg;
import com.inspur.eip.entity.sbw.SbwAllocateParam;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class CommonUtil {

    public static boolean isDebug = true;


    public static String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    @Setter
    private static JSONObject KeyClockInfo;



    private static String authUrl = "https://10.110.25.117:5000/v3"; //endpoint Url
    private static String user = "admin";
    private static String password = "89rqdHLMN5rm0x1P";
    private static String projectId = "140785795de64945b02363661eb9e769";
    private static String userDomainId = "default";
    private static String region="RegionOne";
    private static String region1="cn-north-3";



    public static JSONObject getTokenInfo(){

        return KeyClockInfo;

    }

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


    /**
     * get the region info from httpHeader;
     * @return ret
     */
    //TODO region is not correct for now
    public static String getReginInfo()  {
        return region1;
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
        if((0== param.getBandwidth()) || (param.getBandwidth() > 2000)){
            errorMsg = "value must be 1-2000.";
        }
        if(null != param.getChargemode()) {
            if (!param.getChargemode().equalsIgnoreCase(HsConstants.BANDWIDTH) &&
                    !param.getChargemode().equals(HsConstants.SHAREDBANDWIDTH)) {
                errorMsg = errorMsg + "Only Bandwidth,SharedBandwidth is allowed. ";
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
            log.info(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_OK, errorMsg);
        }else {
            log.error(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,errorMsg);
        }
    }

    /**
     * sbw param check
     * @param param
     * @return
     */
    public static ReturnMsg preSbwCheckParam(SbwAllocateParam param){
        String errorMsg = " ";
        if(null == param){
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,"Failed to get param.");
        }
        if(param.getBandwidth()==0 && ((param.getBandwidth() > 2000)||param.getBandwidth()<5)){
            errorMsg = "value must be 5-2000.";
        }
        if(null != param.getChargemode()) {
            if (!param.getChargemode().equalsIgnoreCase(HsConstants.SHAREDBANDWIDTH)) {
                errorMsg = errorMsg + "Only SharedBandwidth is allowed. ";
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
        if(errorMsg.equals(" ")) {
            log.info(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_OK, errorMsg);
        }else {
            log.error(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,errorMsg);
        }
    }
}
