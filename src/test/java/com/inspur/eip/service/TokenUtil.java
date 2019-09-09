package com.inspur.eip.service;


import com.google.gson.Gson;
import com.inspur.eip.util.TrustAnyHostnameVerifier;
import com.inspur.eip.util.TrustAnyTrustManager;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
    private static final Object POST = "POST";

    public static String getToken(String userName, String passWord) throws Exception {

        try {
            String postMessage = post(userName, passWord);
            Gson gson = new Gson();
            HashMap<String, String> map = new HashMap<>();
            map = gson.fromJson(postMessage, map.getClass());
            String token = map.get("access_token");
            return token;
        } catch (Exception e) {
            String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJmZDM1NGNmNC01YmQ4LTRlOTEtYjk1Zi01ZTEyZGRiNDkyYzYiLCJleHAiOjE1Njc2NTk0NjgsIm5iZiI6MCwiaWF0IjoxNTY3NjU0MDY4LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImlhYXMtc2VydmVyIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYjEwNTNhMGItMDIzYS00ZDA1LWI3NmItNjBjNThmOGQ4NTM4IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJBQ0NPVU5UX0FETUlOIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfSwicmRzLW15c3FsLWFwaSI6eyJyb2xlcyI6WyJ1c2VyIl19fSwic2NvcGUiOiIiLCJwcm9qZWN0IjoibGlzaGVuZ2hhbyIsImdyb3VwcyI6WyIvZ3JvdXAtbGlzaGVuZ2hhbyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsaXNoZW5naGFvIiwiZW1haWwiOiJsaXNoZW5naGFvQGluc3B1ci5jb20ifQ.moVKbtzVGaEH_b3anCS3jHvvX8hJPVdofx1WWFyKljYIK9VwkiYZxKsnelJyFVAKBon3BXE0jCkl3DVzBIE3mB_R_f_s5dtF_JCouFUEx03XgePoia-YcFwSlOaImbffPjVyOj1z4x9XNsfI87J_a2lD2NO0K4o0LwvqTJDmU2NdVpqFF5gHs3NfkvoLVHh2InesfYFwNmsZ7kZX1IwwN9wl-kDAI-7byRIGHL-SZjMBaN5VPOkfBgPvFGqkMOyJtlG5bojbnQXXFfPo1aevAvKThuhIuxTYnaK-NGTjLc-wqEYobPSWIcOAhMfNW5fjBUauOVmCZSGmzLlhfnmXfg";
            return token;
        }
    }

    public static String post(String userName, String passWord) throws Exception{
        String strURL = "https://iopdev.10.110.25.123.xip.io/auth/realms/picp/protocol/openid-connect/token";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("grant_type", "password");
        paramMap.put("username", userName);
        paramMap.put("password", passWord);
        paramMap.put("client_id", "iaas-server");
        paramMap.put("client_secret", "3da8fb1c-97d7-4627-8c4c-b002942e820f");
        paramMap.put("response_type", "code id_token");
        HttpsURLConnection conn = null;
        PrintWriter writer = null;
        try{
            SSLContext sc = SSLContext.getInstance("SSL","SunJSSE");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager()},
                    new java.security.SecureRandom());
            URL url = new URL(strURL);
            String param = getParamString(paramMap);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
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
        conn.setInstanceFollowRedirects(false);
        conn.setUseCaches(false);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        if(null!=requestMethod && POST.equals(requestMethod)){
            conn.setDoOutput(true);
            conn.setDoInput(true);
        }
    }


}
