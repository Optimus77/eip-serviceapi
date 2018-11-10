package com.inspur.eip.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class HttpUtil {

    private final static Logger log = LoggerFactory.getLogger(HttpUtil.class);


    protected static HttpClient getCloseableHttpClient() throws Exception {
        try {
            return  HttpClients.createDefault();
        } catch (Exception e) {
            log.error("getCloseableHttpClient error", e);
            throw  new Exception("et the clientBuilder from bean error");
        }

    }

    public static HttpResponse get(String url, Map<String,String > header){
        HttpGet httpGet = new HttpGet(url.toString());

        Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        try {
            HttpResponse httpResponse = getCloseableHttpClient().execute(httpGet);
            return httpResponse;
        } catch (Exception e) {
            log.error("http get error:",e);
        }
        return null;
    }

    public static HttpResponse post(String url, Map<String,String > header, String body ) {
        HttpClient client;
        try {
            client = getCloseableHttpClient();
            HttpPost httpPost = new HttpPost(url.toString());
            Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                httpPost.setHeader(entry.getKey(),entry.getValue());
            }
            log.debug("request line:post-{} " ,httpPost.getRequestLine());
            StringEntity entity = new StringEntity(body, HTTP.UTF_8);
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            return client.execute(httpPost);
        } catch (Exception e) {
            log.error("IO Exception when post.{}",e.getMessage());
            return null;
        }

    }



}
