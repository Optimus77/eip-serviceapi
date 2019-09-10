package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.openapi.EipConstant;
import com.inspur.eip.entity.openapi.Item;
import com.inspur.eip.entity.openapi.OpenCreateEip;
import com.inspur.eip.entity.sbw.Sbw;
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

import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpsClientUtil.class, CommonUtil.class, HttpClientUtil.class, OpenApiSbwServiceImpl.class})
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
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\"}]}}", HttpStatus.OK);
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
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\"}]}}", HttpStatus.OK);
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
        OpenApiSbwServiceImpl spy = PowerMockito.spy(openApiSbwService);
        PowerMockito.doReturn("12").when(spy,"getSbwBandwidth",Mockito.any());
        PowerMockito.doReturn("test").when(spy,"getSbwName",Mockito.any());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\"}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = spy.OpenapiEipAddSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiEipRemoveSbw() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setEipId("eipId");
        OpenApiSbwServiceImpl spy = PowerMockito.spy(openApiSbwService);
        PowerMockito.doReturn("12").when(spy,"getSbwBandwidth",Mockito.any());
        PowerMockito.doReturn("test").when(spy,"getSbwName",Mockito.any());
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\"}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = spy.OpenapiEipRemoveSbw(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void openapiRenewSbw() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbwId");
        openCreateEip.setBillType(EipConstant.BILLTYPE_MONTHLY);
        openCreateEip.setDuration("1");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\"}]}}", HttpStatus.OK);
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
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"SBW\"}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null), Mockito.anyMap(), Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiSbwService.OpenapiSbwUpdateBandwidth(openCreateEip,"token");
        Assert.assertEquals(200,result.getStatusCode().value());
    }

    @Test
    public void getSbwBandwidth() throws InvocationTargetException, IllegalAccessException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId ("sbwId");
        Sbw sbw = Sbw.builder().bandWidth(12).build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"getSbwBandwidth",OpenCreateEip.class);
        String result =(String) method.invoke(openApiSbwService,openCreateEip);
    }

    @Test
    public void getSbwName() throws InvocationTargetException, IllegalAccessException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId ("sbwId");
        Sbw sbw = Sbw.builder().sbwName("test").build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"getSbwName",OpenCreateEip.class);
        String result =(String) method.invoke(openApiSbwService,openCreateEip);
    }

    @Test
    public void buildItemListCase1() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","bandwidth");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildItemListCase2() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","sbwName");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildItemListBandwidth() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","bandwidth");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildItemListBandwidth",List.class,JSONArray.class,int.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth");
    }

    @Test
    public void buildEipAddSbwItemListCase1() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","bandwidth");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipAddSbwItemListCase2() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","transfer");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipAddSbwItemListCase3() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","IP");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipAddSbwItemListCase4() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","provider");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipAddSbwItemListCase5() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","is_SBW");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipAddSbwItemListCase6() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","sbwId");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipRemoveSbwItemListCase1() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","bandwidth");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipRemoveSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipRemoveSbwItemListCase2() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","transfer");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipRemoveSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipRemoveSbwItemListCase3() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","IP");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipRemoveSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipRemoveSbwItemListCase4() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","provider");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipRemoveSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipRemoveSbwItemListCase5() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","is_SBW");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipRemoveSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipRemoveSbwItemListCase6() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","sbwName");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipRemoveSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }

    @Test
    public void buildEipRemoveSbwItemListCase7() throws InvocationTargetException, IllegalAccessException {
        List<Item> list = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","sbwId");
        list2.add(jsonObject);
        JSONArray jsonArray = new JSONArray(list2);
        Method method = PowerMockito.method(OpenApiSbwServiceImpl.class,"buildEipRemoveSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
        method.invoke(openApiSbwService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
    }


}