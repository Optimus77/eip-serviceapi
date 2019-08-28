package com.inspur.eip.util.http;

import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.exception.EipException;
import com.inspur.eip.util.ReturnResult;
import com.inspur.eip.util.constant.HsConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class HttpUtil {


    private static HttpClient getCloseableHttpClient() throws Exception {
        try {
            return  HttpClients.createDefault();
        } catch (Exception e) {
            log.error("getCloseableHttpClient error", e);
            throw  new Exception("et the clientBuilder from bean error");
        }

    }



    private static Map<String,String> getHeader(){
        Map<String,String> header=new HashMap<>();
        header.put("requestId", UUID.randomUUID().toString());
        header.put(HsConstants.AUTHORIZATION, CommonUtil.getKeycloackToken());
        header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
        return header;
    }

    public static ReturnResult get(String url, Map<String,String > header) throws Exception{
        HttpGet httpGet = new HttpGet(url);

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000)
                .setSocketTimeout(10000).setConnectTimeout(10000).build();
        httpGet.setConfig(requestConfig);
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
            HttpPost httpPost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000)
                    .setSocketTimeout(10000).setConnectTimeout(10000).build();
            httpPost.setConfig(requestConfig);

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
            return ReturnResult.actionResult(resultString, httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("IO Exception when post.{}",e.getMessage());
        }
        throw new EipException("Post request throw https error.", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }


    public static String postIam(String url, Map<String,String > header, String body ) throws Exception {
        HttpClient client;

        if(null == header){
            header = getHeader();
        }

        try {
            client = getCloseableHttpClient();
            HttpPost httpPost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000)
                    .setSocketTimeout(10000).setConnectTimeout(10000).build();
            httpPost.setConfig(requestConfig);

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
            //return httpResponse;
            String resultString = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            return resultString;
        } catch (Exception e) {
            log.error("IO Exception when post.{}",e.getMessage());
        }
        throw new EipException("Post request throw https error.", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }


    public static ReturnResult delete(String url, Map<String,String > header) throws Exception{
        HttpDelete httpDelete = new HttpDelete(url);

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000)
                .setSocketTimeout(10000).setConnectTimeout(10000).build();
        httpDelete.setConfig(requestConfig);
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
            HttpPut httpPut = new HttpPut(url);

            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000)
                    .setSocketTimeout(10000).setConnectTimeout(10000).build();
            httpPut.setConfig(requestConfig);

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


    private static HttpUtil.ConnectionManager connectionFactory = new HttpUtil.ConnectionManager();




    /**
     * 使用连接池来创建CloseableHttpClient
     */
    public static class ConnectionManager {

        //最大连接数
        private static final int MAX_TOTAL = 10000;
        // 每一个路由的最大连接数
        private static final int MAX_PER_ROUTE = 500;
        //从连接池中获得连接的超时时间
        private static final int CONNECTION_REQUEST_TIMEOUT = 60000;
        //连接超时
        private static final int CONNECTION_TIMEOUT = 80000;
        //获取数据的超时时间
        private static final int SOCKET_TIMEOUT = 120000;

        PoolingHttpClientConnectionManager cm;
        CloseableHttpClient httpClient;

        /**
         * 重连接策略
         */
        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
            // Do not retry if over max retry count
            if (executionCount >= 3) {
                log.error("[HttpClientUtil] : Over max retry count!", exception);
                return false;
            }
            // Timeout
            if (exception instanceof InterruptedIOException) {
                log.error("[HttpClientUtil] : Timeout!", exception);
                return false;
            }
            // Unknown host
            if (exception instanceof UnknownHostException) {
                log.error("[HttpClientUtil] : Unknown host!", exception);
                return false;
            }
            // Connection refused
            if (exception instanceof ConnectTimeoutException) {
                log.error("[HttpClientUtil] : Connection refused!", exception);
                return false;
            }
            // SSL handshake exception
            if (exception instanceof SSLException) {
                log.error("[HttpClientUtil] : SSL handshake exception!", exception);
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();

            // Retry if the request is considered idempotent
            return !(request instanceof HttpEntityEnclosingRequest);
        };


        /**
         * 配置连接参数
         */
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();

        public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
            //TLS
            SSLContext sc = SSLContext.getInstance("SSLv3");
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                    //I don't know why this method is empty
                }

                @Override
                public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                    //I don't know why this method is empty
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            sc.init(null, new X509TrustManager[]{trustManager}, null);
            return sc;
        }

        ConnectionManager() {
            SSLContext sslContext = null;
            try {
                sslContext = createIgnoreVerifySSL();
            } catch (NoSuchAlgorithmException e) {
                log.error("[HttpClientUtil] : NoSuchAlgorithmException!", e);
            } catch (KeyManagementException e) {
                log.error("[HttpClientUtil] : KeyManagementException!", e);
            }
            Registry<ConnectionSocketFactory> socketFactoryRegistry;
            if (sslContext != null) {
                socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslContext))
                        .build();
            } else {
                socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .build();
            }

            cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            cm.setMaxTotal(MAX_TOTAL);
            cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);


            // 定制实现HttpClient，全局只有一个HttpClient
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setDefaultRequestConfig(requestConfig)
                    .setRetryHandler(retryHandler)
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build();

        }

        CloseableHttpClient getHttpClient() {
            return httpClient;
        }

    }

    public static com.inspur.eip.util.http.HttpResponse doGet(String url, Map<String, String> param, Map<String, String> headParam) throws IOException, URISyntaxException {

        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        com.inspur.eip.util.http.HttpResponse httpResponse = null;
        try {
            // create uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                param.keySet().forEach(aParam -> builder.addParameter(aParam, param.get(aParam)));
            }
            URI uri = builder.build();
            // create http GET request
            HttpGet httpGet = new HttpGet(uri);
            if (headParam != null) {
                headParam.keySet().forEach(aParam -> httpGet.addHeader(aParam, headParam.get(aParam)));
            }
            // execute request
            response = httpClient.execute(httpGet);
            httpResponse = com.inspur.eip.util.http.HttpResponse
                    .builder()
                    .responseBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8))
                    .statusCode(response.getStatusLine().getStatusCode())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return httpResponse;
    }

}
