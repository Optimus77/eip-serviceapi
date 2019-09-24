package com.inspur.eip.service;

import com.inspur.eip.util.common.IpUtil;
import com.inspur.eip.util.http.HsHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@PrepareForTest({HsHttpClient.class,IpUtil.class})
@RunWith(PowerMockRunner.class)
public class QosServiceTest {
    @InjectMocks
    private QosService qosService;

    @Mock
    private FireWallCommondService fwCmdService;
    @Before
    public void setUp() {
        ReflectionTestUtils.setField(qosService,"fwIp","11");
        ReflectionTestUtils.setField(qosService,"fwPort","22");
        ReflectionTestUtils.setField(qosService,"fwUser","33");
        ReflectionTestUtils.setField(qosService,"fwPwd","44");

        PowerMockito.mockStatic(HsHttpClient.class);
        PowerMockito.mockStatic(IpUtil.class);
        PowerMockito.when(IpUtil.ipToLong(Mockito.anyString())).thenReturn("123");

    }

  /*  @Test
    public void delQosPipe() throws Exception {
        String str = "{\"target\":\"root\",\"success\",\"true\"}";
        PowerMockito.when(HsHttpClient.hsHttpDelete(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(str);
        Map<String ,String> map = qosService.delQosPipe("11");
    }*/
    /*@Test
    public void delQosPipeCase2() throws Exception {
        String str = "{\"target\":\"root\",\"success\":\"true\"}";
        PowerMockito.when(HsHttpClient.hsHttpDelete(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(str);
        Map<String ,String> map = qosService.delQosPipe("11");
    }
    @Test
    public void delQosPipeCase3() throws Exception {
        String str = "{\"target\":\"root\",\"success\":\"false\",\"exception\":{\"message\":\"Error: The root pipe dose not exist\"}}";
        PowerMockito.when(HsHttpClient.hsHttpDelete(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(str);
        Map<String ,String> map = qosService.delQosPipe("11");
    }*/

   /* @Test
    public void getQosPipeId() throws Exception {
        String str = "{\"children\":[{\"name\":\"xiaoming\",\"id\":\"001\"}]}";
        PowerMockito.when(HsHttpClient.hsHttpGet(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(str);
        String result = qosService.getQosPipeId("xiaoming");
    }*/

    @Test
    public void removeIpFromPipe() throws Exception {
        String str = "{\"children\":[{\"name\":\"xiaoming\",\"id\":\"001\"}]}";
        PowerMockito.when(HsHttpClient.hsHttpGet(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.eq("/rest/iQos?query=%7B%22conditions%22%3A%5B%7B%22f%22%3A%22name%22%2C%22v%22%3A%22first%22%7D%5D%7D&target=root&node=root&id=%7B%22node%22%3A%22root%22%7D"))).thenReturn(str);
        String str2 = "[{\"name\":\"xiaohua\"}]";
        PowerMockito.when(HsHttpClient.hsHttpGet(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.eq("/rest/iQos?query=%7B%22conditions%22%3A%5B%7B%22f%22%3A%22name%22%2C%22v%22%3A%22first%22%7D%2C%7B%22f%22%3A%22root.id%22%2C%22v%22%3A%22" + "001" + "%22%7D%5D%7D&target=root.rule"))).thenReturn(str2);
        Boolean result = qosService.removeIpFromPipe("111","xiaoming");
    }
    @Test
    public void deleteConditionFromPipe() throws Exception {
        String str = "{\"target\":\"root\",\"success\":\"true\"}";
        PowerMockito.when(HsHttpClient.hsHttpDelete(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(str);
        Method method = PowerMockito.method(QosService.class,"deleteConditionFromPipe",String.class,String.class);
        Boolean result =(Boolean) method.invoke(qosService,"11","22");
    }

}
