package com.inspur.eip.util.http;

import com.inspur.eip.util.ReturnResult;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpUtilTest {

    @Before
    public void setUp(){

    }

    @Test
    public void get() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("key","value");
        ReturnResult result =HttpUtil.get("http://www.ietf.org",map);
    }
    @Test
    public void post() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("key","value");
        ReturnResult result =HttpUtil.post("http://www.ietf.org",map,"body");
    }
    @Test
    public void delete() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("key","value");
        ReturnResult result =HttpUtil.delete("http://www.ietf.org",map);
    }
    @Test
    public void put() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("key","value");
        ReturnResult result =HttpUtil.put("http://www.ietf.org",map,"body");
    }
}
