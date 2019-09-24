package com.inspur.eip.util.http;

import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.config.proxy.httppool.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtilTest {

    @Mock
    private CloseableHttpClient closeableHttpClient;

    @Before
    public void setUp(){
//        ReflectionTestUtils.setField(httpClientUtil,"LOGGER", LoggerFactory.getLogger(HttpClientUtil.class));
//        ReflectionTestUtils.setField(httpClientUtil,"RESULT", "the result is:===[");
//        ReflectionTestUtils.setField(httpClientUtil,"connectionFactory", new HttpClientUtil.ConnectionManager());

    }

    @Test
    public void doGet() throws IOException, URISyntaxException {
        try {
            Map<String,String> map = new HashMap<>();
            Map<String,String> map2 = new HashMap<>();
            ResponseEntity responseEntity = HttpClientUtil.doGet("http://www.ietf.org//rfc",map,map2);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void doGetWithHeaders() throws IOException, URISyntaxException {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            Map<String,String> map = new HashMap<>();
            HttpResponse httpResponse = HttpClientUtil.doGetWithHeaders("http://www.ietf.org//rfc",map,httpHeaders);
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void doPost() throws IOException, URISyntaxException {
        try {
            Map<String,String> map = new HashMap<>();
            ResponseEntity responseEntity = HttpClientUtil.doPost("http://www.ietf.org//rfc","22",map);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void doPostJson(){
        try {
            Map<String,String> map = new HashMap<>();
            HttpResponse httpResponse = HttpClientUtil.doPostJson("http://www.ietf.org//rfc","22",map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void doPut() throws IOException {
        try {
            Map<String,String> map = new HashMap<>();
            Map<String,String> map2 = new HashMap<>();
            HttpResponse httpResponse = HttpClientUtil.doPut("http://www.ietf.org//rfc",map,map2);

        }catch (Exception e){
            e.printStackTrace();
        }
//                Assert.assertEquals((long)httpResponse.getStatusCode(),302);

    }

    @Test
    public void doPutJson() throws IOException {
        Map<String,String> map = new HashMap<>();
        HttpResponse httpResponse = HttpClientUtil.doPutJson("http://www.ietf.org//rfc","123",map);
    }

    @Test
    public void doDelete() throws IOException, URISyntaxException {
        try {
            Map<String,String> map = new HashMap<>();
            Map<String,String> map2 = new HashMap<>();
            HttpResponse httpResponse = HttpClientUtil.doDelete("http://www.ietf.org//rfc",map,map2);

        }catch ( Exception e){
            e.printStackTrace();
        }
        //        Assert.assertEquals(httpResponse.statusCode,302);
//        Assert.assertEquals((long)httpResponse.getStatusCode(),302);
    }
}
