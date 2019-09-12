package com.inspur.eip.util.http;

import com.inspur.eip.exception.EipException;
import com.inspur.eip.util.ReturnResult;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class HttpsClientUtilTest {
    @Before
    public void setUp(){

    }
    @Test
    public void doPostJson() throws EipException {
        Map<String,String> map = new HashMap<>();
        map.put("key","value");
        ReturnResult result =HttpsClientUtil.doPostJson("http://www.ietf.org",map,"json");
    }
    @Test
    public void getHeader()  {
        Map<String,String> result = HttpsClientUtil.getHeader();
    }
    @Test
    public void getHttpsClient()  {
        CloseableHttpClient result = HttpsClientUtil.getHttpsClient();
    }
    @Test
    public void createIgnoreVerifySSL() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext result = HttpsClientUtil.createIgnoreVerifySSL();
    }
}
