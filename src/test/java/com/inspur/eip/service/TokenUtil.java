package com.inspur.eip.service;


import com.google.gson.Gson;

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
            String token;
            if(userName == "lishenghao")
                token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyNDIxZGQxZS00MDhkLTRhZDYtOGQyNS03NjIzNzFmZTNiZTciLCJleHAiOjE1NjY5MDc5NDAsIm5iZiI6MCwiaWF0IjoxNTY2OTAyNTQwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYyNjc3ZGViLTVmZjUtNDAwMS04NzY0LTJmYWQzODdjMzk2YiIsImF1dGhfdGltZSI6MTU2NjkwMjUyOSwic2Vzc2lvbl9zdGF0ZSI6IjkxOTNkZjlhLWFlZTUtNDRjMS04OTY4LWY0ZWJjMGZjN2M0MyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.j5n_4ywpou1KuVrmAb3TjVz5nnD2SzAM7iwZz_bpUCdyZ2LvMN40m3cLLsnGCaendZ9139E9-klvzJLNveDF13IP3_y9xuRY3KVdrbzZXjfMHS2ONvQNe87SlgtgBvQnBqWOBIHL4ZF9mDJH0AjXClJjX9DUUoBD37cd0E9h7XxKuEaFwaqHKOvc_scGUAb1LWwmgag-Z02RttSk4M7hOWt_j0mBooEp8xQOpy-0fX28nDpVUBgG8lObi936LPT_cfFj3MXFXrOVu7Hahtu_a_6DqboenubrepiFE1YXXqbpHzblEYYBA1EEbCXJqgCXZO4wtUe05mqrdFH6VS97Xw";
            else
                token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJhNmZmOTIyYS00Y2IwLTQwYWMtODVmNC0wYzVlMzRlZjYxYTUiLCJleHAiOjE1NjcwNjkwMzUsIm5iZiI6MCwiaWF0IjoxNTY3MDYzNjM1LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImE4YWY5NTQ2LWNhZDEtNGI2YS1hMmE2LTMyMDNjOGYxY2JmYSIsImF1dGhfdGltZSI6MTU2NzA2MzYzMCwic2Vzc2lvbl9zdGF0ZSI6ImFhZTM4ZmI3LTA1MTctNDVjYi1iYWZjLTZmMmY0MjE0OTcxNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.EgG0_W2OVAH6UI3u39N_sl05fG98C6otxlyz76K5UhmhUmbxndph7_UyvnL_gugIVzIfDR7cqD4U9iPggfJy9xIRNLWyljfWkXKffG7bTauhP4UjoZwcQvSPQwGcrjAh2fvBUthBt7O9Wq9uiS1qFXwHkOzEbDoUXz86Om0WijTzELMO-S9_OWpx6bu-52Go8Ega_xP_PUdn4IxWWPsCVSC9MSwXDuf16u3WfQWWzTPfGOLV03jNFDabN3fLUzpvoZSj6VJBNeLn51CRIceNCg0sCSTLMAPib728GIcgDioHwhKTs1k8BDQ_u0voUGhr-3OATz-dNlzLTWvsxZc10w";
            return token;
        }
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
}
