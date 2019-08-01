package com.eipserviceapi.unitTest;


import com.google.gson.Gson;
import net.minidev.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
    private static final Object POST = "POST";

    public static String getToken(String userName, String passWord) throws Exception {
        String postMessage = post(userName, passWord);
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map = gson.fromJson(postMessage,map.getClass());
        String token = map.get("access_token");
        return token;
    }

    public static String post(String userName, String passWord) throws Exception{
        String strURL = "http://iopdev.10.110.25.123.xip.io/auth/realms/picp/protocol/openid-connect/token";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("grant_type", "password");
        paramMap.put("username", userName);
        paramMap.put("password", passWord);
        paramMap.put("client_id", "iaas-server");
        paramMap.put("client_secret", "3da8fb1c-97d7-4627-8c4c-b002942e820f");
        paramMap.put("response_type", "code id_token");
        HttpURLConnection conn = null;
        PrintWriter writer = null;
        try{
            URL url = new URL(strURL);
            String param = getParamString(paramMap);
            conn = (HttpURLConnection) url.openConnection();
            setHttpUrlConnection(conn, (String) POST);
            conn.connect();
            writer = new PrintWriter(conn.getOutputStream());
            writer.print(param);
            writer.flush();
            return readResponseContent(conn.getInputStream());
        }finally{
            if(null!=conn) conn.disconnect();
            if(null!=writer) writer.close();
        }
    }
    private static String getParamString(Map<String, String> paramMap){
        if(null==paramMap || paramMap.isEmpty()){
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for(String key : paramMap.keySet() ){
            builder.append("&")
                    .append(key).append("=").append(paramMap.get(key));
        }
        return builder.deleteCharAt(0).toString();
    }

    private static String readResponseContent(InputStream in) throws IOException {
        Reader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            reader = new InputStreamReader(in);
            char[] buffer = new char[1024];
            int head = 0;
            while( (head=reader.read(buffer))>0 ){
                content.append(new String(buffer, 0, head));
            }
            return content.toString();
        }finally{
            if(null!=in) in.close();
            if(null!=reader) reader.close();
        }
    }

    private static void setHttpUrlConnection(HttpURLConnection conn, String requestMethod) throws ProtocolException {
        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        conn.setRequestProperty("Proxy-Connection", "Keep-Alive");
        if(null!=requestMethod && POST.equals(requestMethod)){
            conn.setDoOutput(true);
            conn.setDoInput(true);
        }
    }

//    public static void main(String[] args) throws Exception {
//        String token = getToken("lishenghao", "1qaz2wsx3edc");
//        System.out.println(token);
//    }
}
