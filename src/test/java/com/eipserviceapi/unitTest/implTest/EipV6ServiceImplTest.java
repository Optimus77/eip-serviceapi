package com.eipserviceapi.unitTest.implTest;

import com.eipserviceapi.TestEipServiceApplication;
import com.eipserviceapi.unitTest.TokenUtil;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.entity.ipv6.EipV6;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipPoolV6Repository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.EipV6DaoService;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.service.impl.EipV6ServiceImpl;
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
import javax.validation.constraints.Null;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EipV6ServiceImpl.class)
@Rollback
@SpringBootTest(classes = TestEipServiceApplication.class)
@Transactional(transactionManager = "transactionManager")
public class EipV6ServiceImplTest {
    @Autowired
    EipV6ServiceImpl eipV6ServiceImpl;
    @Autowired
    EipV6DaoService eipV6DaoService;
    @Autowired
    EipPoolV6Repository eipPoolV6Repository;
    @Autowired
    EipDaoService eipDaoService;
    @Autowired
    EipPoolRepository eipPoolRepository;
    @Autowired
    EipRepository eipRepository;
    @Autowired
    SbwDaoService sbwDaoService;

    @Before
    public void setUp() throws Exception {
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
    public void atomCreateEipV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        if (eipPoolV6Repository.getEipV6ByRandom() == null) {
            ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eip.getId(), token);
            assertEquals(HttpStatus.FAILED_DEPENDENCY, responseEntity.getStatusCode());
        } else {
            ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eip.getId(), token);
            eipV6ServiceImpl.atomDeleteEipV6(eip.getEipV6Id());
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        }
    }

    @Test
    public void atomCreateEipV6WithNoV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        while (eipPoolV6Repository.getEipV6ByRandom() != null) {
            eipV6DaoService.getOneEipFromPoolV6();
        }
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eip.getId(), token);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, responseEntity.getStatusCode());
    }

    @Test
    public void atomCreateEipV6InBind() throws Exception {
        /*String eipIdInBind = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eipIdInBind,token);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());*/
    }

    @Test
    public void eipErrorCreateEipV6Null() throws Exception {
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(null, token);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipErrorCreateEipV6WithSbw() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eip.getId(), token);
        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipErrorCreateEipV6WithV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(),token);
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eip.getId(), token);
        eipV6ServiceImpl.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN10S10() {
        int pageNo = 10;
        int pageSize = 10;
        String status = HsConstants.ACTIVE;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN1S1() {
        int pageNo = 1;
        int pageSize = 1;
        String status = null;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN10S0() {
        int pageNo = 10;
        int pageSize = 0;
        String status = HsConstants.ACTIVE;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN0S10() {
        int pageNo = 0;
        int pageSize = 10;
        String status = HsConstants.ACTIVE;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(),token);
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipV6Null() {
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipV6IdIsNull() {
        String eipV6Id = "";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(eipV6Id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipV6OfOtherUser() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, "other");
        String token = TokenUtil.getToken("xinjing", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(),token);
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(eip.getEipV6Id());
        eipV6DaoService.deleteEipV6(eip.getEipV6Id(),token);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void getEipV6Detail() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(),token);
        ResponseEntity responseEntity = eipV6ServiceImpl.getEipV6Detail(eip.getEipV6Id());
        eipV6ServiceImpl.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEipV6DetailNull() {
        String eipV6Id = "";
        ResponseEntity responseEntity = eipV6ServiceImpl.getEipV6Detail(eipV6Id);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getEipV6DetailDelete() {
        String eipV6IdDelete = "065a2b32-7043-4d7b-9a8b-6d91304a702b";
        ResponseEntity responseEntity = eipV6ServiceImpl.getEipV6Detail(eipV6IdDelete);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPort() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        Eip eip1 = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(),token);
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eip.getEipV6Id(), eip1.getEipAddress());
        eipV6ServiceImpl.atomDeleteEipV6(eip1.getEipV6Id());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPortNull() {
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort("123", null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPortV6Delete() {
        String eipV6IdDelete = "065a2b32-7043-4d7b-9a8b-6d91304a702b";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6IdDelete, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPortV4Null() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(),token);
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eip.getEipV6Id(), null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPortV6InBindToV4UnBind() {
        /*String eipV6Id = "6ca07302-a89f-4935-b982-a336670f2712";
        String eip = "10.110.38.118";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id, eip);
        eip = "10.110.38.218";
        eipV6ServiceImpl.eipV6bindPort(eipV6Id, eip);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());*/
    }

    @Test
    public void eipV6bindPortV6UnbindToV4InBind() {
        /*String eipV6Id = "b81f8d46-ffdb-436d-932e-7e00a6bf85ee";
        String eip = "10.110.38.30";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id, eip);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());*/
    }

    @Test
    public void eipV6bindPortInBindToInBind() {
        /*String eipV6Id = "6ca07302-a89f-4935-b982-a336670f2712";
        String eip = "10.110.38.30";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id, eip);
        eip = "10.110.38.218";
        eipV6ServiceImpl.eipV6bindPort(eipV6Id, eip);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());*/
    }

    @Test
    public void getEipv6ById() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6ServiceImpl.atomCreateEipV6(eip.getId(),token);
        EipV6 eipV6 = eipV6DaoService.getEipV6ById(eip.getEipV6Id());
        EipV6 eipV6Entity = eipV6ServiceImpl.getEipv6ById(eip.getEipV6Id());
        assertEquals(eipV6, eipV6Entity);
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
        if (eipPoolRepository.getEipByRandom("1") == null) {
            return null;
        } else {
            EipPool eip = eipDaoService.getOneEipFromPool("1");
            Eip eipEntity = eipDaoService.allocateEip(eipConfig, eip, operater, token);
            return eipEntity;
        }
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

    public void addEipToSbw(String eipId, String sbwId) throws Exception {
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam eipUpdateParam = new EipUpdateParam();
        Sbw sbw = sbwDaoService.getSbwById(sbwId);
        eipUpdateParam.setSbwId(sbwId);
        eipUpdateParam.setBandwidth(sbw.getBandWidth());
        sbwDaoService.addEipIntoSbw(eipId, eipUpdateParam, token);
    }

    public void removeEipFromSbw(String eipId, String sbwId) throws Exception {
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam eipUpdateParam = new EipUpdateParam();
        Eip eip = eipDaoService.getEipById(eipId);
        eipUpdateParam.setSbwId(sbwId);
        eipUpdateParam.setBandwidth(eip.getOldBandWidth());
        sbwDaoService.removeEipFromSbw(eipId, eipUpdateParam, token);
    }
}