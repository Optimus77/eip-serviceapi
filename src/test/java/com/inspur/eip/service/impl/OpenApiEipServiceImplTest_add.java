package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.PowerMockBaseTest;
import com.inspur.eip.entity.openapi.OpenCreateEip;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.http.HttpClientUtil;
import com.inspur.eip.util.http.HttpsClientUtil;
import groovy.util.logging.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.omg.PortableServer.POA;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;


@RunWith(PowerMockRunner.class)
//@ContextConfiguration(classes = OpenApiEipServiceImpl.class)
//@SpringBootTest(classes = EipServiceApplicationTests.class)
@PrepareForTest({HttpsClientUtil.class,CommonUtil.class,HttpClientUtil.class,OpenApiEipServiceImpl.class})
public class OpenApiEipServiceImplTest_add {

    @Autowired
    @InjectMocks
    OpenApiEipServiceImpl openApiEipService;

    @Mock
    private SbwRepository sbwRepository;

    @Mock
    private EipRepository eipRepository;

    @Before
    public void setUp() throws KeycloakTokenException {
        ReflectionTestUtils.setField(openApiEipService, "regionCode", "regionCode");
        ReflectionTestUtils.setField(openApiEipService, "bssQuotaUrl", "bssQuotaUrl");
        ReflectionTestUtils.setField(openApiEipService, "bssSubmitUrl", "regionCode");
        ReflectionTestUtils.setField(openApiEipService, "regionCode", "regionCode");
        PowerMockito.mockStatic(HttpsClientUtil.class,CommonUtil.class,HttpClientUtil.class);
        PowerMockito.when(CommonUtil.getUserId(Mockito.anyString())).thenReturn("23");

    }

    @Test
    public void openapiCreateEip() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBillType ("monthly");
        openCreateEip.setDuration("notnull");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEip(openCreateEip,"23");
        Assert.assertEquals(result.getStatusCode().value(),200);

    }

    @Test
    public void openapiCreateEipAddSbw() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbw");
        OpenApiEipServiceImpl spy = PowerMockito.spy(openApiEipService);
        PowerMockito.doReturn("12").when(spy,"getSbwBandwidth",Mockito.any());
        PowerMockito.mockStatic(HttpsClientUtil.class);
        Map<String, String> map = new HashMap<>();
        map.put("name", "alien");
        PowerMockito.when(HttpsClientUtil.getHeader()).thenReturn(map);
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = spy.OpenapiCreateEipAddSbw(openCreateEip, "token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiDeleteEip() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbw");
        openCreateEip.setEipId("eipId");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiDeleteEip(openCreateEip,"token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void OpenapiRenewEip() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setEipId("eipId");
        openCreateEip.setDuration("duration");
        OpenApiEipServiceImpl spy = PowerMockito.spy(openApiEipService);
        PowerMockito.when(spy,"getUserProductItems",Mockito.anyString()).thenReturn(new JSONArray());
        spy.OpenapiRenewEip(openCreateEip,"token");
    }
}