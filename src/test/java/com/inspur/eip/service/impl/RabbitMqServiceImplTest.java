package com.inspur.eip.service.impl;

import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.service.TokenUtil;
import com.inspur.eip.entity.bss.*;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.util.constant.HsConstants;
import groovy.util.logging.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RabbitMqServiceImpl.class)
@Rollback
@SpringBootTest(classes = EipServiceApplicationTests.class)
@Transactional
public class RabbitMqServiceImplTest {
    @Autowired
    private RabbitMqServiceImpl rabbitMqService;
    @Autowired
    SbwDaoService sbwDaoService;
    @Autowired
    EipPoolRepository eipPoolRepository;
    @Autowired
    EipDaoService eipDaoService;

    @Before
    public void setUp() throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequest(){

            @Override
            public String getHeader(String name) {
                try {
                    return "bearer " + TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public long getContentLengthLong() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return null;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public void setAttribute(String name, Object o) {

            }

            @Override
            public void removeAttribute(String name) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return null;
            }

            @Override
            public String getRealPath(String path) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }

            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];
            }

            @Override
            public long getDateHeader(String name) {
                return 0;
            }



            @Override
            public Enumeration<String> getHeaders(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public int getIntHeader(String name) {
                return 0;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return null;
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public HttpSession getSession(boolean create) {
                return null;
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public String changeSessionId() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
                return false;
            }

            @Override
            public void login(String username, String password) throws ServletException {

            }

            @Override
            public void logout() throws ServletException {

            }

            @Override
            public Collection<Part> getParts() throws IOException, ServletException {
                return null;
            }

            @Override
            public Part getPart(String name) throws IOException, ServletException {
                return null;
            }

            @Override
            public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
                return null;
            }
        }));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createEipInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("100");
        OrderProductItem orderProductItem2 = new OrderProductItem();
        orderProductItem2.setCode("provider");
        orderProductItem2.setValue("BGP");
        OrderProductItem orderProductItem3 = new OrderProductItem();
        orderProductItem3.setCode("transfer");
        orderProductItem3.setValue("0");
        OrderProductItem orderProductItem4 = new OrderProductItem();
        orderProductItem4.setCode("IP");
        orderProductItem4.setValue("1");
        OrderProductItem orderProductItem5 = new OrderProductItem();
        orderProductItem5.setCode("is_SBW");
        orderProductItem5.setValue("no");
        orderProductItems.add(orderProductItem1);
        orderProductItems.add(orderProductItem2);
        orderProductItems.add(orderProductItem3);
        orderProductItems.add(orderProductItem4);
        orderProductItems.add(orderProductItem5);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("EIP")
                .productTypeCode("EIP")
                .instanceCount("1")
                .instanceId("")
                .instanceStatus("success")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("06efa8b8-0d2a-8f29-eb32-868eacb00288")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIzNTFiZDAyNi0yMDVlLTRmZWQtYjQ5MC05NjAxYzRlYzg5MjEiLCJleHAiOjE1NjI1NzU0NDMsIm5iZiI6MCwiaWF0IjoxNTYyNTcwMDQzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImM3NDdiMzU4LTQ2NjUtNDIzZi04ZWRiLTcwZmIzYzUwZjY5NSIsImF1dGhfdGltZSI6MTU2MjU3MDA0Miwic2Vzc2lvbl9zdGF0ZSI6IjQzNTE4MzE4LWQ1NjItNGYzZi04MTcxLThkNWMzNTYzZjliZCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.bYy8UiPv_zFydZHhsHw6umc9IO2Qvukfdf5waiqb8QZ1zKd6XE0mTkJbSxhsafVISq5D2hqb1yQjgcMI1oGggmi8HfMIcCosT73NXuPf4LX2LO8ClqvXuQtEp8-_0O5rJ9AyVNOvJEEZ4LuDvnipVNQPsOPjstB2QpKXf51-VsNibFwYmiF4BL73ND3--g_Jx22WaVdtQfw1Uip46PfaISc5pLL79Ev6UlSHOc4B7bGsQ2JAw4F_mc7GfJCWtOcUAhdFrrdn-J_fgZxI0G76FBS5NLs0R_7cI8DM5nej8vFf3se8fjEYnRGyRZu8Br6nEsPJ0P3RmuXPGwys0AD7mg")
                .orderRoute("EIP")
                .consoleCustomization(null)
                .userId("9d1a8b7b-0ba4-46c1-9239-a3976c2aedff")
                .setCount("1")
                .billType("hourlySettlement")
                .orderType("new")
                .duration("1")
                .durationUnit("H")
                .productList(orderProducts)
                .build();

        ActionResponse eipInfo = rabbitMqService.createEipInfo(reciveOrder);
        assertEquals(200,eipInfo.getCode());
    }

    @Test
    public void deleteEipConfig() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("100");
        OrderProductItem orderProductItem2 = new OrderProductItem();
        orderProductItem2.setCode("provider");
        orderProductItem2.setValue("BGP");
        OrderProductItem orderProductItem3 = new OrderProductItem();
        orderProductItem3.setCode("transfer");
        orderProductItem3.setValue("0");
        OrderProductItem orderProductItem4 = new OrderProductItem();
        orderProductItem4.setCode("IP");
        orderProductItem4.setValue("1");
        OrderProductItem orderProductItem5 = new OrderProductItem();
        orderProductItem5.setCode("is_SBW");
        orderProductItem5.setValue("no");
        orderProductItems.add(orderProductItem1);
        orderProductItems.add(orderProductItem2);
        orderProductItems.add(orderProductItem3);
        orderProductItems.add(orderProductItem4);
        orderProductItems.add(orderProductItem5);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("EIP")
                .productTypeCode("EIP")
                .instanceCount("1")
                .instanceId(eip.getId())
                .instanceStatus("success")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("06efa8b8-0d2a-8f29-eb32-868eacb00288")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token(TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"))
                .orderRoute("SBW")
                .consoleCustomization(null)
                .userId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72")
                .setCount("1")
                .billType("hourlySettlement")
                .orderType("new")
                .duration("1")
                .durationUnit("H")
                .productList(orderProducts)
                .build();

        ActionResponse actionResponse = rabbitMqService.deleteEipConfig(reciveOrder);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void deleteEipConfigNotFound() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("100");
        OrderProductItem orderProductItem2 = new OrderProductItem();
        orderProductItem2.setCode("provider");
        orderProductItem2.setValue("BGP");
        OrderProductItem orderProductItem3 = new OrderProductItem();
        orderProductItem3.setCode("transfer");
        orderProductItem3.setValue("0");
        OrderProductItem orderProductItem4 = new OrderProductItem();
        orderProductItem4.setCode("IP");
        orderProductItem4.setValue("1");
        OrderProductItem orderProductItem5 = new OrderProductItem();
        orderProductItem5.setCode("is_SBW");
        orderProductItem5.setValue("no");
        orderProductItems.add(orderProductItem1);
        orderProductItems.add(orderProductItem2);
        orderProductItems.add(orderProductItem3);
        orderProductItems.add(orderProductItem4);
        orderProductItems.add(orderProductItem5);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("EIP")
                .productTypeCode("EIP")
                .instanceCount("1")
                .instanceId("aa031f07-19f4-4e21-b140-ebd731e5f37b")
                .instanceStatus("success")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("06efa8b8-0d2a-8f29-eb32-868eacb00288")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token(TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"))
                .orderRoute("SBW")
                .consoleCustomization(null)
                .userId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72")
                .setCount("1")
                .billType("hourlySettlement")
                .orderType("new")
                .duration("1")
                .durationUnit("H")
                .productList(orderProducts)
                .build();

        ActionResponse actionResponse = rabbitMqService.deleteEipConfig(reciveOrder);
        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void updateEipInfoConfig() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("100");
        OrderProductItem orderProductItem2 = new OrderProductItem();
        orderProductItem2.setCode("provider");
        orderProductItem2.setValue("BGP");
        OrderProductItem orderProductItem3 = new OrderProductItem();
        orderProductItem3.setCode("transfer");
        orderProductItem3.setValue("0");
        OrderProductItem orderProductItem4 = new OrderProductItem();
        orderProductItem4.setCode("IP");
        orderProductItem4.setValue("1");
        OrderProductItem orderProductItem5 = new OrderProductItem();
        orderProductItem5.setCode("is_SBW");
        orderProductItem5.setValue("no");
        orderProductItems.add(orderProductItem1);
        orderProductItems.add(orderProductItem2);
        orderProductItems.add(orderProductItem3);
        orderProductItems.add(orderProductItem4);
        orderProductItems.add(orderProductItem5);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("EIP")
                .productTypeCode("EIP")
                .instanceCount("1")
                .instanceId(eip.getId())
                .instanceStatus("success")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("06efa8b8-0d2a-8f29-eb32-868eacb00288")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token(TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"))
                .orderRoute("EIP")
                .consoleCustomization(null)
                .userId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72")
                .setCount("1")
                .billType("hourlySettlement")
                .orderType("changeConfigure")
                .duration("1")
                .durationUnit("H")
                .productList(orderProducts)
                .build();

        ActionResponse actionResponse = rabbitMqService.updateEipInfoConfig(reciveOrder);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void softDowOrDeleteEip() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        SoftDownInstance softDownInstance1 = SoftDownInstance.builder()
                .subFlowId("")
                .operateType("DELETE")
                .productLineCode("")
                .productTypeCode("")
                .instanceId(eip.getId())
                .result("")
                .statusTime("")
                .instanceStatus("")
                .build();
        Eip eip1 = creatEip(HsConstants.MONTHLY, null);
        SoftDownInstance softDownInstance2 = SoftDownInstance.builder()
                .subFlowId("")
                .operateType("stopServer")
                .productLineCode("")
                .productTypeCode("")
                .instanceId(eip1.getId())
                .result("")
                .statusTime("")
                .instanceStatus("")
                .build();
        SoftDownInstance softDownInstance3 = SoftDownInstance.builder()
                .subFlowId("")
                .operateType("resumeServer")
                .productLineCode("")
                .productTypeCode("")
                .instanceId(eip1.getId())
                .result("")
                .statusTime("")
                .instanceStatus("")
                .build();
        List<SoftDownInstance> softDownInstances = new ArrayList<>();
        softDownInstances.add(softDownInstance1);
        softDownInstances.add(softDownInstance2);
        softDownInstances.add(softDownInstance3);
        OrderSoftDown orderSoftDown = OrderSoftDown.builder()
                .region("cn-north-3")
                .flowId("")
                .instanceList(softDownInstances)
                .build();
        ActionResponse actionResponse = rabbitMqService.softDowOrDeleteEip(orderSoftDown);
        assertEquals(200,actionResponse.getCode());
    }



    /*
        SBW
     */

    @Test
    public void createSbwInfo() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("100");
        OrderProductItem orderProductItem2 = new OrderProductItem();
        orderProductItem2.setCode("sbwName");
        orderProductItem2.setValue("atomUnitTestNoOtherBuilder");
        orderProductItems.add(orderProductItem1);
        orderProductItems.add(orderProductItem2);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("SBW")
                .productTypeCode("SBW")
                .instanceCount("1")
                .instanceId(null)
                .instanceStatus("ACTIVE")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("06efa8b8-0d2a-8f29-eb32-868eacb00288")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token(TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"))
                .orderRoute("SBW")
                .consoleCustomization(null)
                .userId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72")
                .setCount("1")
                .billType("hourlySettlement")
                .orderType("new")
                .duration("1")
                .durationUnit("H")
                .productList(orderProducts)
                .build();

        ActionResponse sbwInfo = rabbitMqService.createSbwInfo(reciveOrder);
        List<Sbw> list = sbwDaoService.findByProjectId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSbwName().equals("atomUnitTestNoOtherBuilder")) {
                deleteSbw(list.get(i).getId());
                break;
            }
        }
        assertEquals(200,sbwInfo.getCode());
    }

    @Test
    public void deleteSbwConfig() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("100");
        orderProductItems.add(orderProductItem1);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("SBW")
                .productTypeCode("SBW")
                .instanceCount("1")
                .instanceId(sbw.getId())
                .instanceStatus("ACTIVE")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("8cf1c6d7-0556-d174-66b1-337dcf1de027")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token(TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"))
                .orderRoute("SBW")
                .consoleCustomization(null)
                .userId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72")
                .setCount("1")
                .billType("hourlySettlement")
                .orderType("new")
                .duration("1")
                .durationUnit("H")
                .productList(orderProducts)
                .build();

        ActionResponse actionResponse = rabbitMqService.deleteSbwConfig(reciveOrder);

        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void updateSbwInfoConfig() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("20");
        orderProductItems.add(orderProductItem1);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("SBW")
                .productTypeCode("SBW")
                .instanceCount("1")
                .instanceId(sbw.getId())
                .instanceStatus("ACTIVE")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("8cf1c6d7-0556-d174-66b1-337dcf1de027")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token(TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"))
                .orderRoute("SBW")
                .consoleCustomization(null)
                .userId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72")
                .setCount("1")
                .billType("hourlySettlement")
                .orderType("changeConfigure")
                .duration("1")
                .durationUnit("H")
                .productList(orderProducts)
                .build();

        ActionResponse actionResponse = rabbitMqService.updateSbwInfoConfig(reciveOrder);
        deleteSbw(sbw.getId());
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void renewSbwInfoConfig() throws Exception {
        Sbw sbw = creatSbw(HsConstants.MONTHLY, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //add itemLists
        List<OrderProductItem> orderProductItems = new ArrayList<>();
        OrderProductItem orderProductItem1 = new OrderProductItem();
        orderProductItem1.setCode("bandwidth");
        orderProductItem1.setValue("344");
        orderProductItems.add(orderProductItem1);

        OrderProduct orderProduct = OrderProduct.builder()
                .region("cn-north-3")
                .availableZone("")
                .productLineCode("SBW")
                .productTypeCode("SBW")
                .instanceCount("1")
                .instanceId(sbw.getId())
                .instanceStatus("ACTIVE")
                .statusTime(simpleDateFormat.format(new Date()))
                .itemList(orderProductItems)
                .build();
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        ReciveOrder reciveOrder = ReciveOrder.builder()
                .consoleOrderFlowId("8cf1c6d7-0556-d174-66b1-337dcf1de027")
                .orderStatus("paySuccess")
                .statusTime(simpleDateFormat.format(new Date()))
                .token(TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"))
                .orderRoute("SBW")
                .consoleCustomization(null)
                .userId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72")
                .setCount("1")
                .billType("monthly")
                .orderType("renew")
                .duration("1")
                .durationUnit("M")
                .productList(orderProducts)
                .build();

        ActionResponse actionResponse = rabbitMqService.updateSbwInfoConfig(reciveOrder);
        deleteSbw(sbw.getId());
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void softDowOrDeleteSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SoftDownInstance softDownInstance = SoftDownInstance.builder()
                .subFlowId("481718c1-d715-ba7a-5a14-be8b3c7fa663")
                .operateType("DELETE")
                .productLineCode("SBW")
                .productTypeCode("SBW")
                .instanceId(sbw.getId())
                .result("success")
                .statusTime(simpleDateFormat.format(new Date()))
                .instanceStatus("DELETE")
                .build();
        List<SoftDownInstance> softDownInstances = new ArrayList<>();
        softDownInstances.add(softDownInstance);

        OrderSoftDown orderSoftDown = OrderSoftDown.builder()
                .region("cn-north-3")
                .flowId("481718c1-d715-ba7a-5a14-be8b3c7fa663")
                .instanceList(softDownInstances)
                .build();

        ActionResponse actionResponse = rabbitMqService.softDowOrDeleteSbw(orderSoftDown);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void stopServerSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.MONTHLY, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SoftDownInstance softDownInstance = SoftDownInstance.builder()
                .subFlowId("481718c1-d715-ba7a-5a14-be8b3c7fa663")
                .operateType("stopServer")
                .productLineCode("SBW")
                .productTypeCode("SBW")
                .instanceId(sbw.getId())
                .result("success")
                .statusTime(simpleDateFormat.format(new Date()))
                .instanceStatus("STOP")
                .build();
        List<SoftDownInstance> softDownInstances = new ArrayList<>();
        softDownInstances.add(softDownInstance);

        OrderSoftDown orderSoftDown = OrderSoftDown.builder()
                .region("cn-north-3")
                .flowId("481718c1-d715-ba7a-5a14-be8b3c7fa663")
                .instanceList(softDownInstances)
                .build();

        ActionResponse actionResponse = rabbitMqService.softDowOrDeleteSbw(orderSoftDown);
        deleteSbw(sbw.getId());
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void resumeServerSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.MONTHLY, null);
        sbwDaoService.stopSbwService(sbw.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SoftDownInstance softDownInstance = SoftDownInstance.builder()
                .subFlowId("481718c1-d715-ba7a-5a14-be8b3c7fa663")
                .operateType("resumeServer")
                .productLineCode("SBW")
                .productTypeCode("SBW")
                .instanceId(sbw.getId())
                .result("success")
                .statusTime(simpleDateFormat.format(new Date()))
                .instanceStatus("ACTIVE")
                .build();
        List<SoftDownInstance> softDownInstances = new ArrayList<>();
        softDownInstances.add(softDownInstance);

        OrderSoftDown orderSoftDown = OrderSoftDown.builder()
                .region("cn-north-3")
                .flowId("481718c1-d715-ba7a-5a14-be8b3c7fa663")
                .instanceList(softDownInstances)
                .build();

        ActionResponse actionResponse = rabbitMqService.softDowOrDeleteSbw(orderSoftDown);
        deleteSbw(sbw.getId());
        assertEquals(200,actionResponse.getCode());
    }


    public Sbw creatSbw(String billType, String user) throws Exception {
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration("1");
        param.setBandwidth(10);
        param.setBillType(billType);
        param.setRegion("cn-north-3");
        param.setSbwName("sbwUnitTest");
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        if (user == "other")
            token = TokenUtil.getToken("xinjing", "1qaz2wsx3edc");
        Sbw sbwEntity = sbwDaoService.allocateSbw(param, token);
        return sbwEntity;
    }

    public void deleteSbw(String sbwId) throws Exception {
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        Sbw sbw = sbwDaoService.getSbwById(sbwId);
        if(sbw.getBillType().equals("hourlySettlement"))
            sbwDaoService.deleteSbw(sbwId, token);
        else
            sbwDaoService.adminDeleteSbw(sbwId);
    }

    public Eip creatEip(String billType, String user) throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(5);
        eipConfig.setBillType(billType);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        if (user == "other")
            token = TokenUtil.getToken("xinjing", "1qaz2wsx3edc");
        String operater = "unitTest";
        if (eipPoolRepository.getEipByRandom("BGP") == null) {
            return null;
        } else {
            EipPool eip = eipDaoService.getOneEipFromPool("BGP");
            Eip eipEntity = eipDaoService.allocateEip(eipConfig, eip, operater, token);
            return eipEntity;
        }
    }
}