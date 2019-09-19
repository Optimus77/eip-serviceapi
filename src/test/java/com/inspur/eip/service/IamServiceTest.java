package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.bss.OrderProduct;
import com.inspur.eip.entity.bss.OrderProductItem;
import com.inspur.eip.entity.bss.ReciveOrder;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.iam.IamParam;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.ReturnResult;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.http.HttpUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openstack4j.model.common.ActionResponse;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.eq;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CommonUtil.class, HttpUtil.class})
public class IamServiceTest {

    @InjectMocks
    IamService iamService;

    @Mock
    private SbwRepository sbwRepository;

    @Mock
    private EipRepository eipRepository;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(iamService, "iamUrl", "iamUrl");
        PowerMockito.mockStatic(CommonUtil.class,HttpUtil.class);
        //PowerMockito.when(CommonUtil.getUserId()).thenReturn("23");
        PowerMockito.when(CommonUtil.getProjectId(Mockito.anyString())).thenReturn("unittest");
        //PowerMockito.when(CommonUtil.isParentOrChildAccount(Mockito.anyString())).thenReturn(false);

        Eip eip = new Eip();
        eip.setUserId("23");
        eip.setProjectId("unittest");
        Mockito.doReturn(eip).when(eipRepository).findByIdAndIsDelete(Mockito.anyString(),Mockito.anyInt());
        Mockito.doReturn(null).when(eipRepository).findByIdAndIsDelete(eq("notFound"),Mockito.anyInt());


        Sbw sbw = Sbw.builder()
                .userId("23")
                .projectId("unittest")
                .build();
        Mockito.doReturn(sbw).when(sbwRepository).findByIdAndIsDelete(Mockito.anyString(),Mockito.anyInt());
        Mockito.doReturn(null).when(sbwRepository).findByIdAndIsDelete(eq("notFound"),Mockito.anyInt());
        Sbw sbw1 = Sbw.builder()
                .projectId("unittest")
                .build();
        Mockito.doReturn(sbw1).when(sbwRepository).findByIdAndIsDelete(eq("userIsNull"),Mockito.anyInt());
    }


    @Test
    public void isIamAuthorityEip() throws Exception {
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("EIP")
                .orderType("new")
                .productList(orderProducts)
                .build();
        ReturnResult returnResult = ReturnResult.actionSuccess("true");
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("23");
        PowerMockito.when(CommonUtil.isParentOrChildAccount(Mockito.anyString())).thenReturn(false);
        PowerMockito.when(HttpUtil.post(Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(returnResult);
        ActionResponse actionResponse = iamService.isIamAuthority(reciveOrder);
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void isIamAuthoritySbw() throws Exception {
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("sbwId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("SBW")
                .orderType("new")
                .productList(orderProducts)
                .build();
        ReturnResult returnResult = ReturnResult.actionSuccess("true");
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("23");
        PowerMockito.when(CommonUtil.isParentOrChildAccount(Mockito.anyString())).thenReturn(false);
        PowerMockito.when(HttpUtil.post(Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(returnResult);
        ActionResponse actionResponse = iamService.isIamAuthority(reciveOrder);
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void isIamAuthorityOtherUser() throws Exception {
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("EIP")
                .orderType("unsubscribe")
                .productList(orderProducts)
                .build();
        ReturnResult returnResult = ReturnResult.actionSuccess("false");
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("33");
        PowerMockito.when(CommonUtil.isParentOrChildAccount(Mockito.anyString())).thenReturn(false);
        PowerMockito.when(HttpUtil.post(Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(returnResult);
        ActionResponse actionResponse = iamService.isIamAuthority(reciveOrder);
        assertEquals(403, actionResponse.getCode());
    }

    @Test
    public void isIamAuthorityNotFound() throws Exception {
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("notFound")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("EIP")
                .orderType("unsubscribe")
                .productList(orderProducts)
                .build();
        ReturnResult returnResult = ReturnResult.actionSuccess("false");
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("33");
        PowerMockito.when(CommonUtil.isParentOrChildAccount(Mockito.anyString())).thenReturn(false);
        PowerMockito.when(HttpUtil.post(Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(returnResult);
        ActionResponse actionResponse = iamService.isIamAuthority(reciveOrder);
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void isIamAuthorityParentAccount() throws Exception {
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("EIP")
                .orderType("new")
                .productList(orderProducts)
                .build();
        ReturnResult returnResult = ReturnResult.actionSuccess("true");
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("23");
        PowerMockito.when(CommonUtil.isParentOrChildAccount(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(HttpUtil.post(Mockito.anyString(),Mockito.anyMap(),Mockito.anyString())).thenReturn(returnResult);
        ActionResponse actionResponse = iamService.isIamAuthority(reciveOrder);
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void getActionUpdateEip() {
        String orderType = "";
        String orderRoute = "EIP";
        String action = iamService.getAction(orderType,orderRoute);
        assertEquals("UpdateEip", action);
    }
    @Test
    public void getActionCreateSbw() {
        String orderType = "new";
        String orderRoute = "SBW";
        String action = iamService.getAction(orderType,orderRoute);
        assertEquals("CreateSbw", action);
    }
    @Test
    public void getActionDeleteSbw() {
        String orderType = "unsubscribe";
        String orderRoute = "SBW";
        String action = iamService.getAction(orderType,orderRoute);
        assertEquals("DeleteSbw", action);
    }
    @Test
    public void getActionUpdateSbw() {
        String orderType = "";
        String orderRoute = "SBW";
        String action = iamService.getAction(orderType,orderRoute);
        assertEquals("UpdateSbw", action);
    }

    @Test
    public void isParentOrChildEntity() throws KeycloakTokenException {
        String Id = "eipId";
        IamParam iamParam = new IamParam();
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("EIP")
                .orderType("new")
                .productList(orderProducts)
                .build();
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("23");
        String isParentOrChildEntity = iamService.isParentOrChildEntity(reciveOrder,Id,iamParam);
        assertEquals("ChildEntity", isParentOrChildEntity);
    }

    @Test
    public void isParentOrChildEntitySbwNotFound() throws KeycloakTokenException {
        String Id = "notFound";
        IamParam iamParam = new IamParam();
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("SBW")
                .orderType("new")
                .productList(orderProducts)
                .build();
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("23");
        String isParentOrChildEntity = iamService.isParentOrChildEntity(reciveOrder,Id,iamParam);
        assertEquals("NOT_FOUND", isParentOrChildEntity);
    }

    @Test
    public void isParentOrChildEntitySbwChildEntity() throws KeycloakTokenException {
        String Id = "sbwId";
        IamParam iamParam = new IamParam();
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("SBW")
                .orderType("new")
                .productList(orderProducts)
                .build();
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("23");
        String isParentOrChildEntity = iamService.isParentOrChildEntity(reciveOrder,Id,iamParam);
        assertEquals("ChildEntity", isParentOrChildEntity);
    }

    @Test
    public void isParentOrChildEntitySbwOtherEntity() throws KeycloakTokenException {
        String Id = "sbwId";
        IamParam iamParam = new IamParam();
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("SBW")
                .orderType("new")
                .productList(orderProducts)
                .build();
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("33");
        String isParentOrChildEntity = iamService.isParentOrChildEntity(reciveOrder,Id,iamParam);
        assertEquals("otherEntity", isParentOrChildEntity);
    }

    @Test
    public void isParentOrChildEntitySbwOtherEntityUserIsNull() throws KeycloakTokenException {
        String Id = "userIsNull";
        IamParam iamParam = new IamParam();
        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .instanceId("eipId")
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        ReciveOrder reciveOrder = ReciveOrder.builder()
                .token("token")
                .orderRoute("SBW")
                .orderType("new")
                .productList(orderProducts)
                .build();
        PowerMockito.when(CommonUtil.getUserId()).thenReturn("33");
        String isParentOrChildEntity = iamService.isParentOrChildEntity(reciveOrder,Id,iamParam);
        assertEquals("otherEntity", isParentOrChildEntity);
    }


}