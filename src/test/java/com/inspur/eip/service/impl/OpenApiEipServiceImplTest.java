package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.openapi.Item;
import com.inspur.eip.entity.openapi.OpenCreateEip;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
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
@PrepareForTest({HttpsClientUtil.class,CommonUtil.class,HttpClientUtil.class})
public class OpenApiEipServiceImplTest {

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
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEip(openCreateEip,"23");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiCreateEip1() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBillType ("monthly");
        openCreateEip.setDuration("notnull");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(null);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(null);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEip(openCreateEip,"23");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiCreateEip2() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBillType ("monthly");
        openCreateEip.setDuration("notnull");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.CREATED);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEip(openCreateEip,"23");
        Assert.assertEquals(result.getStatusCode().value(),201);
    }

    @Test
    public void openapiCreateEip3() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBillType ("monthly");
        openCreateEip.setDuration("notnull");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEip(openCreateEip,"23");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiCreateEip4() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBillType ("monthly");
        openCreateEip.setDuration("notnull");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"eip\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEip(openCreateEip,"23");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiCreateEip5() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBillType ("monthly");
        openCreateEip.setDuration("notnull");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\"}",HttpStatus.OK);
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
        Sbw sbw = Sbw.builder().bandWidth(50).build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());

        PowerMockito.mockStatic(HttpsClientUtil.class);
        Map<String, String> map = new HashMap<>();
        map.put("name", "alien");
        PowerMockito.when(HttpsClientUtil.getHeader()).thenReturn(map);
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);

        ResponseEntity responseEntity1 = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"IP\"},{\"code\":\"transfer\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwName\"},{\"code\":\"sbwId\"}]}]}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity1);

//        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEipAddSbw(openCreateEip, "token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiCreateEipAddSbw1() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbw");
        Sbw sbw = Sbw.builder().bandWidth(50).build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());

        PowerMockito.mockStatic(HttpsClientUtil.class);
        Map<String, String> map = new HashMap<>();
        map.put("name", "alien");
        PowerMockito.when(HttpsClientUtil.getHeader()).thenReturn(map);
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(Mockito.anyString(),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);

        ResponseEntity responseEntity1 = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"eip\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"IP\"},{\"code\":\"transfer\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwName\"},{\"code\":\"sbwId\"}]}]}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity1);

//        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiCreateEipAddSbw(openCreateEip, "token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiDeleteEip() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setSbwId("sbw");
        openCreateEip.setEipId("eipId");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"sbwName\"}]}]}]}}", HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiDeleteEip(openCreateEip,"token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiRenewEip() throws Exception {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setEipId("eipId");
        openCreateEip.setDuration("duration");
        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        List <String> list = new ArrayList<>();
        list.add("123");
        jsonArray.add(list);
        ResponseEntity responseEntity1 = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"aa\":\"bb\"}]}]}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity1);

        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiRenewEip(openCreateEip,"token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapiEipupdateBandwidth() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setEipId("eipId");
        openCreateEip.setBandwidth("11");
        openCreateEip.setBillType ("monthly");
        Eip eip = new Eip();
        eip.setBandWidth(9);
        eip.setBillType("monthly");
        Optional<Eip> optional = Optional.of(eip);
        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
        ResponseEntity responseEntity1 = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"IP\"},{\"code\":\"transfer\"},{\"code\":\"provider\"},{\"code\":\"is_SBW\"},{\"code\":\"sbwName\"},{\"code\":\"sbwId\"}]}]}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity1);


        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
//        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapiEipupdateBandwidth(openCreateEip,"token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

    @Test
    public void openapicreateIptsBandEip() throws IOException, URISyntaxException {
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBillType ("monthly");
        openCreateEip.setDuration("notnull");
        openCreateEip.setBandwidth("50");
        ResponseEntity responseEntity = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\"}]}}",HttpStatus.OK);
//        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = new ResponseEntity("{\"code\":\"0\",\"result\":{\"productLineList\":[{\"code\":\"EIP\",\"productTypeList\":[{\"code\":\"EIP\",\"itemList\":[{\"code\":\"bandwidth\"},{\"code\":\"IP\"},{\"code\":\"transfer\"},{\"code\":\"provider\"},{\"code\":\"withIpv6\"}]}]}]}}",HttpStatus.OK);
        PowerMockito.when(HttpClientUtil.doGet(eq(null),Mockito.anyMap(),Mockito.anyMap())).thenReturn(responseEntity1);

        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(),Mockito.anyString(),Mockito.anyMap())).thenReturn(responseEntity);
        ResponseEntity result = openApiEipService.OpenapicreateIptsBandEip(openCreateEip,"token");
        Assert.assertEquals(result.getStatusCode().value(),200);
    }

//    @Test
//    public void getEipBandwidth() throws InvocationTargetException, IllegalAccessException {
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        openCreateEip.setEipId ("eipId");
//        Eip eip = new Eip();
//        eip.setBandWidth(123);
//        Optional<Eip> optional = Optional.of(eip);
//        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"getEipBandwidth",OpenCreateEip.class);
//        String result =(String) method.invoke(openApiEipService,openCreateEip);
//    }

//    @Test
//    public void getSbwBandwidth() throws InvocationTargetException, IllegalAccessException {
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        openCreateEip.setSbwId ("sbwId");
//        Sbw sbw = Sbw.builder().bandWidth(12).build();
//        Optional<Sbw> optional = Optional.of(sbw);
//        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"getSbwBandwidth",OpenCreateEip.class);
//        String result =(String) method.invoke(openApiEipService,openCreateEip);
//    }

//    @Test
//    public void buildItemListCase1() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        List<Object> list2 = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","bandwidth");
//        list2.add(jsonObject);
//        JSONArray jsonArray = new JSONArray(list2);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildItemListCase2() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        List<Object> list2 = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","transfer");
//        list2.add(jsonObject);
//        JSONArray jsonArray = new JSONArray(list2);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildItemListCase3() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        List<Object> list2 = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","IP");
//        list2.add(jsonObject);
//        JSONArray jsonArray = new JSONArray(list2);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildItemListCase4() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        List<Object> list2 = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","provider");
//        list2.add(jsonObject);
//        JSONArray jsonArray = new JSONArray(list2);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildItemListCase5() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        List<Object> list2 = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","is_SBW");
//        list2.add(jsonObject);
//        JSONArray jsonArray = new JSONArray(list2);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildItemListCase6() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        List<Object> list2 = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","sbwName");
//        list2.add(jsonObject);
//        JSONArray jsonArray = new JSONArray(list2);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildItemListCase7() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        List<Object> list2 = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","sbwId");
//        list2.add(jsonObject);
//        JSONArray jsonArray = new JSONArray(list2);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildItemList",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }

//    @Test
//    public void buildIptsBindEipCase1() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","bandwidth");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildIptsBindEip",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildIptsBindEipCase2() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","transfer");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildIptsBindEip",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildIptsBindEipCase3() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","IP");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildIptsBindEip",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildIptsBindEipCase4() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","provider");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildIptsBindEip",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//    @Test
//    public void buildIptsBindEipCase5() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","withIpv6");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildIptsBindEip",List.class,JSONArray.class,int.class,String.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName","sbwId");
//    }
//
//    @Test
//    public void buildCreateEipAddSbwItemList() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","bandwidth");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildCreateEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName");
//    }
//    @Test
//    public void buildCreateEipAddSbwItemListCase2() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","transfer");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildCreateEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName");
//    }
//    @Test
//    public void buildCreateEipAddSbwItemListCase3() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","IP");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildCreateEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName");
//    }
//    @Test
//    public void buildCreateEipAddSbwItemListCase4() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","provider");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildCreateEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName");
//    }
//    @Test
//    public void buildCreateEipAddSbwItemListCase5() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","is_SBW");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildCreateEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName");
//    }
//    @Test
//    public void buildCreateEipAddSbwItemListCase6() throws InvocationTargetException, IllegalAccessException {
//        List<Item> list = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code","sbwId");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(jsonObject);
//        Method method = PowerMockito.method(OpenApiEipServiceImpl.class,"buildCreateEipAddSbwItemList",List.class,JSONArray.class,int.class,String.class,String.class);
//        method.invoke(openApiEipService,list,jsonArray,0,"bandwidth","sbwName");
//    }
}