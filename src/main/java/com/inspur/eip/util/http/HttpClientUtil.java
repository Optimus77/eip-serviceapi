package com.inspur.eip.util.http;

import com.inspur.eip.entity.openapi.AwsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Scott Tian
 * @date 20180924
 * HttpClient util
 */
@Slf4j
public class HttpClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String RESULT = "the result is:===[";

    private static ConnectionManager connectionFactory = new ConnectionManager();

    private HttpClientUtil() {
        throw new IllegalStateException("HttpClientUtil class");
    }

    public static ResponseEntity doGet(String url, Map<String, String> param, Map<String, String> headParam) throws IOException, URISyntaxException {
        LOGGER.info("doget===URL=[" + url + "]");
        if (param != null) {
            LOGGER.info("doget===param=[" + param.toString() + "]");
        }
        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;
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
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    public static HttpResponse doGetWithHeaders(String url, Map<String, String> param, HttpHeaders httpHeaders) throws IOException, URISyntaxException {

        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        HttpResponse httpResponse = null;
        try {
            // create uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            URI uri = builder.build();
            // create http GET request
            HttpGet httpGet = new HttpGet(uri);
            if (httpHeaders != null) {
                for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue().get(0));
                }
            }
            // execute request
            response = httpClient.execute(httpGet);
            httpResponse = HttpResponse
                    .builder()
                    .responseBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8))
                    .statusCode(response.getStatusLine().getStatusCode())
                    .build();
            if (httpResponse.getStatusCode() >= 300) {
                LOGGER.info(RESULT + httpResponse.toString() + "]===");
            } else {
                LOGGER.info(RESULT + httpResponse.getStatusCode().toString() + "]===");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return httpResponse;
    }



    public static ResponseEntity doPost(String url, String body, Map<String, String> headParam) throws IOException, URISyntaxException {
        LOGGER.info("doPost===URL=[" + url + "]");
        if (body != null) {
            LOGGER.info("doPost===param=[" + body.toString() + "]");
        }
        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;
        try {
            // create Http Post request
            HttpPost httpPost = new HttpPost(url);
            if (body != null) {
//                List<NameValuePair> paramList = new ArrayList<>();
//                param.keySet().forEach(aParam -> paramList.add(new BasicNameValuePair(aParam, param.get(aParam))));
//                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            //add headers
            if (headParam != null) {
                headParam.keySet().forEach(aParam -> httpPost.addHeader(aParam, headParam.get(aParam)));
            }
            // execute http request
            response = httpClient.execute(httpPost);
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            log.info(AwsConstant.LOG_RESULT + responseEntity.getBody().toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    public static ResponseEntity doPost(String url, String param) throws IOException, URISyntaxException {
        return doPost(url, param, null);
    }

    public static HttpResponse doPostJson(String url, String json, Map<String, String> headParam) throws IOException {
        LOGGER.info("doPostJson===URL=[" + url + "]");
        if (json != null) {
            LOGGER.info("doPostJson===json=[" + json + "]");
        }
        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        HttpResponse httpResponse = null;
        try {
            // create Http Post request
            HttpPost httpPost = new HttpPost(url);
            // add request body
            if (json != null) {
                StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            //add headers
            if (headParam != null) {
                headParam.keySet().forEach(aParam -> httpPost.addHeader(aParam, headParam.get(aParam)));
            }
            // execute http request
            response = httpClient.execute(httpPost);
            // assemble return result
            httpResponse = HttpResponse
                    .builder()
                    .responseBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8))
                    .statusCode(response.getStatusLine().getStatusCode())
                    .build();
            if (httpResponse.getStatusCode() >= 300) {
                LOGGER.info(RESULT + httpResponse.toString() + "]===");
            } else {
                LOGGER.info(RESULT + httpResponse.getStatusCode().toString() + "]===");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return httpResponse;
    }

    public static HttpResponse doPostJson(String url, String json) throws IOException {
        return doPostJson(url, json, null);
    }

    public static HttpResponse doPut(String url, Map<String, String> param, Map<String, String> headParam) throws IOException {
        LOGGER.info("doPutJson===URL=[" + url + "]");
        if (param != null) {
            LOGGER.info("doPutJson===param=[" + param + "]");
        }
        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        HttpResponse httpResponse = null;
        try {
            // create Http Put request
            HttpPut httpPut = new HttpPut(url);
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                param.keySet().forEach(aParam -> paramList.add(new BasicNameValuePair(aParam, param.get(aParam))));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPut.setEntity(entity);
            }
            //add headers
            if (headParam != null) {
                headParam.keySet().forEach(aParam -> httpPut.addHeader(aParam, headParam.get(aParam)));
            }
            // execute http request
            response = httpClient.execute(httpPut);
            httpResponse = HttpResponse
                    .builder()
                    .responseBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8))
                    .statusCode(response.getStatusLine().getStatusCode())
                    .build();
            if (httpResponse.getStatusCode() >= 300) {
                LOGGER.info(RESULT + httpResponse.toString() + "]===");
            } else {
                LOGGER.info(RESULT + httpResponse.getStatusCode().toString() + "]===");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return httpResponse;
    }

    public static HttpResponse doPut(String url, Map<String, String> param) throws IOException {
        return doPut(url, param, null);
    }

    public static HttpResponse doPutJson(String url, String json, Map<String, String> headParam) throws IOException {
        LOGGER.info("doPutJson===URL=[" + url + "]");
        if (json != null) {
            LOGGER.info("doPutJson===json=[" + json + "]");
        }
        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        HttpResponse httpResponse = null;
        try {
            // create Http Put request
            HttpPut httpPut = new HttpPut(url);
            // add request body
            if (json != null) {
                StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
                httpPut.setEntity(entity);
            }
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);
            //add headers
            if (headParam != null) {
                headParam.keySet().forEach(aParam -> httpPut.addHeader(aParam, headParam.get(aParam)));
            }
            // execute http request
            response = httpClient.execute(httpPut);
            httpResponse = HttpResponse
                    .builder()
                    .responseBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8))
                    .statusCode(response.getStatusLine().getStatusCode())
                    .build();
            if (httpResponse.getStatusCode() >= 300) {
                LOGGER.info(RESULT + httpResponse.toString() + "]===");
            } else {
                LOGGER.info(RESULT + httpResponse.getStatusCode().toString() + "]===");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return httpResponse;
    }

    public static HttpResponse doPutJson(String url, String json) throws IOException {
        return doPutJson(url, json, null);
    }


    public static HttpResponse doDelete(String url, Map<String, String> param, Map<String, String> headParam) throws IOException, URISyntaxException {
        if (param != null) {
            LOGGER.info("doDelete===param=[" + param.toString() + "]");
        }
        // get Httpclient object
        CloseableHttpClient httpClient = connectionFactory.getHttpClient();
        CloseableHttpResponse response = null;
        HttpResponse httpResponse = null;
        try {
            // create uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                param.keySet().forEach(aParam -> builder.addParameter(aParam, param.get(aParam)));
            }
            URI uri = builder.build();
            // create http Delete request
            HttpDelete httpDelete = new HttpDelete(uri);
            //add headers
            if (headParam != null) {
                headParam.keySet().forEach(aParam -> httpDelete.addHeader(aParam, headParam.get(aParam)));
            }
            // execute request
            response = httpClient.execute(httpDelete);

            httpResponse = HttpResponse
                    .builder()
                    .responseBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8))
                    .statusCode(response.getStatusLine().getStatusCode())
                    .build();
            if (httpResponse.getStatusCode() >= 300) {
                LOGGER.info(RESULT + httpResponse.toString() + "]===");
            } else {
                LOGGER.info(RESULT + httpResponse.getStatusCode().toString() + "]===");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return httpResponse;
    }

    public static HttpResponse doDelete(String url, Map<String, String> param) throws IOException, URISyntaxException {
        return doDelete(url, param, null);
    }


//    public CloseableHttpClient getHttpClient() {
//
//        //最大连接数
//        int MAX_TOTAL = 100000;
//        // 每一个路由的最大连接数
//        int MAX_PER_ROUTE = 5000;
//        //从连接池中获得连接的超时时间
//        int CONNECTION_REQUEST_TIMEOUT = 30 * 60 * 1000;
//        //连接超时
//        int CONNECTION_TIMEOUT = 30 * 60 * 1000;
//        //获取数据的超时时间
//        int SOCKET_TIMEOUT = 30 * 60 * 1000;
//
//        PoolingHttpClientConnectionManager cm;
//
//        /**
//         * 重连接策略
//         */
//        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
//            // Do not retry if over max retry count
//            if (executionCount >= 3) {
//                LOGGER.error("[HttpClientUtil] : Over max retry count!", exception);
//                return false;
//            }
//            // Timeout
//            if (exception instanceof InterruptedIOException) {
//                LOGGER.error("[HttpClientUtil] : Timeout!", exception);
//                return false;
//            }
//            // Unknown host
//            if (exception instanceof UnknownHostException) {
//                LOGGER.error("[HttpClientUtil] : Unknown host!", exception);
//                return false;
//            }
//            // Connection refused
//            if (exception instanceof ConnectTimeoutException) {
//                LOGGER.error("[HttpClientUtil] : Connection refused!", exception);
//                return false;
//            }
//            // SSL handshake exception
//            if (exception instanceof SSLException) {
//                LOGGER.error("[HttpClientUtil] : SSL handshake exception!", exception);
//                return false;
//            }
//            HttpClientContext clientContext = HttpClientContext.adapt(context);
//            HttpRequest request = clientContext.getRequest();
//
//            // Retry if the request is considered idempotent
//            return !(request instanceof HttpEntityEnclosingRequest);
//        };
//
//        /**
//         * 配置连接参数
//         */
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
//                .setConnectTimeout(CONNECTION_TIMEOUT)
//                .setSocketTimeout(SOCKET_TIMEOUT)
//                .build();
//
//        SSLContext sslContext = null;
//        try {
//            sslContext = SSLContext.getInstance("SSLv3");
//            X509TrustManager trustManager = new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {
//                    //I don't know why this method is empty
//                }
//
//                @Override
//                public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {
//                    //I don't know why this method is empty
//                }
//
//                @Override
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            };
//            sslContext.init(null, new X509TrustManager[]{trustManager}, null);
//        } catch (NoSuchAlgorithmException | KeyManagementException e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        Registry<ConnectionSocketFactory> socketFactoryRegistry;
//        if (sslContext != null) {
//            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("http", PlainConnectionSocketFactory.INSTANCE)
//                    .register("https", new SSLConnectionSocketFactory(sslContext))
//                    .build();
//        } else {
//            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("http", PlainConnectionSocketFactory.INSTANCE)
//                    .build();
//        }
//
//        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//        cm.setMaxTotal(MAX_TOTAL);
//        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
//
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        cookieStore.addCookies(getCookies());
//
//        return HttpClients.custom()
//                .setConnectionManager(cm)
//                .setDefaultRequestConfig(requestConfig)
//                .setRetryHandler(retryHandler)
//                .setSSLContext(sslContext)
//                .setDefaultCookieStore(cookieStore)
//                .setSSLHostnameVerifier(new NoopHostnameVerifier())
//                .build();
//    }

    /**
     * 使用连接池来创建CloseableHttpClient
     * <p>
     * 20190701
     * 由于要对接iam不能再使用连接池了
     */
    public static class ConnectionManager {

        //最大连接数
        private static final int MAX_TOTAL = 100000;
        // 每一个路由的最大连接数
        private static final int MAX_PER_ROUTE = 5000;
        //从连接池中获得连接的超时时间
        private static final int CONNECTION_REQUEST_TIMEOUT = 30 * 60 * 1000;
        //连接超时
        private static final int CONNECTION_TIMEOUT = 30 * 60 * 1000;
        //获取数据的超时时间
        private static final int SOCKET_TIMEOUT = 30 * 60 * 1000;

        PoolingHttpClientConnectionManager cm;

        CloseableHttpClient httpClient;

        /**
         * 重连接策略
         */
        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
            // Do not retry if over max retry count
            if (executionCount >= 3) {
                LOGGER.error("[HttpClientUtil] : Over max retry count!", exception);
                return false;
            }
            // Timeout
            if (exception instanceof InterruptedIOException) {
                LOGGER.error("[HttpClientUtil] : Timeout!", exception);
                return false;
            }
            // Unknown host
            if (exception instanceof UnknownHostException) {
                LOGGER.error("[HttpClientUtil] : Unknown host!", exception);
                return false;
            }
            // Connection refused
            if (exception instanceof ConnectTimeoutException) {
                LOGGER.error("[HttpClientUtil] : Connection refused!", exception);
                return false;
            }
            // SSL handshake exception
            if (exception instanceof SSLException) {
                LOGGER.error("[HttpClientUtil] : SSL handshake exception!", exception);
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
                LOGGER.error("[HttpClientUtil] : NoSuchAlgorithmException!", e);
            } catch (KeyManagementException e) {
                LOGGER.error("[HttpClientUtil] : KeyManagementException!", e);
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

        public HttpClientConnectionManager getManager() {
            return cm;
        }
    }


    public static org.apache.http.impl.cookie.BasicClientCookie[] getCookies() {
        //important
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        javax.servlet.http.Cookie[] requestCookies = request.getCookies();
        org.apache.http.impl.cookie.BasicClientCookie[] basicClientCookies = new org.apache.http.impl.cookie.BasicClientCookie[requestCookies.length];
        for (int i = 0; i < requestCookies.length; i++) {
            basicClientCookies[i] = new org.apache.http.impl.cookie.BasicClientCookie(requestCookies[i].getName(), requestCookies[i].getValue());
        }
        return basicClientCookies;
    }

}
