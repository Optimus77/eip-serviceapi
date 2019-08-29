package com.inspur.eip.service;

import com.inspur.eip.TestEipServiceApplication;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.service.impl.EipV6ServiceImpl;
import com.inspur.eip.util.constant.HsConstants;
import groovy.util.logging.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EipDaoService.class)
@Rollback
@SpringBootTest(classes = TestEipServiceApplication.class)
@Transactional

public class EipDaoServiceTest {

    @Autowired
    EipRepository eipRepository;

    @Autowired
    EipPoolRepository eipPoolRepository;

    @Autowired
    NeutronService neutronService;

    @Autowired
    EipDaoService eipDaoService;

    @Autowired
    EipV6ServiceImpl eipV6Service;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequest() {


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
    public void allocateEip() {
    }

    @Test
    public void deleteEip() {
    }


    @Test
    public void adminDeleteEip() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eip.getId());
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipNull() {
        String eipId = "123";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipAlreadyDelete() {
        String eipId = "453a3f69-da30-470a-af3b-fe57ebea9326";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipInBind() {
        /*String eipId = "18c95fef-11ff-4e6e-8a1e-7a00949f9410";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200, actionResponse.getCode());*/
    }

    @Test
    public void adminDeleteEipWithIpV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eipV6Service.atomCreateEipV6(eip.getId(),TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eip.getId());
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void softDownEip() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ActionResponse actionResponse = eipDaoService.softDownEip(eip.getId());
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void softDownEipNotFound() {
        String eipId = "123";
        ActionResponse actionResponse = eipDaoService.softDownEip(eipId);
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void associateInstanceWithEip() {
    }

    @Test
    public void disassociateInstanceWithEip() {
    }

    @Test
    public void updateEipEntity() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eip.getId(), param, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void updateEipEntityNotFound() throws Exception {

        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity("123", param, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void updateEipEntityWithIpV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eipV6Service.atomCreateEipV6(eip.getId(),TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eip.getId(), param, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        eipV6Service.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void updateEipEntityOfOtherUser() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, "other");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eip.getId(), param, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(403, actionResponse.getCode());
    }

    @Test
    public void updateEipEntityMonthly() throws Exception {
        Eip eip = creatEip(HsConstants.MONTHLY, null);
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(0);
        param.setBillType(HsConstants.MONTHLY);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eip.getId(), param, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(400, actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eip.getId(), addTime);
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void reNewEipEntityInBind() {
        /*String eipId = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId, addTime);
        assertEquals(200, actionResponse.getCode());*/
    }

    @Test
    public void reNewEipEntityNotFound() {
        String eipId = "123";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId, addTime);
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity1() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eip.getId(), addTime, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity1InBind() {
        /*Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId, addTime, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(200, actionResponse.getCode());*/
    }

    @Test
    public void reNewEipEntity1NotFound() throws Exception {
        String eipId = "123";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId, addTime, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity1OfOtherUser() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, "other");
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eip.getId(), addTime, TokenUtil.getToken("lishenghao","1qaz2wsx3edc"));
        assertEquals(403, actionResponse.getCode());
    }

    @Test
    public void findByProjectId() {
    }

    @Test
    public void findByEipAddress() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        Eip eipEntity = eipDaoService.findByEipAddress(eip.getEipAddress());
        assertEquals(eip, eipEntity);
    }

    @Test
    public void findByInstanceId() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setInstanceId("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        eipRepository.saveAndFlush(eip);
        Eip eipEntity = eipDaoService.findByInstanceId("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        assertEquals(eip, eipEntity);
    }

    @Test
    public void getEipById() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        Eip eipEntity = eipDaoService.getEipById(eip.getId());
        assertEquals(eip, eipEntity);
    }

    @Test
    public void getInstanceNum() {
        String userId = "9d0b67cd-20cb-40b4-8dc4-b0415ca25d72";
        long instanceNum = eipDaoService.getInstanceNum(userId);
        assertThat(instanceNum, instanceOf(long.class));
    }

    @Test
    public void getFreeEipCount() {
        int freeEipCount = eipDaoService.getFreeEipCount();
        assertThat(freeEipCount, instanceOf(int.class));

    }

    @Test
    public void getUsingEipCount() {
        int usingEipCount = eipDaoService.getUsingEipCount();
        assertThat(usingEipCount, instanceOf(int.class));
    }

    @Test
    public void getTotalBandWidth() {
        int totalBandWidth = eipDaoService.getTotalBandWidth();
        assertThat(totalBandWidth, instanceOf(int.class));

    }

    @Test
    public void getUsingEipCountByStatus() {
        int getUsingEipCountByStatus = eipDaoService.getUsingEipCountByStatus("DOWN");
        assertThat(getUsingEipCountByStatus, instanceOf(int.class));
    }

    @Test
    public void getOneEipFromPool() {
        EipPool eipPool = eipDaoService.getOneEipFromPool();
        assertThat(eipPool, instanceOf(EipPool.class));
    }

    @Test
    public void getDuplicateEip() {
    }

    @Test
    public void getDuplicateEipFromPool() {
    }

    @Test
    public void get() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setInstanceId("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        eipRepository.saveAndFlush(eip);
        Eip eipEntity = eipDaoService.get("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        assertEquals(eip, eipEntity);
    }

    @Test
    public void statisEipCountBySbw() {
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
        String token = TokenUtil.getToken("lishenghao","1qaz2wsx3edc");
        if (user == "other")
            token = TokenUtil.getToken("xinjing","1qaz2wsx3edc");
        String operater = "unitTest";
        if (eipPoolRepository.getEipByRandom() == null) {
            return null;
        } else {
            EipPool eip = eipDaoService.getOneEipFromPool();
            Eip eipEntity = eipDaoService.allocateEip(eipConfig, eip, operater, token);
            return eipEntity;
        }
    }

}