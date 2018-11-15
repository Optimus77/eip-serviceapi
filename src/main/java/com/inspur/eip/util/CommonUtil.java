package com.inspur.eip.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Setter;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class CommonUtil {

    public final static Logger log = LoggerFactory.getLogger(CommonUtil.class);
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
     * @throws Exception e
     */
    //TODO region is not correct for now
    public static String getReginInfo() throws Exception {
        //ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
//        if(null != requestAttributes) {
//            HttpServletRequest request = requestAttributes.getRequest();
//            String regionName = QueryUtil.getEndpoint(request);
//            log.info("regionName"+regionName);
//            if(regionName==null){
//                throw new Exception("get region fail");
//            }
//            return regionName;
//        }else{
//            throw new Exception("get region error");
//        }
        return region1;

    }

    public static String getUserId()throws KeycloakTokenException {

        String token = getKeycloackToken();
        if(null == token){
            throw new KeycloakTokenException("400-Bad request:can't get Authorization info from header,please check");
        }else{
            JSONObject jsonObject = decodeUserInfo(token);
            String sub = (String) jsonObject.get("sub");
            if(sub!=null){
                log.info("getUserId:{}", sub);
                return sub;
            }else{
                throw new KeycloakTokenException("400-Bad request:can't get user info from header,please check");
            }
        }
    }

    public static JSONObject decodeUserInfo(String keycloakToken) {
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

        StringBuilder sb = new StringBuilder("");
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
            String username = (String) jsonObject.get("preferred_username");
            if(username!=null){
                log.info("getUsername:{}", username);
                return username;
            }else{
                throw new KeycloakTokenException("400-Bad request:can't get user info from header,please check");
            }
        }
    }

    public static JSONObject handlerResopnse(HttpResponse response){

        StringBuffer sb= new StringBuffer();
        if(response!=null) {
            StatusLine status = response.getStatusLine();
            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                JSONObject returnInfo = JSONObject.parseObject(sb.toString());
                log.info("RETURN ==>{}", returnInfo);
                returnInfo.put("statusCode", status.getStatusCode());
                return returnInfo;
            }catch (Exception e){
                log.error("handlerResopnse exception:", e);
            }
        }
        JSONObject result=new JSONObject();
        result.put(HsConstants.SUCCESS,false);
        result.put("message","no return from.");
        return result;
    }
}
