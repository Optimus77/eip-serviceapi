package com.inspur.eip.util;


import com.alibaba.fastjson.JSONObject;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
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
    private static String region1="cn-north-3a";



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
            log.info(keyCloackToken);
            if (keyCloackToken == null) {
                return null;
            } else {
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

    public static String getUserId(){
        return "useridnotfound";
    }
}
