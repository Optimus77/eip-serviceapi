package com.inspur.eip.service.impl;

import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.service.TokenUtil;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.HsConstants;
import groovy.util.logging.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.io.*;
import java.security.Principal;
import java.util.*;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EipServiceImpl.class)
@Rollback
@SpringBootTest(classes = EipServiceApplicationTests.class)
@Transactional
public class EipServiceImplTest {

    @Autowired
    EipServiceImpl eipServiceImpl;

    @Autowired
    EipDaoService eipDaoService;

    @Autowired
    EipPoolRepository eipPoolRepository;

    @Autowired
    EipV6ServiceImpl eipV6Service;

    @Autowired
    EipRepository eipRepository;

    @Autowired
    EipV6ServiceImpl eipV6ServiceImpl;

    @Before
    public void setUp() {
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
    public void atomCreateEip() throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        String operater = "unitTest";
        if (eipPoolRepository.getEipByRandom(eipConfig.getIpType()) == null) {
            ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
            assertEquals(HttpStatus.FAILED_DEPENDENCY, responseEntity.getStatusCode());
        } else {
            ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        }
    }

    @Test
    public void atomCreateEipWrongRegion() throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        String operater = "unitTest";
        ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void atomCreateEipWrongEipPool() throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(100);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("123");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        String operater = "unitTest";
        ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, responseEntity.getStatusCode());
    }

    @Test
    public void atomCreateEipWrongSbw() throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(100);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId("123abc");
        eipConfig.setDuration("1");
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        String operater = "unitTest";
        ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, responseEntity.getStatusCode());
    }

    @Test
    public void atomCreateEipGroup() throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        EipAllocateParam eipConfig1 = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        List<EipAllocateParam> eipconfigs = new ArrayList<>();
        eipconfigs.add(eipConfig);
        eipconfigs.add(eipConfig1);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        String operater = "unitTest";
        ResponseEntity responseEntity = eipServiceImpl.atomCreateEipGroup(eipconfigs, token, operater);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void atomCreateEipGroupWrongSbw() throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId("123abc");
        eipConfig.setDuration("1");
        EipAllocateParam eipConfig1 = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId("123abc");
        eipConfig.setDuration("1");
        List<EipAllocateParam> eipconfigs = new ArrayList<>();
        eipconfigs.add(eipConfig);
        eipconfigs.add(eipConfig1);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        String operater = "unitTest";
        ResponseEntity responseEntity = eipServiceImpl.atomCreateEipGroup(eipconfigs, token, operater);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, responseEntity.getStatusCode());
    }

    @Test
    public void atomCreateEipGroupWrongEipPool() throws Exception {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("123");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        EipAllocateParam eipConfig1 = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(10);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("123");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        List<EipAllocateParam> eipconfigs = new ArrayList<>();
        eipconfigs.add(eipConfig);
        eipconfigs.add(eipConfig1);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        String operater = "unitTest";
        ResponseEntity responseEntity = eipServiceImpl.atomCreateEipGroup(eipconfigs, token, operater);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, responseEntity.getStatusCode());
    }


    @Test
    public void atomDeleteEip() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eip.getId(), "");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipNull() {
        Eip eip = new Eip();
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eip.getId(), "");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipAlreadyDelete() {
        String eipId = "40c0a50d-c068-4d86-8c83-57cc5e690a74";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eipId, "");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipMonthlyDelete() throws Exception {
        Eip eip = creatEip(HsConstants.MONTHLY, null);
        //String eipId = "e72da25a-f1e6-4603-8432-d0de7edf3d87";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eip.getId(), null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void otherUserDelete() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, "other");
        //String eipId = "9659c812-d006-49a2-98b8-168f95bfae12";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eip.getId(), "");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    /*@Test
    public void bindEipDelete() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setPrivateIpAddress("1.2.3.4");
        eip.setFloatingIp("1.2.3.4");
        eip.setFloatingIpId("3338a9df-3df0-400c-8fe7-7563d9c1e749");
        eip.setPipId("10.110.26.0");
        eip.setDnatId("1.2.3.4");
        eip.setSnatId("1.2.3.4");
        eip.setInstanceId("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        eip.setInstanceType("1");
        eip.setPortId("928fe9a9-0df7-4147-bb15-0c455d7b06d5");
        eip.setStatus(HsConstants.ACTIVE);
        eip.setUpdatedTime(CommonUtil.getGmtDate());
        eipRepository.saveAndFlush(eip);
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eip.getId(), "");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }*/

    @Test
    public void atomDeleteEipGroup() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEipGroup(eip.getGroupId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void deleteEipList() throws Exception {
        Eip eip1 = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        Eip eip2 = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        List<String> eipIds = Arrays.asList(eip1.getId(), eip2.getId());
        ResponseEntity responseEntity = eipServiceImpl.deleteEipList(eipIds);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void deleteEipListNull() {
        String eipId1 = "";
        String eipId2 = "";
        List<String> eipIds = Arrays.asList(eipId1, eipId2);
        ResponseEntity responseEntity = eipServiceImpl.deleteEipList(eipIds);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void listEips() {
        ResponseEntity responseEntity = eipServiceImpl.listEips(0, 1, "DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsCurrentPageIsNotZero() {
        ResponseEntity responseEntity = eipServiceImpl.listEips(1, 1, "DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsLimitIsZero() {
        ResponseEntity responseEntity = eipServiceImpl.listEips(2, 0, "DOWN");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsByGroup() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsByGroup(0, 1, "DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsByGroupCurrentPageIsNotZero() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsByGroup(1, 1, "DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsByGroupLimitIstZero() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsByGroup(1, 0, "DOWN");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsV() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsV(0, 1, "DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsVCurrentPageIsNotZero() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsV(1, 1, "DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsVLimitIsZero() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsV(2, 0, "DOWN");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetail() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = eipServiceImpl.getEipDetail(eip.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailIpV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eipV6Service.atomCreateEipV6(eip.getId(), TokenUtil.getToken("lishenghao", "1qaz2wsx3edc"));
        ResponseEntity responseEntity = eipServiceImpl.getEipDetail(eip.getId());
        eipV6Service.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailNotFound() {
        String eipIdNotFound = "";
        ResponseEntity responseEntity = eipServiceImpl.getEipDetail(eipIdNotFound);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipGroupDetail() throws Exception {
        Eip eip1 = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        Eip eip2 = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip1.setGroupId("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        eipRepository.saveAndFlush(eip1);
        eip2.setGroupId("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        eipRepository.saveAndFlush(eip2);
        ResponseEntity responseEntity = eipServiceImpl.getEipGroupDetail("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipGroupDetailWithIpV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setGroupId("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        eipRepository.saveAndFlush(eip);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(), token);
        ResponseEntity responseEntity = eipServiceImpl.getEipGroupDetail("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        eipV6ServiceImpl.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipGroupDetailNull() throws Exception {
        ResponseEntity responseEntity = eipServiceImpl.getEipGroupDetail("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipByInstanceId() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setPrivateIpAddress("1.2.3.4");
        eip.setFloatingIp("1.2.3.4");
        eip.setFloatingIpId("3338a9df-3df0-400c-8fe7-7563d9c1e749");
        eip.setPipId("10.110.26.0");
        eip.setDnatId("1.2.3.4");
        eip.setSnatId("1.2.3.4");
        eip.setInstanceType("1");
        eip.setPortId("928fe9a9-0df7-4147-bb15-0c455d7b06d5");
        eip.setStatus(HsConstants.ACTIVE);
        eip.setUpdatedTime(CommonUtil.getGmtDate());
        eip.setInstanceId("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        eipRepository.saveAndFlush(eip);
        ResponseEntity responseEntity = eipServiceImpl.getEipByInstanceIdV2("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipByInstanceIdNull() {
        ResponseEntity responseEntity = eipServiceImpl.getEipByInstanceIdV2("");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipByInstanceIdWithoutEip() {
        String instanceIdWithoutEip = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        ResponseEntity responseEntity = eipServiceImpl.getEipByInstanceIdV2(instanceIdWithoutEip);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipGroupByIpAddress() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setGroupId("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        eipRepository.saveAndFlush(eip);
        ResponseEntity responseEntity = eipServiceImpl.getEipGroupByIpAddress(eip.getEipAddress());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipGroupByIpAddressGroupIdIsNull() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setGroupId(null);
        eipRepository.saveAndFlush(eip);
        ResponseEntity responseEntity = eipServiceImpl.getEipGroupByIpAddress(eip.getEipAddress());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipGroupByIpAddressWithIpV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setGroupId("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
        eipRepository.saveAndFlush(eip);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(), token);
        ResponseEntity responseEntity = eipServiceImpl.getEipGroupByIpAddress(eip.getEipAddress());
        eipV6ServiceImpl.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipGroupByIpAddressEipIsNull() throws Exception {
        ResponseEntity responseEntity = eipServiceImpl.getEipGroupByIpAddress(null);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipByIpAddressV2() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = eipServiceImpl.getEipByIpAddressV2(eip.getEipAddress());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipByIpAddressV2EipIsNull() throws Exception {
        ResponseEntity responseEntity = eipServiceImpl.getEipByIpAddressV2(null);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipByIpAddress() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = eipServiceImpl.getEipByIpAddress(eip.getEipAddress());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipByIpAddressOfOtherUser() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, "other");
        ResponseEntity responseEntity = eipServiceImpl.getEipByIpAddress(eip.getEipAddress());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstance() throws Exception {
     /*   Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        //NeutronService neutronService = PowerMockito.mock(NeutronService.class);
        //PowerMockito.when(neutronService.createAndAssociateWithFip(anyString(), anyString(), anyString(), any(Eip.class), anyString())).thenReturn(netFloatingIP);
        //mock普通方法，不行
        //PowerMockito.doReturn(netFloatingIP).when(neutronService).createAndAssociateWithFip(anyString(), anyString(), anyString(), any(Eip.class), anyString());
        //firewallService = mock(FirewallService.class);
        //when(firewallService.addNatAndQos(any(Eip.class), anyString(), anyString(), anyInt(), anyString())).thenReturn(MethodReturn.builder().httpCode(200).innerCode(ReturnStatus.SC_OK).eip(eip).build());
        String ecsId = "20d00e01-afa5-4e8c-a272-77a27b4773f2";
        String ecsPortId = "928fe9a9-0df7-4147-bb15-0c455d7b06d5";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eip.getId(), "1", ecsId, ecsPortId, null);
        eip = eipDaoService.getEipById(eip.getId());
        if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
            firewallService.delNatAndQos(eip);
            neutronService.disassociateAndDeleteFloatingIp(eip.getFloatingIp(),
                    eip.getFloatingIpId(), ecsId, eip.getRegion());
        }
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());*/
    }

    @Test
    public void eipBindWithInstanceServerIsNull() {
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(null, null, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceInBind() {
        /*String eipId = "3af556cb-beb1-4cc4-9357-3aaa209a358b";
        String type = HsConstants.ECS;
        String ecsId = "e61994c5-bba0-4831-b847-022d5d01b697";
        String ecsPortId = "e331525b-11ce-415c-98d0-bcbff0e755ee";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eipId, type, ecsId, ecsPortId, null);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());*/
    }

    @Test
    public void eipBindWithInstanceEipNull() {
        String eipId = "123";
        String type = "1";
        String ecsId = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        String ecsPortId = "fe84a64d-8765-497c-8dfe-f8fcb0750d88";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eipId, type, ecsId, ecsPortId, null);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceEipInBind() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setPrivateIpAddress("1.2.3.4");
        eip.setFloatingIp("1.2.3.4");
        eip.setFloatingIpId("3338a9df-3df0-400c-8fe7-7563d9c1e749");
        eip.setPipId("10.110.26.0");
        eip.setDnatId("1.2.3.4");
        eip.setSnatId("1.2.3.4");
        eip.setInstanceId("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        eip.setInstanceType("1");
        eip.setPortId("928fe9a9-0df7-4147-bb15-0c455d7b06d5");
        eip.setStatus(HsConstants.ACTIVE);
        eip.setUpdatedTime(CommonUtil.getGmtDate());
        eipRepository.saveAndFlush(eip);
        String type = "1";
        String ecsId = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        String ecsPortId = "fe84a64d-8765-497c-8dfe-f8fcb0750d88";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eip.getId(), type, ecsId, ecsPortId, null);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceEipOfOtherUser() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, "other");
        String type = "1";
        String ecsId = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        String ecsPortId = "fe84a64d-8765-497c-8dfe-f8fcb0750d88";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eip.getId(), type, ecsId, ecsPortId, null);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void eipUnbindWithInstacnce() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        eip.setPrivateIpAddress("1.2.3.4");
        eip.setFloatingIp(null);
        eip.setFloatingIpId("3338a9df-3df0-400c-8fe7-7563d9c1e749");
        eip.setPipId(null);
        eip.setDnatId(null);
        eip.setSnatId(null);
        eip.setInstanceId("20d00e01-afa5-4e8c-a272-77a27b4773f2");
        eip.setInstanceType("1");
        eip.setPortId("928fe9a9-0df7-4147-bb15-0c455d7b06d5");
        eip.setFirewallId("3f26960e-6ac3-4636-aae7-822ef3579288");
        eip.setStatus(HsConstants.ACTIVE);
        eip.setUpdatedTime(CommonUtil.getGmtDate());
        eipRepository.saveAndFlush(eip);

        ResponseEntity responseEntity = eipServiceImpl.eipUnbindWithInstacnce(eip.getId());

        /*if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            //neutronService.associaInstanceWithFloatingIp(eip, eip.getInstanceId(), eip.getPortId());
            firewallService.addNatAndQos(eip, eip.getFloatingIp(), eip.getEipAddress(), eip.getBandWidth(), eip.getFirewallId());
        }*/
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void eipUnbindWithInstacnceNull() {
        ResponseEntity responseEntity = eipServiceImpl.eipUnbindWithInstacnce(null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipUnbindWithInstacnceEipUnBind() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = eipServiceImpl.eipUnbindWithInstacnce(eip.getId());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipUnbindWithInstacnceOfOtherUser() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, "other");
        ResponseEntity responseEntity = eipServiceImpl.eipUnbindWithInstacnce(eip.getId());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void getEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getEipCount();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipStatistics() {
        ResponseEntity responseEntity = eipServiceImpl.getEipStatistics();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getFreeEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getFreeEipCount();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getUsingEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getUsingEipCount();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getUsingEipCountByStatus() {
        ResponseEntity responseEntity = eipServiceImpl.getUsingEipCountByStatus("DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getTotalEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getTotalEipCount();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipsByBandWidth() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsByBandWidth("DOWN");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }


    @Test
    public void getEipDetailsByIpAddress() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = eipServiceImpl.getEipDetailsByIpAddress(eip.getEipAddress());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailsByIpAddressNull() {
        ResponseEntity responseEntity = eipServiceImpl.getEipDetailsByIpAddress(null);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailsByIpAddressEipIsNull() {
        String eipNull = "111.222.333.444";
        ResponseEntity responseEntity = eipServiceImpl.getEipDetailsByIpAddress(eipNull);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
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
        eipConfig.setGroupId("1qaz2wsx-3edc-4rfv-5tgb-6yhn7ujm8ik9");
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