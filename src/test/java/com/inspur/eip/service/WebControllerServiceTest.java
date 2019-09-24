package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.bss.OrderProduct;
import com.inspur.eip.entity.bss.OrderProductItem;
import com.inspur.eip.entity.bss.ReciveOrder;
import com.inspur.eip.exception.EipException;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.util.ReturnResult;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.http.HttpsClientUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

@PrepareForTest({CommonUtil.class, HttpsClientUtil.class})
@RunWith(PowerMockRunner.class)
public class WebControllerServiceTest {

    @InjectMocks
    private WebControllerService webControllerService;

    @Before
    public void setUp() throws KeycloakTokenException, EipException {
        ReflectionTestUtils.setField(webControllerService,"pushMq","pushmq");
        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.mockStatic(HttpsClientUtil.class);
        PowerMockito.when(CommonUtil.getUsername(Mockito.anyString())).thenReturn("123");
        PowerMockito.when(HttpsClientUtil.doPostJson(Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(ReturnResult.actionSuccess("its ok"));
    }

    @Test
    public void retWebsocket(){
        ReciveOrder reciveOrder = ReciveOrder.builder().token("123").build();
        webControllerService.retWebsocket("yes",reciveOrder,"yes",420);
    }

    @Test
    public void returnsIpv6Websocket(){
        webControllerService.returnsIpv6Websocket("11","createNatWithEip","33");

    }
    /*@Test
    public void returnSbwWebsocket(){
        ReciveOrder reciveOrder = ReciveOrder.builder().token("123").build();
        webControllerService.returnSbwWebsocket("11",reciveOrder,"22");
    }

    @Test
    public void returnsWebsocket(){
        ReciveOrder reciveOrder = ReciveOrder.builder().token("123").build();
        webControllerService.returnsWebsocket("11",reciveOrder,"22");
    }*/

    @Test
    public void returnsWebsocketV2(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId","11");
        List<OrderProduct> list = new ArrayList<>();
        List<OrderProductItem> itemList = new ArrayList<>();
        OrderProductItem orderProductItem = new OrderProductItem();
        itemList.add(orderProductItem);
        orderProductItem.setCode("groupId");
        orderProductItem.setValue("val");
        OrderProduct orderProduct = OrderProduct.builder().itemList(itemList).build();
        list.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder().productList(list).consoleCustomization(jsonObject).orderType("new").orderRoute("EIP").token("123").build();
        webControllerService.returnsWebsocketV2(reciveOrder,"success");
    }
}

