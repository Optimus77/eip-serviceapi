package com.eipserviceapi.unitTest.implTest;

import com.eipserviceapi.TestEipServiceApplication;
import com.inspur.eip.repository.EipPoolV6Repository;
import com.inspur.eip.service.EipV6DaoService;
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

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
    private EipPoolV6Repository eipPoolV6Repository;

    @Before
    public void setUp() throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequest(){

            @Override
            public String getHeader(String name) {
                //todo 测试之前摘取token
                return "bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiI0ODI3NzJjMi02YTRkLTRlNzUtOTBlNS1jNjA0YzkyOWJjZWEiLCJleHAiOjE1NjM0MTcwNDMsIm5iZiI6MCwiaWF0IjoxNTYzNDExNjQzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6Ijg3NmMxMDFlLTYzZDktNGYwZS04MTYwLTM1NzY4ZGViMzQzNCIsImF1dGhfdGltZSI6MTU2MzQxMTYzMCwic2Vzc2lvbl9zdGF0ZSI6ImFkNTg0ZWRmLWI4OTAtNDVhMS1hNzhhLTRkNDhhOTJkNWU4MSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.Q_7heuF3pUvpIfV_fS7_SLrTVkZFhGT9wR8a9qVH0oZGQwNcJnfsJzNh9bs0Ul8ZZ8-QEe3MHPuq3IBBxSwIVFWS1D6n3rJAP6MAhA4U5NvlOaGzYZ2vQKsCWSlEoST7zp38zxumhTZcS-2G7zT0ky6AGmWKhY0NU--nvCgVjkBdRnq8VQd2VjOKt8FgopNNx7t_OAUImLsTu4C-C6zTvZYQAz40mKJAGheiNZ5b4eEhMe0Nxjw5dSNdqcWlgj3CMuMq-PFiPExNVbpO2VP6pglw3LcQ577o_RQcDe_TKLgR26Rlojajw8HgGzDyYlW8g9JpQpTCYW9wVJcycPDAeg";
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
    public void atomCreateEipV6() {
        String eipId = "7216e894-6a79-4320-b1db-b4ec1fb8d2e0";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkODM0OTkwNS1iYzcwLTQxZjQtOWJlMC0wNWQ3Mjk5OTVkODYiLCJleHAiOjE1NjEwMDU0MzMsIm5iZiI6MCwiaWF0IjoxNTYxMDAwMDMzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjA0MzQ0NWJlLWE3ZmEtNDZmNy05OTkwLWNhMWQ5ZmRlYmJjNiIsImF1dGhfdGltZSI6MTU2MDk5ODIzMSwic2Vzc2lvbl9zdGF0ZSI6ImNiMjJjYTQ2LTA2NjctNDVkYi04NzNiLTNjZjhhOGRkMmIzMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.knnSyiRFfH3J3URnTygvs4e-2rxh8U7HWmagtGZ3FUg-j_w37e50Z8gFOBoaoOMChl3IEoYtBSJXk9nj_AlCYZqZ3QZET2fsuoB0ERwoUXtyK9uZOPR1PaRAfLURRTBbui5MtbUZ8nni3esbz01DeJaWtjo22Dx1VhdAUQwPRulu2td2InkGO-_HvLhgLv173a5mEnQsH2_nSl9m8axFLeM9kz_Tr6Xb9MecTny_y8XWy2hxF4ihnq5AYRhEnTATUvJxAoYc6aJYCs-cxxcKHnqp7n17r3UbVa1E1O6wZ3P5xBWLnBp-GVM8bkekJ_WCcYGFMZ2Ev2gLa6Unb_oXFw";
        if(eipPoolV6Repository.getEipV6ByRandom()==null)
        {
            ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eipId,token);
            assertEquals(HttpStatus.FAILED_DEPENDENCY,responseEntity.getStatusCode());
        }
        else {
            ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eipId,token);
            assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        }
    }
    @Test
    public void atomCreateEipV6WithNoV6(){
        String eipId = "7216e894-6a79-4320-b1db-b4ec1fb8d2e0";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkODM0OTkwNS1iYzcwLTQxZjQtOWJlMC0wNWQ3Mjk5OTVkODYiLCJleHAiOjE1NjEwMDU0MzMsIm5iZiI6MCwiaWF0IjoxNTYxMDAwMDMzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjA0MzQ0NWJlLWE3ZmEtNDZmNy05OTkwLWNhMWQ5ZmRlYmJjNiIsImF1dGhfdGltZSI6MTU2MDk5ODIzMSwic2Vzc2lvbl9zdGF0ZSI6ImNiMjJjYTQ2LTA2NjctNDVkYi04NzNiLTNjZjhhOGRkMmIzMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.knnSyiRFfH3J3URnTygvs4e-2rxh8U7HWmagtGZ3FUg-j_w37e50Z8gFOBoaoOMChl3IEoYtBSJXk9nj_AlCYZqZ3QZET2fsuoB0ERwoUXtyK9uZOPR1PaRAfLURRTBbui5MtbUZ8nni3esbz01DeJaWtjo22Dx1VhdAUQwPRulu2td2InkGO-_HvLhgLv173a5mEnQsH2_nSl9m8axFLeM9kz_Tr6Xb9MecTny_y8XWy2hxF4ihnq5AYRhEnTATUvJxAoYc6aJYCs-cxxcKHnqp7n17r3UbVa1E1O6wZ3P5xBWLnBp-GVM8bkekJ_WCcYGFMZ2Ev2gLa6Unb_oXFw";
        while (eipPoolV6Repository.getEipV6ByRandom()!=null)
        {
            eipV6DaoService.getOneEipFromPoolV6();
        }
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eipId,token);
        assertEquals(HttpStatus.FAILED_DEPENDENCY,responseEntity.getStatusCode());
    }
    @Test
    public void atomCreateEipV6InBind(){
        String eipIdInBind = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkODM0OTkwNS1iYzcwLTQxZjQtOWJlMC0wNWQ3Mjk5OTVkODYiLCJleHAiOjE1NjEwMDU0MzMsIm5iZiI6MCwiaWF0IjoxNTYxMDAwMDMzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjA0MzQ0NWJlLWE3ZmEtNDZmNy05OTkwLWNhMWQ5ZmRlYmJjNiIsImF1dGhfdGltZSI6MTU2MDk5ODIzMSwic2Vzc2lvbl9zdGF0ZSI6ImNiMjJjYTQ2LTA2NjctNDVkYi04NzNiLTNjZjhhOGRkMmIzMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.knnSyiRFfH3J3URnTygvs4e-2rxh8U7HWmagtGZ3FUg-j_w37e50Z8gFOBoaoOMChl3IEoYtBSJXk9nj_AlCYZqZ3QZET2fsuoB0ERwoUXtyK9uZOPR1PaRAfLURRTBbui5MtbUZ8nni3esbz01DeJaWtjo22Dx1VhdAUQwPRulu2td2InkGO-_HvLhgLv173a5mEnQsH2_nSl9m8axFLeM9kz_Tr6Xb9MecTny_y8XWy2hxF4ihnq5AYRhEnTATUvJxAoYc6aJYCs-cxxcKHnqp7n17r3UbVa1E1O6wZ3P5xBWLnBp-GVM8bkekJ_WCcYGFMZ2Ev2gLa6Unb_oXFw";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eipIdInBind,token);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void eipErrorCreateEipV6Null(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkODM0OTkwNS1iYzcwLTQxZjQtOWJlMC0wNWQ3Mjk5OTVkODYiLCJleHAiOjE1NjEwMDU0MzMsIm5iZiI6MCwiaWF0IjoxNTYxMDAwMDMzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjA0MzQ0NWJlLWE3ZmEtNDZmNy05OTkwLWNhMWQ5ZmRlYmJjNiIsImF1dGhfdGltZSI6MTU2MDk5ODIzMSwic2Vzc2lvbl9zdGF0ZSI6ImNiMjJjYTQ2LTA2NjctNDVkYi04NzNiLTNjZjhhOGRkMmIzMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.knnSyiRFfH3J3URnTygvs4e-2rxh8U7HWmagtGZ3FUg-j_w37e50Z8gFOBoaoOMChl3IEoYtBSJXk9nj_AlCYZqZ3QZET2fsuoB0ERwoUXtyK9uZOPR1PaRAfLURRTBbui5MtbUZ8nni3esbz01DeJaWtjo22Dx1VhdAUQwPRulu2td2InkGO-_HvLhgLv173a5mEnQsH2_nSl9m8axFLeM9kz_Tr6Xb9MecTny_y8XWy2hxF4ihnq5AYRhEnTATUvJxAoYc6aJYCs-cxxcKHnqp7n17r3UbVa1E1O6wZ3P5xBWLnBp-GVM8bkekJ_WCcYGFMZ2Ev2gLa6Unb_oXFw";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(null,token);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void eipErrorCreateEipV6WithSbw(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkODM0OTkwNS1iYzcwLTQxZjQtOWJlMC0wNWQ3Mjk5OTVkODYiLCJleHAiOjE1NjEwMDU0MzMsIm5iZiI6MCwiaWF0IjoxNTYxMDAwMDMzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjA0MzQ0NWJlLWE3ZmEtNDZmNy05OTkwLWNhMWQ5ZmRlYmJjNiIsImF1dGhfdGltZSI6MTU2MDk5ODIzMSwic2Vzc2lvbl9zdGF0ZSI6ImNiMjJjYTQ2LTA2NjctNDVkYi04NzNiLTNjZjhhOGRkMmIzMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.knnSyiRFfH3J3URnTygvs4e-2rxh8U7HWmagtGZ3FUg-j_w37e50Z8gFOBoaoOMChl3IEoYtBSJXk9nj_AlCYZqZ3QZET2fsuoB0ERwoUXtyK9uZOPR1PaRAfLURRTBbui5MtbUZ8nni3esbz01DeJaWtjo22Dx1VhdAUQwPRulu2td2InkGO-_HvLhgLv173a5mEnQsH2_nSl9m8axFLeM9kz_Tr6Xb9MecTny_y8XWy2hxF4ihnq5AYRhEnTATUvJxAoYc6aJYCs-cxxcKHnqp7n17r3UbVa1E1O6wZ3P5xBWLnBp-GVM8bkekJ_WCcYGFMZ2Ev2gLa6Unb_oXFw";
        String eipIdWithSbw = "708f89ba-54a0-4a03-939a-d2cfe0208789";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eipIdWithSbw,token);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void eipErrorCreateEipV6WithV6(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkODM0OTkwNS1iYzcwLTQxZjQtOWJlMC0wNWQ3Mjk5OTVkODYiLCJleHAiOjE1NjEwMDU0MzMsIm5iZiI6MCwiaWF0IjoxNTYxMDAwMDMzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjA0MzQ0NWJlLWE3ZmEtNDZmNy05OTkwLWNhMWQ5ZmRlYmJjNiIsImF1dGhfdGltZSI6MTU2MDk5ODIzMSwic2Vzc2lvbl9zdGF0ZSI6ImNiMjJjYTQ2LTA2NjctNDVkYi04NzNiLTNjZjhhOGRkMmIzMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.knnSyiRFfH3J3URnTygvs4e-2rxh8U7HWmagtGZ3FUg-j_w37e50Z8gFOBoaoOMChl3IEoYtBSJXk9nj_AlCYZqZ3QZET2fsuoB0ERwoUXtyK9uZOPR1PaRAfLURRTBbui5MtbUZ8nni3esbz01DeJaWtjo22Dx1VhdAUQwPRulu2td2InkGO-_HvLhgLv173a5mEnQsH2_nSl9m8axFLeM9kz_Tr6Xb9MecTny_y8XWy2hxF4ihnq5AYRhEnTATUvJxAoYc6aJYCs-cxxcKHnqp7n17r3UbVa1E1O6wZ3P5xBWLnBp-GVM8bkekJ_WCcYGFMZ2Ev2gLa6Unb_oXFw";
        String eipIdWithV6 = "18c95fef-11ff-4e6e-8a1e-7a00949f9410";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomCreateEipV6(eipIdWithV6,token);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN10S10() {
        int pageNo = 10;
        int pageSize = 10;
        String status = HsConstants.ACTIVE;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN1S1(){
        int pageNo = 1;
        int pageSize = 1;
        String status = null;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN10S0(){
        int pageNo = 10;
        int pageSize = 0;
        String status = HsConstants.ACTIVE;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void listEipV6sN0S10(){
        int pageNo = 0;
        int pageSize = 10;
        String status = HsConstants.ACTIVE;
        ResponseEntity responseEntity = eipV6ServiceImpl.listEipV6s(pageNo, pageSize, status);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipV6() {
        String eipV6IdInBindWitEcs = "0ef67955-e43d-4c86-8a47-285be529968e";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(eipV6IdInBindWitEcs);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipV6Null(){
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void atomDeleteEipV6IdIsNull(){
        String eipV6Id = "";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(eipV6Id);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }
    @Test
    public void atomDeleteEipV6OfOtherUser(){
        String eipV6IdOfOtherUser = "ebeedf5c-19b3-4553-82c8-e6fc7ac98440";
        ResponseEntity responseEntity = eipV6ServiceImpl.atomDeleteEipV6(eipV6IdOfOtherUser);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void getEipV6Detail() {
        String eipV6Id = "ef600bea-6cbb-4d26-9ce5-c738c6e22bd5";
        ResponseEntity responseEntity = eipV6ServiceImpl.getEipV6Detail(eipV6Id);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getEipV6DetailNull(){
        String eipV6Id = "";
        ResponseEntity responseEntity = eipV6ServiceImpl.getEipV6Detail(eipV6Id);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }

    @Test
    public void getEipV6DetailDelete(){
        String eipV6IdDelete = "065a2b32-7043-4d7b-9a8b-6d91304a702b";
        ResponseEntity responseEntity = eipV6ServiceImpl.getEipV6Detail(eipV6IdDelete);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPort() {
        String eipV6Id = "9bdf484b-7929-45df-86b2-f853e54952db";
        String eip = "10.110.38.27";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id,eip);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPortNull(){
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort("123",null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPortV6Delete(){
        String eipV6IdDelete = "065a2b32-7043-4d7b-9a8b-6d91304a702b";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6IdDelete,null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void eipV6bindPortV4Null(){
        String eipV6Id = "ef600bea-6cbb-4d26-9ce5-c738c6e22bd5";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id,null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }
    @Test
    public void eipV6bindPortV6InBindToV4UnBind(){
        String eipV6Id = "0ef67955-e43d-4c86-8a47-285be529968e";
        String eip = "10.110.38.27";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id,eip);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }
    @Test
    public void eipV6bindPortV6UnbindToV4InBind(){
        String eipV6Id = "9bdf484b-7929-45df-86b2-f853e54952db";
        String eip = "10.110.38.30";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id,eip);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }
    @Test
    public void eipV6bindPortInBindToInBind(){
        String eipV6Id = "0ef67955-e43d-4c86-8a47-285be529968e";
        String eip = "10.110.38.30";
        ResponseEntity responseEntity = eipV6ServiceImpl.eipV6bindPort(eipV6Id,eip);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void findEipV6ByEipV6Id() {
    }
}