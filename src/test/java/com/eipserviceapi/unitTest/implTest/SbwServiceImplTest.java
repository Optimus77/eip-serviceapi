package com.eipserviceapi.unitTest.implTest;

import com.eipserviceapi.TestEipServiceApplication;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.service.impl.SbwServiceImpl;
import com.inspur.eip.util.constant.ErrorStatus;
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SbwServiceImpl.class)
@Rollback
@SpringBootTest(classes = TestEipServiceApplication.class)
@Transactional
public class SbwServiceImplTest {

    @Autowired
    private SbwServiceImpl sbwService;

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
    public void atomCreateSbw() {
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration("1");
        param.setBandwidth(100);
        param.setBillType("hourlySettlement");
        param.setRegion("cn-north-3");
        param.setSbwName("atomUnitTest");
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJiOWU5YmI0YS1kYWM0LTRhYjUtYmJmOC0zOWQyM2ZhY2NmNzEiLCJleHAiOjE1NjE2MDQ4MjUsIm5iZiI6MCwiaWF0IjoxNTYxNTk5NDI1LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjViYjk2NTk3LWUxN2MtNDgwNi04ZjNhLTViMzAzYzQxY2I2NiIsImF1dGhfdGltZSI6MTU2MTU5OTQyMSwic2Vzc2lvbl9zdGF0ZSI6ImU4ZWZkNWM2LTdiMmYtNGI3Ni04NDFhLTA3ODMxM2EzMzhmZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.BcUaYp_YazOOF6iVoIniS61Erosyu6CC1-eE3CNEQf7iFhZmYnOkwV4ukblK0-fCfewX87dCcXIKa7ScjVV2tzSFmJvwc-QyujjIbdee0Yxhlg-YEIRKZcCvG-JLsFv7FXNeDeFp4wdj3WdGfFCZaXJI9V3SBixN8g3BuNI3L92_XMg1nXTZYK-nCw3kWElNbtCzkW8n4KQ7Cl0nCTBhljvERaNnumRAMLl4raWfHSSCjQLxF8MtwkAZAcQWFUHh75nMejUo1rKK2nyJA0Ed_dLFDnu2dkMs82_owIvs-TJTEseMLD16879E_x9gAJldYkTGj1AskQrOp8vrSyZ35Q";
        ResponseEntity responseEntity = sbwService.atomCreateSbw(param, token);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());

    }

    @Test
    public void listByIdAndIsDeleteShareBandWidth() {
        Integer pageIndex = 20;
        Integer pageSize = 100;
        String searchValue = "cf5dd08b-c15a-45d5-b8b6-c01f81495a86";
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, searchValue);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listByIsDeleteAndSbwName(){
        Integer pageIndex = 20;
        Integer pageSize = 100;
        String searchValue = "unitTest-勿删";
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, searchValue);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listByIsDelete(){
        Integer pageIndex = 20;
        Integer pageSize = 100;
        String searchValue = null;
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, searchValue);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listByProjectId(){
        Integer pageIndex = 0;
        Integer pageSize = 100;
        String searchValue = null;
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, searchValue);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void deleteSbwInfo() {
        String sbwId = "cf5dd08b-c15a-45d5-b8b6-c01f81495a86";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJiOWU5YmI0YS1kYWM0LTRhYjUtYmJmOC0zOWQyM2ZhY2NmNzEiLCJleHAiOjE1NjE2MDQ4MjUsIm5iZiI6MCwiaWF0IjoxNTYxNTk5NDI1LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjViYjk2NTk3LWUxN2MtNDgwNi04ZjNhLTViMzAzYzQxY2I2NiIsImF1dGhfdGltZSI6MTU2MTU5OTQyMSwic2Vzc2lvbl9zdGF0ZSI6ImU4ZWZkNWM2LTdiMmYtNGI3Ni04NDFhLTA3ODMxM2EzMzhmZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.BcUaYp_YazOOF6iVoIniS61Erosyu6CC1-eE3CNEQf7iFhZmYnOkwV4ukblK0-fCfewX87dCcXIKa7ScjVV2tzSFmJvwc-QyujjIbdee0Yxhlg-YEIRKZcCvG-JLsFv7FXNeDeFp4wdj3WdGfFCZaXJI9V3SBixN8g3BuNI3L92_XMg1nXTZYK-nCw3kWElNbtCzkW8n4KQ7Cl0nCTBhljvERaNnumRAMLl4raWfHSSCjQLxF8MtwkAZAcQWFUHh75nMejUo1rKK2nyJA0Ed_dLFDnu2dkMs82_owIvs-TJTEseMLD16879E_x9gAJldYkTGj1AskQrOp8vrSyZ35Q";
        ActionResponse response = sbwService.deleteSbwInfo(sbwId, token);

        assertEquals(200,response.getCode());
    }

    @Test
    public void errorDeletesbwIdIsBlank(){
        String sbwId = null;
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJiOWU5YmI0YS1kYWM0LTRhYjUtYmJmOC0zOWQyM2ZhY2NmNzEiLCJleHAiOjE1NjE2MDQ4MjUsIm5iZiI6MCwiaWF0IjoxNTYxNTk5NDI1LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjViYjk2NTk3LWUxN2MtNDgwNi04ZjNhLTViMzAzYzQxY2I2NiIsImF1dGhfdGltZSI6MTU2MTU5OTQyMSwic2Vzc2lvbl9zdGF0ZSI6ImU4ZWZkNWM2LTdiMmYtNGI3Ni04NDFhLTA3ODMxM2EzMzhmZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.BcUaYp_YazOOF6iVoIniS61Erosyu6CC1-eE3CNEQf7iFhZmYnOkwV4ukblK0-fCfewX87dCcXIKa7ScjVV2tzSFmJvwc-QyujjIbdee0Yxhlg-YEIRKZcCvG-JLsFv7FXNeDeFp4wdj3WdGfFCZaXJI9V3SBixN8g3BuNI3L92_XMg1nXTZYK-nCw3kWElNbtCzkW8n4KQ7Cl0nCTBhljvERaNnumRAMLl4raWfHSSCjQLxF8MtwkAZAcQWFUHh75nMejUo1rKK2nyJA0Ed_dLFDnu2dkMs82_owIvs-TJTEseMLD16879E_x9gAJldYkTGj1AskQrOp8vrSyZ35Q";
        ActionResponse response = sbwService.deleteSbwInfo(sbwId, token);

        assertEquals(400,response.getCode());
    }

    @Test
    public void bssSoftDeleteSbw() {
        String sbwId = "cf5dd08b-c15a-45d5-b8b6-c01f81495a86";
        ActionResponse actionResponse = sbwService.bssSoftDeleteSbw(sbwId);

        assertEquals(200,actionResponse.getCode());
    }
    @Test
    public void errorBssSoftDeleteSbw(){
        String sbwId = null;
        ActionResponse actionResponse = sbwService.bssSoftDeleteSbw(sbwId);

        assertEquals(400,actionResponse.getCode());
    }

    @Test
    public void getSbwDetail() {
        String sbwId = "cf5dd08b-c15a-45d5-b8b6-c01f81495a86";
        ResponseEntity sbwDetail = sbwService.getSbwDetail(sbwId);

        assertEquals(HttpStatus.OK,sbwDetail.getStatusCode());
    }

    @Test
    public void updateSbwConfig() {
        String sbwId = "cf5dd08b-c15a-45d5-b8b6-c01f81495a86";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkYTU5MDVlNy04NzJiLTQ0OWItYjFkYy03YzRlZmZmZWMxYTUiLCJleHAiOjE1NjE5NTExODEsIm5iZiI6MCwiaWF0IjoxNTYxOTQ1NzgxLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjlkOTY5OTdjLTU2OWEtNDZmNC1iY2FjLTYzMDliODA3NGM5ZCIsImF1dGhfdGltZSI6MTU2MTk0NTc3OSwic2Vzc2lvbl9zdGF0ZSI6ImFlMWUzYzMyLTRkNGItNGM5OS1iNzU0LTMyNzkyZGZkNzEyMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.jJmhFY9G52OlURM0_8hOU4a7A-aLryalggjtsf-nLi5Spqg8gKA82zXTk-O7UtnJ9ZIHXQjCHQw7lrwQjv9dody_W6aDXPdD-6cIjb3X3sCer-CZGV2gWZs5KtlA_8VypNeyjJST1mLwSIp4vtALMSPEUZt229E74GL2uIkp6jmVcrwtN4ez81yIusYsrCZsSs8D6CIe_P0_O14E_HDAvy1h_AwNlNwHSd7k3bGqrkz-0NtMJtq6IKTmOtOqFvsHdp20UaRMmL3hAgu5MnxiM2lXxmkXSNgetMbexVlWGrLpumEpZoYYJ4nB6wQJKGOva3utGRwUpoXnMsGiR6Yn9g";
        SbwUpdateParam param = new SbwUpdateParam();
        param.setSbwName("unitTestCheckout");
        param.setRegion("cn-north-3");
        param.setBillType("hourlySettlement");
        param.setBandwidth(202);
        param.setDuration("1");
        ActionResponse response = sbwService.updateSbwConfig(sbwId, param, token);

        assertEquals(200,response.getCode());
    }

    @Test
    public void errorUpdateSbwConfig(){
        String sbwId = null;
        SbwUpdateParam param = new SbwUpdateParam();
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJiOWU5YmI0YS1kYWM0LTRhYjUtYmJmOC0zOWQyM2ZhY2NmNzEiLCJleHAiOjE1NjE2MDQ4MjUsIm5iZiI6MCwiaWF0IjoxNTYxNTk5NDI1LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjViYjk2NTk3LWUxN2MtNDgwNi04ZjNhLTViMzAzYzQxY2I2NiIsImF1dGhfdGltZSI6MTU2MTU5OTQyMSwic2Vzc2lvbl9zdGF0ZSI6ImU4ZWZkNWM2LTdiMmYtNGI3Ni04NDFhLTA3ODMxM2EzMzhmZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.BcUaYp_YazOOF6iVoIniS61Erosyu6CC1-eE3CNEQf7iFhZmYnOkwV4ukblK0-fCfewX87dCcXIKa7ScjVV2tzSFmJvwc-QyujjIbdee0Yxhlg-YEIRKZcCvG-JLsFv7FXNeDeFp4wdj3WdGfFCZaXJI9V3SBixN8g3BuNI3L92_XMg1nXTZYK-nCw3kWElNbtCzkW8n4KQ7Cl0nCTBhljvERaNnumRAMLl4raWfHSSCjQLxF8MtwkAZAcQWFUHh75nMejUo1rKK2nyJA0Ed_dLFDnu2dkMs82_owIvs-TJTEseMLD16879E_x9gAJldYkTGj1AskQrOp8vrSyZ35Q";
        ActionResponse response = sbwService.updateSbwConfig(sbwId, param, token);

        assertEquals(500,response.getCode());
    }

    @Test
    public void countSbwNumsByProjectId() {
        ResponseEntity sbwCount = sbwService.countSbwNumsByProjectId();

        assertEquals(HttpStatus.OK,sbwCount.getStatusCode());
    }

    @Test
    public void countSbwNumsByStatus() {
        String status = "ACTIVE";
        ResponseEntity responseEntity = sbwService.countSbwNumsByStatus(status);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void restartSbwService() {
        String sbwId = "b9141748-a6ec-48b2-a242-bebf8fa165b2";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJiOWU5YmI0YS1kYWM0LTRhYjUtYmJmOC0zOWQyM2ZhY2NmNzEiLCJleHAiOjE1NjE2MDQ4MjUsIm5iZiI6MCwiaWF0IjoxNTYxNTk5NDI1LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjViYjk2NTk3LWUxN2MtNDgwNi04ZjNhLTViMzAzYzQxY2I2NiIsImF1dGhfdGltZSI6MTU2MTU5OTQyMSwic2Vzc2lvbl9zdGF0ZSI6ImU4ZWZkNWM2LTdiMmYtNGI3Ni04NDFhLTA3ODMxM2EzMzhmZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.BcUaYp_YazOOF6iVoIniS61Erosyu6CC1-eE3CNEQf7iFhZmYnOkwV4ukblK0-fCfewX87dCcXIKa7ScjVV2tzSFmJvwc-QyujjIbdee0Yxhlg-YEIRKZcCvG-JLsFv7FXNeDeFp4wdj3WdGfFCZaXJI9V3SBixN8g3BuNI3L92_XMg1nXTZYK-nCw3kWElNbtCzkW8n4KQ7Cl0nCTBhljvERaNnumRAMLl4raWfHSSCjQLxF8MtwkAZAcQWFUHh75nMejUo1rKK2nyJA0Ed_dLFDnu2dkMs82_owIvs-TJTEseMLD16879E_x9gAJldYkTGj1AskQrOp8vrSyZ35Q";
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration("1");
        param.setRegion("cn-north-3");
        ActionResponse response = sbwService.restartSbwService(sbwId, param, token);

        assertEquals(200,response.getCode());
    }

    @Test
    public void errorRestartSbwService(){
        String sbwId = "b9141748-a6ec-48b2-a242-bebf8fa165b2";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJiOWU5YmI0YS1kYWM0LTRhYjUtYmJmOC0zOWQyM2ZhY2NmNzEiLCJleHAiOjE1NjE2MDQ4MjUsIm5iZiI6MCwiaWF0IjoxNTYxNTk5NDI1LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjViYjk2NTk3LWUxN2MtNDgwNi04ZjNhLTViMzAzYzQxY2I2NiIsImF1dGhfdGltZSI6MTU2MTU5OTQyMSwic2Vzc2lvbl9zdGF0ZSI6ImU4ZWZkNWM2LTdiMmYtNGI3Ni04NDFhLTA3ODMxM2EzMzhmZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.BcUaYp_YazOOF6iVoIniS61Erosyu6CC1-eE3CNEQf7iFhZmYnOkwV4ukblK0-fCfewX87dCcXIKa7ScjVV2tzSFmJvwc-QyujjIbdee0Yxhlg-YEIRKZcCvG-JLsFv7FXNeDeFp4wdj3WdGfFCZaXJI9V3SBixN8g3BuNI3L92_XMg1nXTZYK-nCw3kWElNbtCzkW8n4KQ7Cl0nCTBhljvERaNnumRAMLl4raWfHSSCjQLxF8MtwkAZAcQWFUHh75nMejUo1rKK2nyJA0Ed_dLFDnu2dkMs82_owIvs-TJTEseMLD16879E_x9gAJldYkTGj1AskQrOp8vrSyZ35Q";
        SbwUpdateParam param = new SbwUpdateParam();
        ActionResponse response = sbwService.restartSbwService(sbwId, param, token);

        assertEquals(400,response.getCode());
    }

    @Test
    public void stopSbwService() {
        String sbwId = "b9141748-a6ec-48b2-a242-bebf8fa165b2";
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration("0");
        ActionResponse response = sbwService.stopSbwService(sbwId, param);

        assertEquals(200,response.getCode());
    }

    @Test
    public void errorStopSbwService() {
        String sbwId = "b9141748-a6ec-48b2-a242-bebf8fa165b2";
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration(null);
        ActionResponse response = sbwService.stopSbwService(sbwId, param);

        assertEquals(400,response.getCode());
    }

    @Test
    public void sbwListEip() {
        String sbwId = "0e40e97a-4b30-492e-8472-df3e2c044c2a";
        Integer currentPage = 20;
        Integer limit = 50;
        ResponseEntity responseEntity = sbwService.sbwListEip(sbwId, currentPage, limit);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());

    }

    @Test
    public void sbwListEipWithNOCurrentPage() {
        String sbwId = "0e40e97a-4b30-492e-8472-df3e2c044c2a";
        Integer currentPage = 0;
        Integer limit = 50;
        ResponseEntity responseEntity = sbwService.sbwListEip(sbwId, currentPage, limit);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());

    }

    @Test
    public void renameSbw() {
        String sbwId = "8aa775f5-e085-417a-bc65-29c55fb2cfb4";
        SbwUpdateParam param = new SbwUpdateParam();
        param.setSbwName("rename");
        ResponseEntity responseEntity = sbwService.renameSbw(sbwId, param);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void errorrRenameSbw() {
        String sbwId = null;
        SbwUpdateParam param = new SbwUpdateParam();
        param.setSbwName(null);
        ResponseEntity responseEntity = sbwService.renameSbw(sbwId, param);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }


    @Test
    public void getOtherEips() {
        String sbwId = "0e40e97a-4b30-492e-8472-df3e2c044c2a";
        ResponseEntity otherEips = sbwService.getOtherEips(sbwId);

        assertEquals(HttpStatus.OK,otherEips.getStatusCode());
    }
}