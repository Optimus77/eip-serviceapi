//package com.inspur.eip.service.impl;
//
//import com.inspur.eip.EipServiceApplicationTests;
//import com.inspur.eip.entity.openapi.OpenCreateEip;
//import com.inspur.eip.service.TokenUtil;
//import com.inspur.eip.util.common.CommonUtil;
//import groovy.util.logging.Slf4j;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.*;
//import javax.servlet.http.*;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.security.Principal;
//import java.util.Collection;
//import java.util.Enumeration;
//import java.util.Locale;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//@Slf4j
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = OpenApiEipServiceImpl.class)
//@Rollback
//@SpringBootTest(classes = EipServiceApplicationTests.class)
//@Transactional
//public class OpenApiEipServiceImplTest {
//
//    @Autowired
//    private OpenApiEipServiceImpl openApiEipService;
//
//    @Before
//    public void setUp() {
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequest() {
//            @Override
//            public String getHeader(String name) {
//                try {
//                    return "bearer " + TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            public Object getAttribute(String name) {
//                return null;
//            }
//
//            @Override
//            public Enumeration<String> getAttributeNames() {
//                return null;
//            }
//
//            @Override
//            public String getCharacterEncoding() {
//                return null;
//            }
//
//            @Override
//            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
//
//            }
//
//            @Override
//            public int getContentLength() {
//                return 0;
//            }
//
//            @Override
//            public long getContentLengthLong() {
//                return 0;
//            }
//
//            @Override
//            public String getContentType() {
//                return null;
//            }
//
//            @Override
//            public ServletInputStream getInputStream() throws IOException {
//                return null;
//            }
//
//            @Override
//            public String getParameter(String name) {
//                return null;
//            }
//
//            @Override
//            public Enumeration<String> getParameterNames() {
//                return null;
//            }
//
//            @Override
//            public String[] getParameterValues(String name) {
//                return new String[0];
//            }
//
//            @Override
//            public Map<String, String[]> getParameterMap() {
//                return null;
//            }
//
//            @Override
//            public String getProtocol() {
//                return null;
//            }
//
//            @Override
//            public String getScheme() {
//                return null;
//            }
//
//            @Override
//            public String getServerName() {
//                return null;
//            }
//
//            @Override
//            public int getServerPort() {
//                return 0;
//            }
//
//            @Override
//            public BufferedReader getReader() throws IOException {
//                return null;
//            }
//
//            @Override
//            public String getRemoteAddr() {
//                return null;
//            }
//
//            @Override
//            public String getRemoteHost() {
//                return null;
//            }
//
//            @Override
//            public void setAttribute(String name, Object o) {
//
//            }
//
//            @Override
//            public void removeAttribute(String name) {
//
//            }
//
//            @Override
//            public Locale getLocale() {
//                return null;
//            }
//
//            @Override
//            public Enumeration<Locale> getLocales() {
//                return null;
//            }
//
//            @Override
//            public boolean isSecure() {
//                return false;
//            }
//
//            @Override
//            public RequestDispatcher getRequestDispatcher(String path) {
//                return null;
//            }
//
//            @Override
//            public String getRealPath(String path) {
//                return null;
//            }
//
//            @Override
//            public int getRemotePort() {
//                return 0;
//            }
//
//            @Override
//            public String getLocalName() {
//                return null;
//            }
//
//            @Override
//            public String getLocalAddr() {
//                return null;
//            }
//
//            @Override
//            public int getLocalPort() {
//                return 0;
//            }
//
//            @Override
//            public ServletContext getServletContext() {
//                return null;
//            }
//
//            @Override
//            public AsyncContext startAsync() throws IllegalStateException {
//                return null;
//            }
//
//            @Override
//            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
//                return null;
//            }
//
//            @Override
//            public boolean isAsyncStarted() {
//                return false;
//            }
//
//            @Override
//            public boolean isAsyncSupported() {
//                return false;
//            }
//
//            @Override
//            public AsyncContext getAsyncContext() {
//                return null;
//            }
//
//            @Override
//            public DispatcherType getDispatcherType() {
//                return null;
//            }
//
//            @Override
//            public String getAuthType() {
//                return null;
//            }
//
//            @Override
//            public Cookie[] getCookies() {
//                return new Cookie[0];
//            }
//
//            @Override
//            public long getDateHeader(String name) {
//                return 0;
//            }
//
//
//            @Override
//            public Enumeration<String> getHeaders(String name) {
//                return null;
//            }
//
//            @Override
//            public Enumeration<String> getHeaderNames() {
//                return null;
//            }
//
//            @Override
//            public int getIntHeader(String name) {
//                return 0;
//            }
//
//            @Override
//            public String getMethod() {
//                return null;
//            }
//
//            @Override
//            public String getPathInfo() {
//                return null;
//            }
//
//            @Override
//            public String getPathTranslated() {
//                return null;
//            }
//
//            @Override
//            public String getContextPath() {
//                return null;
//            }
//
//            @Override
//            public String getQueryString() {
//                return null;
//            }
//
//            @Override
//            public String getRemoteUser() {
//                return null;
//            }
//
//            @Override
//            public boolean isUserInRole(String role) {
//                return false;
//            }
//
//            @Override
//            public Principal getUserPrincipal() {
//                return null;
//            }
//
//            @Override
//            public String getRequestedSessionId() {
//                return null;
//            }
//
//            @Override
//            public String getRequestURI() {
//                return null;
//            }
//
//            @Override
//            public StringBuffer getRequestURL() {
//                return null;
//            }
//
//            @Override
//            public String getServletPath() {
//                return null;
//            }
//
//            @Override
//            public HttpSession getSession(boolean create) {
//                return null;
//            }
//
//            @Override
//            public HttpSession getSession() {
//                return null;
//            }
//
//            @Override
//            public String changeSessionId() {
//                return null;
//            }
//
//            @Override
//            public boolean isRequestedSessionIdValid() {
//                return false;
//            }
//
//            @Override
//            public boolean isRequestedSessionIdFromCookie() {
//                return false;
//            }
//
//            @Override
//            public boolean isRequestedSessionIdFromURL() {
//                return false;
//            }
//
//            @Override
//            public boolean isRequestedSessionIdFromUrl() {
//                return false;
//            }
//
//            @Override
//            public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
//                return false;
//            }
//
//            @Override
//            public void login(String username, String password) throws ServletException {
//
//            }
//
//            @Override
//            public void logout() throws ServletException {
//
//            }
//
//            @Override
//            public Collection<Part> getParts() throws IOException, ServletException {
//                return null;
//            }
//
//            @Override
//            public Part getPart(String name) throws IOException, ServletException {
//                return null;
//            }
//
//            @Override
//            public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
//                return null;
//            }
//        }));
//    }
//
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void openapiCreateEip() throws Exception {
//        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        //openCreateEip.setDuration("1");
//        openCreateEip.setBillType("hourlySettlement");
//        openCreateEip.setBandwidth("5");
//
//        ResponseEntity responseEntity = openApiEipService.OpenapiCreateEip(openCreateEip, token);
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//
//    }
//
//    @Test
//    public void openapiCreateEipAddSbw() throws Exception {
//        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        openCreateEip.setBandwidth("6");
//        openCreateEip.setSbwId("");
//        ResponseEntity responseEntity = openApiEipService.OpenapiCreateEipAddSbw(openCreateEip,token);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void openapiDeleteEip() throws Exception {
//        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        openCreateEip.setEipId("");
//        ResponseEntity responseEntity = openApiEipService.OpenapiDeleteEip(openCreateEip, token);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void openapiRenewEip() throws Exception {
//        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        openCreateEip.setEipId("");
//        openCreateEip.setDuration("2");
//        ResponseEntity responseEntity = openApiEipService.OpenapiRenewEip(openCreateEip,token);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void openapiEipupdateBandwidth() throws Exception {
//        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        openCreateEip.setBandwidth("6");
//        openCreateEip.setEipId("");
//        ResponseEntity responseEntity = openApiEipService.OpenapiEipupdateBandwidth(openCreateEip,token);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void openapicreateIptsBandEip() throws Exception {
//        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
//        OpenCreateEip openCreateEip = new OpenCreateEip();
//        openCreateEip.setBandwidth("6");
//        ResponseEntity responseEntity = openApiEipService.OpenapicreateIptsBandEip(openCreateEip,token);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }
//}