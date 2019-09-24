package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.openapi.EipConstant;
import com.inspur.eip.entity.openapi.Item;
import com.inspur.eip.entity.openapi.OpenCreateEip;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.http.HttpClientUtil;
import com.inspur.eip.util.http.HttpsClientUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpsClientUtil.class, CommonUtil.class, HttpClientUtil.class})
public class OpenApiSbwServiceImplTest {

    @InjectMocks
    private OpenApiSbwServiceImpl openApiSbwService;

    @Mock
    private SbwRepository sbwRepository;

    @Mock
    private EipRepository eipRepository;

    @Autowired
    SbwDaoService sbwDaoService;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(openApiSbwService, "regionCode", "regionCode");
        ReflectionTestUtils.setField(openApiSbwService, "bssQuotaUrl", "bssQuotaUrl");
        ReflectionTestUtils.setField(openApiSbwService, "bssSubmitUrl", "regionCode");
        ReflectionTestUtils.setField(openApiSbwService, "regionCode", "regionCode");
        PowerMockito.mockStatic(HttpsClientUtil.class, CommonUtil.class, HttpClientUtil.class);
        PowerMockito.when(CommonUtil.getUserId(Mockito.anyString())).thenReturn("23");
    }


    @Test
    public void openapiCreateSbw() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[{\"code\":\"SBW\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void openapiCreateSbw1() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[{\"code\":\"SBW\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(null);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(null);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void openapiCreateSbw2() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[{\"code\":\"SBW\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.CREATED);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(201, result.getStatusCode().value());
    }

    @Test
    public void openapiCreateSbw3() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void openapiCreateSbw4() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void openapiCreateSbw5() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[{\"code\":\"sbw\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void openapiCreateSbw6() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"sbw\",\"productTypeList\":[{\"code\":\"SBW\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void openapiCreateSbw7() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\"}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiCreateSbw(openCreateEip, "23");
        Assert.assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void openapiDeleteSbw() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[{\"code\":\"SBW\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiDeleteSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"transfer\"},{\"code\":\"IP\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwId\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw1() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"transfer\"},{\"code\":\"IP\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwId\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(null);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw2() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"transfer\"},{\"code\":\"IP\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwId\"}]}]}]}}", HttpStatus.CREATED);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(201,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw3() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw4() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw5() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"eip\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"transfer\"},{\"code\":\"IP\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwId\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw6() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"eip\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"transfer\"},{\"code\":\"IP\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwId\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipAddSbw7() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\"}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipRemoveSbw() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        Sbw sbw = Sbw.builder()
                .id("sbwId")
                .bandWidth(12)
                .sbwName("test")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"transfer\"},{\"code\":\"IP\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwName\"},{\"code\":\"sbwId\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiEipRemoveSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiRenewSbw() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setBillType(EipConstant.BILLTYPE_MONTHLY);
        openCreateEip.setDuration("1");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[{\"code\":\"SBW\",\"itemList\":[{\"code\":\"bandwidth\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiRenewSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiSbwUpdateBandwidth() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setBandwidth("20");
        openCreateEip.setBillType ("monthly");
        Sbw sbw = Sbw.builder()
                .bandWidth(10)
                .billType("monthly")
                .build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\",\"productTypeList\":[{\"code\":\"SBW\",\"itemList\":[{\"code\":\"bandwidth\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiSbwUpdateBandwidth(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

}