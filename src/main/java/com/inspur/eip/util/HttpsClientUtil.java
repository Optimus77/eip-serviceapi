package com.inspur.eip.util;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsClientUtil {

	static boolean ignoreSSL = Boolean.TRUE;

	public static String doGet(String url, Map<String, String> param) {

		// 创建Httpclient对象
		CloseableHttpClient httpclient = getHttpsClient();

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			log.info("response status code: " + response.getStatusLine().getStatusCode());
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doGet(String url) {

		return doGet(url, null);
	}

	public static String doPost(String url, Map<String, String> param) {

		// 创建Httpclient对象
		CloseableHttpClient httpClient = getHttpsClient();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			log.info("response status code: " + response.getStatusLine().getStatusCode());
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultString;
	}
	   

	public static String doPost(String url) {

		return doPost(url, null);
	}


	    
	public static ReturnResult doPostJson(String url, Map<String, String> header, String json) {

		// 创建Httpclient对象
		CloseableHttpClient httpClient = getHttpsClient();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);

			if(null == header){
				header = getHeader();
			}

			Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			log.info("response status code: " + response.getStatusLine().getStatusCode());

			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			return ReturnResult.actionResult(resultString, response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return ReturnResult.actionFailed("Exception", HttpStatus.SC_INTERNAL_SERVER_ERROR);
	}

	private static Map<String,String> getHeader(){
		Map<String,String> header=new HashMap<String,String>();
		header.put("requestId", UUID.randomUUID().toString());
		header.put(HsConstants.AUTHORIZATION, CommonUtil.getKeycloackToken());
		header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
		return header;
	}

    /**
     * get HttpsClient
     *
     * @return org.apache.http.impl.client.CloseableHttpClient
     * @Exception
     * @auther: zhang_xiaoyu
     * @date: 2018/9/13 19:09
     */
    public static CloseableHttpClient getHttpsClient() {

        CloseableHttpClient httpClient;
        if (ignoreSSL) {
            //HTTPS request is handled by bypassing authentication
            SSLContext sslcontext = null;
            try {
                sslcontext = createIgnoreVerifySSL();
            } catch (NoSuchAlgorithmException e) {
                log.error("NoSuchAlgorithmException");
            } catch (KeyManagementException e) {
                log.error("KeyManagementException");
            }

            //Sets the objects corresponding to the socket link factory that the Protocol HTTP and HTTPS correspond to.
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext, new X509HostnameVerifier() {

                        @Override
                        public boolean verify(String paramString, SSLSession paramSSLSession) {
                            return true;
                        }

                        @Override
                        public void verify(String host, SSLSocket ssl) throws IOException {

                        }

                        @Override
                        public void verify(String host, X509Certificate cert) throws SSLException {

                        }

                        @Override
                        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

                        }
                    }))
                    .build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        } else {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    public static RestTemplate buildRestTemplate() {

        RestTemplate restTemplate = null;
        if (ignoreSSL) {
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectionRequestTimeout(300000);
            factory.setConnectTimeout(300000);
            factory.setReadTimeout(300000);
            // https
            SSLContextBuilder builder = new SSLContextBuilder();
            try {
                builder.loadTrustMaterial(null, (X509Certificate[] x509Certificates, String s) -> true);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            SSLConnectionSocketFactory socketFactory = null;
            try {
                socketFactory = new SSLConnectionSocketFactory(createIgnoreVerifySSL());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", socketFactory).build();
            PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager(registry);
            phccm.setMaxTotal(200);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).setConnectionManager(phccm).setConnectionManagerShared(true).build();
            factory.setHttpClient(httpClient);
            restTemplate = new RestTemplate(factory);
        } else {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }


}
