package com.inspur.eip.util.http;

import com.inspur.eip.entity.openapi.AwsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
public class HttpClientUtilOrder {

    private HttpClientUtilOrder() {
    }

    public static ResponseEntity doGet(String url, Map<String, String> param, Map<String, String> head) {
        log.info("GET: " + url);
        if (param != null) {
            log.info(AwsConstant.LOG_PARAM + param.toString());
        }
        if (head != null) {
            log.info(AwsConstant.LOG_HEAD + head.toString());
        }
        CloseableHttpClient httpClient = clientBuilder.build();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                param.keySet().forEach(aParam -> builder.addParameter(aParam, param.get(aParam)));
            }
            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            if (head != null) {
                head.keySet().forEach(aParam -> httpGet.addHeader(aParam, head.get(aParam)));
            }
            response = httpClient.execute(httpGet);
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            log.info(AwsConstant.LOG_RESULT + responseEntity.getBody().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    public static ResponseEntity doPost(String url, String body, Map<String, String> head) {
        log.info("POST: " + url);
        if (body != null) {
            log.info(AwsConstant.LOG_BODY + body);
        }
        if (null != head) {
            log.info(AwsConstant.LOG_HEAD + head.toString());
        }
        CloseableHttpClient httpClient = clientBuilder.build();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            if (head != null) {
                head.keySet().forEach(aParam -> httpPost.addHeader(aParam, head.get(aParam)));
            }
            if (body != null) {
                StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            response = httpClient.execute(httpPost);
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            log.info(AwsConstant.LOG_RESULT + responseEntity.getBody().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    public static ResponseEntity doPostForm(String url, Map<String, String> param, Map<String, String> head) {
        log.info("POST: " + url);
        if (param != null) {
            log.info(AwsConstant.LOG_PARAM + param.toString());
        }
        if (head != null) {
            log.info(AwsConstant.LOG_HEAD + head.toString());
        }
        CloseableHttpClient httpClient = clientBuilder.build();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            if (head != null) {
                head.keySet().forEach(aParam -> httpPost.addHeader(aParam, head.get(aParam)));
            }
            List<NameValuePair> paramList = new ArrayList<>();
            if (param != null && param.size() > 0) {
                Set<String> keySet = param.keySet();
                for (String key : keySet) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8));
            response = httpClient.execute(httpPost);
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            log.info(AwsConstant.LOG_RESULT + responseEntity.getBody().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    public static ResponseEntity doPut(String url, String body, Map<String, String> head) {
        log.info("PUT: " + url);
        if (body != null) {
            log.info(AwsConstant.LOG_BODY + body);
        }
        if (null != head) {
            log.info(AwsConstant.LOG_HEAD + head.toString());
        }
        CloseableHttpClient httpClient = clientBuilder.build();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;
        try {
            HttpPut httpPut = new HttpPut(url);
            if (head != null) {
                head.keySet().forEach(aParam -> httpPut.addHeader(aParam, head.get(aParam)));
            }
            if (body != null) {
                StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpPut.setEntity(entity);
            }
            response = httpClient.execute(httpPut);
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            log.info(AwsConstant.LOG_RESULT + responseEntity.getBody().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    public static ResponseEntity doDelete(String url, String body, Map<String, String> head) {
        log.info("DELETE: " + url);
        if (body != null) {
            log.info(AwsConstant.LOG_BODY + body);
        }
        if (null != head) {
            log.info(AwsConstant.LOG_HEAD + head.toString());
        }
        CloseableHttpClient httpClient = clientBuilder.build();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(uri);
            if (head != null) {
                head.keySet().forEach(aParam -> httpDelete.addHeader(aParam, head.get(aParam)));
            }
            if(body != null) {
                StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpDelete.setEntity(entity);
            }
            response = httpClient.execute(httpDelete);
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            log.info(AwsConstant.LOG_RESULT + responseEntity.getBody().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    public static ResponseEntity doPatch(String url, String body, Map<String, String> head) {
        log.info("PATCH: " + url);
        if (body != null) {
            log.info(AwsConstant.LOG_BODY + body);
        }
        if (null != head) {
            log.info(AwsConstant.LOG_HEAD + head.toString());
        }
        CloseableHttpClient httpClient = clientBuilder.build();
        CloseableHttpResponse response = null;
        ResponseEntity responseEntity = null;
        try {
            HttpPatch httpPatch = new HttpPatch(url);
            if (head != null) {
                head.keySet().forEach(aParam -> httpPatch.addHeader(aParam, head.get(aParam)));
            }
            if (body != null) {
                StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpPatch.setEntity(entity);
            }
            response = httpClient.execute(httpPatch);
            responseEntity = new ResponseEntity<>((EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)), HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            log.info(AwsConstant.LOG_RESULT + responseEntity.getBody().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseEntity;
    }

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(1000 * 60 * 30)
            .setSocketTimeout(1000 * 60 * 60)
            .setConnectionRequestTimeout(1000 * 60 * 30)
            .setCookieSpec(CookieSpecs.STANDARD_STRICT)
            .setExpectContinueEnabled(true)
            .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
            .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
            .build();

    //https
    private static SSLConnectionSocketFactory socketFactory;
    private static TrustManager manager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            //ssl
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            //ssl
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    private static void enableSSL() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{manager}, null);
            socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static HttpRequestRetryHandler myRetryHandler = (exception, executionCount, context) -> false;

    static {
        enableSSL();
    }

    private static Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
            .<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", socketFactory)
            .build();

    private static PoolingHttpClientConnectionManager pccm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

    static {
        pccm.setMaxTotal(100); // 连接池最大并发连接数
        pccm.setDefaultMaxPerRoute(20); // 单路由最大并发数
    }

    private static HttpClientBuilder clientBuilder = HttpClients
            .custom()
            .setConnectionManager(pccm)
            .setConnectionManagerShared(true)
            .setRetryHandler(myRetryHandler)
            .setDefaultRequestConfig(requestConfig);
}
