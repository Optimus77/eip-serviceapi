package com.inspur.eip.util.http;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class HttpClientUtilOrderTest {
    @Before
    public void setUp(){

    }

    @Test
    public void doGet(){
        Map<String,String> map = new HashMap<>();
        Map<String,String> map2 = new HashMap<>();
        ResponseEntity responseEntity = HttpClientUtilOrder.doGet("http://www.ietf.org//rfc",map,map2);
    }

    @Test
    public void doPost(){
        Map<String,String> map = new HashMap<>();
        ResponseEntity responseEntity = HttpClientUtilOrder.doPost("http://www.ietf.org//rfc","body",map);
    }
    @Test
    public void doPostForm(){
        Map<String,String> map = new HashMap<>();
        Map<String,String> map2 = new HashMap<>();
        ResponseEntity responseEntity = HttpClientUtilOrder.doPostForm("http://www.ietf.org//rfc",map,map2);
    }
    @Test
    public void doPut(){
        Map<String,String> map = new HashMap<>();
        ResponseEntity responseEntity = HttpClientUtilOrder.doPut("http://www.ietf.org//rfc","body",map);
    }
    @Test
    public void doDelete(){
        Map<String,String> map = new HashMap<>();
        ResponseEntity responseEntity = HttpClientUtilOrder.doDelete("http://www.ietf.org//rfc","body",map);
    }
    @Test
    public void doPatch(){
        Map<String,String> map = new HashMap<>();
        ResponseEntity responseEntity = HttpClientUtilOrder.doPatch("http://www.ietf.org//rfc","body",map);
    }
}
