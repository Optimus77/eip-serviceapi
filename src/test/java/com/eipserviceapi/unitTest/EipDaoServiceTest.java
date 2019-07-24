package com.eipserviceapi.unitTest;

import com.eipserviceapi.TestEipServiceApplication;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.impl.EipServiceImpl;
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

//import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;
@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EipDaoService.class)
@Rollback
@SpringBootTest(classes = TestEipServiceApplication.class)
@Transactional(transactionManager = "transactionManager")
public class EipDaoServiceTest {

    @Autowired
    EipDaoService eipDaoService;

    @Before
    public void setUp() throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequest(){

            @Override
            public String getHeader(String name) {
                //todo 测试之前摘取token
                return "bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIxNDI1MWNiNS0yNGY0LTQwZGQtYjFkMi1lOWZjMTc2ZmUwOWUiLCJleHAiOjE1NjM5NjMxODAsIm5iZiI6MCwiaWF0IjoxNTYzOTU3NzgwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjQ3NTU2ZTdhLWFlZjMtNGIyYy04NWMxLTA4Y2E4MTUxMjk4NSIsImF1dGhfdGltZSI6MTU2Mzk1NDQ4Miwic2Vzc2lvbl9zdGF0ZSI6IjRlZGQwNGM0LWI0YTMtNGY2Yi05Yzg3LWI1MmZhMzFlYjRhNCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.VYpLCJNQr24m6kN6KgBsT3eBPPXAgOqfnJxgoanguWlP_QfIfKNq4SiRi5A07HLdDqBQTFjZ8kOKOnoRWuMdT4AIwE2TasgeuA-SrHuu3KJ4BPVKBm9MBUbsZrReoKrQRUQMlfWyiOZPSEjziB-v-h2OXWEYD_wDVSiCKvWZuqNk8_cqMpBI0J1zYRB7faCFOQeALIFH-zB-i7_phT4K1jdZaFALid-zmKDWxX1Q8_EbGJqYU3OrcOGY78cEtM3wjVVRxqpG1lT-ssgk0mgf6VDAwab7ovVfolJDbHjMvhak2UoqRCmzGovO_MOcavQif1Ue2_ZIGxOuBMdl5fG1JQ";
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
    public void adminDeleteEip() {
        String eipId = "5fc8acda-6608-4d49-9363-c962df4a53f4";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipNull(){
        String eipId = "123";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipAlreadyDelete(){
        String eipId = "453a3f69-da30-470a-af3b-fe57ebea9326";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipInBind(){
        String eipId = "18c95fef-11ff-4e6e-8a1e-7a00949f9410";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void adminDeleteEipWithIpV6(){
        String eipId = "962d1124-30b2-4c47-8976-f7b5baea6c1d";
        ActionResponse actionResponse = eipDaoService.adminDeleteEip(eipId);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void softDownEip() {
        String eipId = "09e27ea7-27c8-4def-8ecf-54c00e185bfd";
        ActionResponse actionResponse = eipDaoService.softDownEip(eipId);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void softDownEipNotFound(){
        String eipId = "123";
        ActionResponse actionResponse = eipDaoService.softDownEip(eipId);
        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void associateInstanceWithEip() {
    }

    @Test
    public void disassociateInstanceWithEip() {
    }

    @Test
    public void updateEipEntity() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "d7de403e-c694-48f8-8057-c26f02a73a50";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eipId,param,token);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void updateEipEntityNotFound(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "123";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eipId,param,token);
        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void updateEipEntityWithIpV6() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "91094d7d-0650-4e9f-a08a-06fb5a2c309d";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eipId,param,token);
        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void updateEipEntityOfOtherUser() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "60a42b44-6986-4dc7-b39c-ead8eaed0c1b";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(123);
        param.setBillType(HsConstants.HOURLYSETTLEMENT);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eipId,param,token);
        assertEquals(403,actionResponse.getCode());
    }

    @Test
    public void updateEipEntityMonthly() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "356e2e2d-c5f3-4059-98bd-2cd4ab1883b8";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(0);
        param.setBillType(HsConstants.MONTHLY);
        ActionResponse actionResponse = eipDaoService.updateEipEntity(eipId,param,token);
        assertEquals(400,actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity() {
        String eipId = "09e27ea7-27c8-4def-8ecf-54c00e185bfd";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId,addTime);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void reNewEipEntityInBind() {
        String eipId = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId,addTime);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void reNewEipEntityNotFound() {
        String eipId = "123";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId,addTime);
        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity1() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "2b17b38c-ad65-431e-9ed4-b82fc90b5e78";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId,addTime,token);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity1InBind() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId,addTime,token);
        assertEquals(200,actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity1NotFound() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "123";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId,addTime,token);
        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void reNewEipEntity1OfOtherUser() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNGZhOGRkZS03MjFjLTQ4MTUtOTM1Ni05ZDUxYmQ0YTlkMzQiLCJleHAiOjE1NjI2NjAxNjAsIm5iZiI6MCwiaWF0IjoxNTYyNjU0NzYwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjE0NTEwMzFkLTNlNDItNDRhZS05NTE3LTllNTAyYzFiMjVkOSIsImF1dGhfdGltZSI6MTU2MjY0NzE5MCwic2Vzc2lvbl9zdGF0ZSI6ImYzNDIwNzgxLWRjYzctNGEyMy04ZDIyLTIxMDQ0ZTZjNjZjYiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.ib2jA9Y7ny_rsfw5bSjYY1eEx3S01jzM0ySvd_wzCCTjA8EfqTYqgdGwxzRTvown_ZKh6m8P9RCB4TR58VcylVCf7QoKLN4aJlM70KYmylPO4mZZX39QQlTel1Z7HLqIxU4_FORiQva8IeuDxawwweukKXmLuf7F3hmTd3vVKKYA8t1zMwU36aWp4NO394J2c2FLctcTPXmFmXKmYjUTV4aUHEBC_UyKIHdvhKLjbHRpLZ8i7lm1F4S-sKLcS4HRRj3E5y6vUxLxrhWmQOxjfR1ZJEDfJAhXZQj0xAePSaFQx6O3m6W0yXEUqe_ijOxxJynp-yD0h1aHAXkgq_ajXA";
        String eipId = "60a42b44-6986-4dc7-b39c-ead8eaed0c1b";
        String addTime = "1";
        ActionResponse actionResponse = eipDaoService.reNewEipEntity(eipId,addTime,token);
        assertEquals(403,actionResponse.getCode());
    }

    @Test
    public void findByUserId() {
    }

    @Test
    public void findByEipAddress() {
    }

    @Test
    public void findByInstanceId() {
    }

    @Test
    public void getEipById() {
    }

    @Test
    public void getInstanceNum() {
        String userId = "9d0b67cd-20cb-40b4-8dc4-b0415ca25d72";
        long instanceNum = eipDaoService.getInstanceNum(userId);
        assertThat(instanceNum,instanceOf(long.class));
    }

    @Test
    public void getFreeEipCount() {
        int freeEipCount = eipDaoService.getFreeEipCount();
        assertThat(freeEipCount,instanceOf(int.class));

    }

    @Test
    public void getUsingEipCount() {
        int usingEipCount = eipDaoService.getUsingEipCount();
        assertThat(usingEipCount,instanceOf(int.class));
    }

    @Test
    public void getTotalBandWidth() {
        int totalBandWidth = eipDaoService.getTotalBandWidth();
        assertThat(totalBandWidth,instanceOf(int.class));

    }

    @Test
    public void getUsingEipCountByStatus() {
    }

    @Test
    public void getOneEipFromPool() {
    }

    @Test
    public void getDuplicateEip() {
    }

    @Test
    public void getDuplicateEipFromPool() {
    }

    @Test
    public void get() {
    }

    @Test
    public void statisEipCountBySbw() {
    }
}