//package com.inspur.eip.util.http;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.io.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class HsHttpClientTest {
//
//    @Before
//    public void setUp(){
//        Map<String, String> cookieMap = new HashMap<>();
//        cookieMap.put("abc","def");
//        ReflectionTestUtils.setField(HsHttpClient.class,"cookieMap",cookieMap);
//    }
//
//    @Test
//    public void ConvertStreamToString() throws FileNotFoundException {
//        InputStream inputStream2 = null;
//        File file   = new File("D:\\PublicCloud\\eip-serviceapi\\src\\main\\java\\com\\inspur\\eip\\util\\MonitorScheduledTask.java");
//        inputStream2 = new FileInputStream(file);
//        String result = HsHttpClient.ConvertStreamToString(inputStream2);
//    }
//
//    @Test
//    public void hsHttpGet() throws Exception {
//        HsHttpClient.hsHttpGet("11","22","33","44","55");
//    }
//}
