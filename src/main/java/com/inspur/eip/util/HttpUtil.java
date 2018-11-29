package com.inspur.eip.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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



    private static Map<String,String> getHeader(){
        Map<String,String> header=new HashMap<String,String>();
        header.put("requestId", UUID.randomUUID().toString());
        header.put(HsConstants.AUTHORIZATION, CommonUtil.getKeycloackToken());
        header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
        return header;
    }

    public static ReturnResult get(String url, Map<String,String > header) throws Exception{
        HttpGet httpGet = new HttpGet(url.toString());

        if(null == header){
            header = getHeader();
        }

        Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        try {
            HttpResponse httpResponse = getCloseableHttpClient().execute(httpGet);
            String resultString = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            log.info("return:{}", resultString);
            return ReturnResult.actionResult(resultString, httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("http get error:",e);
        }
        throw new EipException("Get request use http error.", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    public static ReturnResult post(String url, Map<String,String > header, String body ) throws Exception {
        HttpClient client;

        if(null == header){
            header = getHeader();
        }

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
            HttpResponse httpResponse = client.execute(httpPost);
            String resultString = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            log.info("return:{}", resultString);
            return ReturnResult.actionResult(resultString, httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("IO Exception when post.{}",e.getMessage());
        }
        throw new EipException("Post request throw https error.", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    public static ReturnResult delete(String url, Map<String,String > header) throws Exception{
        HttpDelete httpDelete = new HttpDelete(url.toString());

        if(null == header){
            header = getHeader();
        }

        Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            httpDelete.setHeader(entry.getKey(),entry.getValue());
        }
        try {
            HttpResponse httpResponse = getCloseableHttpClient().execute(httpDelete);
            String resultString = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            log.info("return:{}", resultString);
            return ReturnResult.actionResult(resultString, httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("http get error:",e);
        }
        throw new EipException("Post request throw https error.", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    public static ReturnResult put(String url, Map<String,String > header, String body ) {
        HttpClient client;

        if(null == header){
            header = getHeader();
        }

        try {

            client = getCloseableHttpClient();
            HttpPut httpPut = new HttpPut(url.toString());
            Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                httpPut.setHeader(entry.getKey(),entry.getValue());
            }
            log.debug("request line:put-{} " ,httpPut.getRequestLine());
            StringEntity entity = new StringEntity(body, HTTP.UTF_8);
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            httpPut.setEntity(entity);
            HttpResponse httpResponse = client.execute(httpPut);
            String resultString = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            log.info("return:{}", resultString);
            return ReturnResult.actionResult(resultString, httpResponse.getStatusLine().getStatusCode());

        } catch (Exception e) {
            log.error("IO Exception when post.{}",e.getMessage());
            return null;
        }

    }

}
