package com.inspur.eip.service;

import com.inspur.eip.TestEipServiceApplication;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.service.impl.EipV6ServiceImpl;
import com.inspur.eip.util.constant.HsConstants;
import groovy.util.logging.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EipV6DaoService.class)
@Rollback
@SpringBootTest(classes = TestEipServiceApplication.class)
@Transactional
public class EipV6DaoServiceTest {

    @Autowired
    EipV6DaoService eipV6DaoService;
    @Autowired
    EipPoolRepository eipPoolRepository;
    @Autowired
    EipDaoService eipDaoService;
    @Autowired
    EipV6ServiceImpl eipV6Service;

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
    public void allocateEipV6() {
    }

    @Test
    public void getOneEipFromPoolV6() {
    }

    @Test
    public void findEipV6ByUserId() {
    }

    @Test
    public void findByEipV6IdAndIsDelete() {
    }

    @Test
    public void deleteEipV6() {
    }

    @Test
    public void adminDeleteEipV6() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6Service.atomCreateEipV6(eip.getId(),token);
        ActionResponse actionResponse = eipV6DaoService.adminDeleteEipV6(eip.getEipV6Id());
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipV6NotFound(){
        String eipV6Id = "";
        ActionResponse actionResponse = eipV6DaoService.adminDeleteEipV6(eipV6Id);
        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void bindIpv6WithInstance() {
    }

    @Test
    public void unBindIpv6WithInstance() {
    }

    @Test
    public void getEipV6ById() {
    }

    @Test
    public void updateIp() {
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