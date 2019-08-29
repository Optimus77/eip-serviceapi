package com.eipserviceapi.unitTest.implTest;

import com.eipserviceapi.TestEipServiceApplication;
import com.eipserviceapi.unitTest.TokenUtil;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.WebControllerService;
import com.inspur.eip.service.impl.EipServiceImpl;
//import groovy.util.logging.Slf4j;
import com.inspur.eip.util.constant.HsConstants;
import groovy.util.logging.Slf4j;
import jdk.nashorn.internal.parser.Token;
import org.apache.http.HttpException;
import org.apache.kafka.common.protocol.types.Field;
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
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Principal;
import java.util.*;

import static org.junit.Assert.*;
@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EipServiceImpl.class)
@Rollback
@SpringBootTest(classes = TestEipServiceApplication.class)
@Transactional
public class EipServiceImplTest {

    @Autowired
    EipServiceImpl eipServiceImpl;

    @Autowired
    EipDaoService eipDaoService;

    @Autowired
    private EipPoolRepository eipPoolRepository;


    @Before
    public void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequest(){


            @Override
            public String getHeader(String name) {
                try {
                    return "bearer " + TokenUtil.getToken("lishenghao","1qaz2wsx3edc");
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
    public void atomCreateEip() {
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(100);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        //token of lishenghao
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNWJlYjNhMS0yMGQ3LTRiN2EtYjNiYi03ZDgxNjUzMzAxYWUiLCJleHAiOjE1NjE2MDQ1MTcsIm5iZiI6MCwiaWF0IjoxNTYxNTk5MTE3LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImNjZjc3YTJjLWU4MDMtNDdjMi04OGNiLTdjZDYyYzNhOWIwMCIsImF1dGhfdGltZSI6MTU2MTU5OTExMCwic2Vzc2lvbl9zdGF0ZSI6IjhkYWI4ZTAxLWJhNDYtNGY3My1iY2MyLWM5ZDg2M2I3ZjZkYyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.mVHzxGdKCmL9ZF1Xa9JCJkTTXSfg7UDTPPVy9oBGuwkzw492Uo3sMmcVnx26l5fO0k__vIxkR5TfuCtgvAJPvp1Mw6rmp2yN45ZUMUQ513uckoPeXUMNzE7f9-GnkZ2qZ2APCYO_JNevWiPHqgoQBEllONDtof4YbTbEkPpeGSL2_g_66CK2sdrG2C8tYSpj2Yayab0q99IM1BwclkgJXxrUVtZTlt3sIdtoJwgi45HujNTfpMmB71JVCHTjsuPqiifYNmAk-SEkdsn22zBTyTApArreRq6QH_mvHIkgB6FjQvjlvpzZH9BzchZh876HY8PrUBlb5UmTeLALfzlh4Q";
        //token of xinjing
        //String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJlNWZiMmEyZC03ZWViLTQ1YzEtOGUyMi0wZDdhYmQxN2MyODQiLCJleHAiOjE1NjEzNDI0NDAsIm5iZiI6MCwiaWF0IjoxNTYxMzM3MDQwLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImM2ODQ2ZmNkLTcyMmEtNGMyMi04MTQ4LWY1YWE5ODdhZWU5OSIsImF1dGhfdGltZSI6MTU2MTMzNzAzOSwic2Vzc2lvbl9zdGF0ZSI6ImIyZDFmZDFjLTgyN2QtNDhiOS04ZWI5LWIwOWVjMGI4MWFjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.KNuL_ucWbqN4S8QuoRQvQdKWaUy2wB38X8HmNESsGmVgbFmFoSf-wSftJPWK5QzGBu6WfBbj9_LqQDYv2h10bvsZFVupwvQTuQyo_16Cjhx7fQrFbUXxyFd7THME2QnbifhwwX7ka7ieNy8wh5h_Q_7fNZzup6uQ8dhEz3zfS6GR6H3gtsiofieUtMYccD_u3PdsnZ0AUecOxMyLG2fFLjYCjYdfICwU3AKne1IxLozIcrN9vSXd0qZl3ktnqqItPaaWpwvliAtwXN6ixpg8gJ-A2253zg2SQaGXDmMpOCTMycMhLMgOW6Tmok3zyHMlfjhqKDf4Pc14_JR_PhGWjA";
        String operater = "unitTest";
        if(eipPoolRepository.getEipByRandom("1") == null){
            ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
            assertEquals(HttpStatus.FAILED_DEPENDENCY,responseEntity.getStatusCode());
        } else
        {
            ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
            assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        }

    }
    @Test
    public void atomCreateEipWithNoEip(){
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(100);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId(null);
        eipConfig.setDuration("1");
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIwNWJlYjNhMS0yMGQ3LTRiN2EtYjNiYi03ZDgxNjUzMzAxYWUiLCJleHAiOjE1NjE2MDQ1MTcsIm5iZiI6MCwiaWF0IjoxNTYxNTk5MTE3LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImNjZjc3YTJjLWU4MDMtNDdjMi04OGNiLTdjZDYyYzNhOWIwMCIsImF1dGhfdGltZSI6MTU2MTU5OTExMCwic2Vzc2lvbl9zdGF0ZSI6IjhkYWI4ZTAxLWJhNDYtNGY3My1iY2MyLWM5ZDg2M2I3ZjZkYyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.mVHzxGdKCmL9ZF1Xa9JCJkTTXSfg7UDTPPVy9oBGuwkzw492Uo3sMmcVnx26l5fO0k__vIxkR5TfuCtgvAJPvp1Mw6rmp2yN45ZUMUQ513uckoPeXUMNzE7f9-GnkZ2qZ2APCYO_JNevWiPHqgoQBEllONDtof4YbTbEkPpeGSL2_g_66CK2sdrG2C8tYSpj2Yayab0q99IM1BwclkgJXxrUVtZTlt3sIdtoJwgi45HujNTfpMmB71JVCHTjsuPqiifYNmAk-SEkdsn22zBTyTApArreRq6QH_mvHIkgB6FjQvjlvpzZH9BzchZh876HY8PrUBlb5UmTeLALfzlh4Q";
        String operater = "unitTest";
        while(eipPoolRepository.getEipByRandom("1") != null)
        {
            eipDaoService.getOneEipFromPool("1");
        }
        ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);
        assertEquals(HttpStatus.FAILED_DEPENDENCY,responseEntity.getStatusCode());

    }

    @Test
    public void errorEipAddSbw(){
        EipAllocateParam eipConfig = new EipAllocateParam();
        eipConfig.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipConfig.setBandwidth(100);
        eipConfig.setBillType(HsConstants.HOURLYSETTLEMENT);
        eipConfig.setIpType("BGP");
        eipConfig.setIpv6("no");
        eipConfig.setRegion("cn-north-3");
        eipConfig.setSbwId("123abc");
        eipConfig.setDuration("1");
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkODM0OTkwNS1iYzcwLTQxZjQtOWJlMC0wNWQ3Mjk5OTVkODYiLCJleHAiOjE1NjEwMDU0MzMsIm5iZiI6MCwiaWF0IjoxNTYxMDAwMDMzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjA0MzQ0NWJlLWE3ZmEtNDZmNy05OTkwLWNhMWQ5ZmRlYmJjNiIsImF1dGhfdGltZSI6MTU2MDk5ODIzMSwic2Vzc2lvbl9zdGF0ZSI6ImNiMjJjYTQ2LTA2NjctNDVkYi04NzNiLTNjZjhhOGRkMmIzMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX0sInJkcy1teXNxbC1hcGkiOnsicm9sZXMiOlsidXNlciJdfX0sInNjb3BlIjoib3BlbmlkIiwic3ZjIjoiW1wiSERJTlNJR0hUXCJdIiwicGhvbmUiOiIxNzY4NjQwNjI5NSIsInByb2plY3QiOiJsaXNoZW5naGFvIiwiZ3JvdXBzIjpbIi9ncm91cC1saXNoZW5naGFvIl0sInByZWZlcnJlZF91c2VybmFtZSI6Imxpc2hlbmdoYW8iLCJlbWFpbCI6Imxpc2hlbmdoYW9AaW5zcHVyLmNvbSJ9.knnSyiRFfH3J3URnTygvs4e-2rxh8U7HWmagtGZ3FUg-j_w37e50Z8gFOBoaoOMChl3IEoYtBSJXk9nj_AlCYZqZ3QZET2fsuoB0ERwoUXtyK9uZOPR1PaRAfLURRTBbui5MtbUZ8nni3esbz01DeJaWtjo22Dx1VhdAUQwPRulu2td2InkGO-_HvLhgLv173a5mEnQsH2_nSl9m8axFLeM9kz_Tr6Xb9MecTny_y8XWy2hxF4ihnq5AYRhEnTATUvJxAoYc6aJYCs-cxxcKHnqp7n17r3UbVa1E1O6wZ3P5xBWLnBp-GVM8bkekJ_WCcYGFMZ2Ev2gLa6Unb_oXFw";
        String operater = "unitTest";

        ResponseEntity responseEntity = eipServiceImpl.atomCreateEip(eipConfig, token, operater);

        assertEquals(HttpStatus.FAILED_DEPENDENCY,responseEntity.getStatusCode());
    }


    @Test
    public void atomDeleteEip() {
        String eipId = "7216e894-6a79-4320-b1db-b4ec1fb8d2e0";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eipId);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void eipAlreadyDelete() {
        String eipId = "40c0a50d-c068-4d86-8c83-57cc5e690a74";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eipId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void eipMonthlyDelete() {
        String eipId = "e72da25a-f1e6-4603-8432-d0de7edf3d87";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eipId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void otherUserDelete() {
        String eipId = "9659c812-d006-49a2-98b8-168f95bfae12";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eipId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void bindEipDelete() {
        String eipId = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        ResponseEntity responseEntity = eipServiceImpl.atomDeleteEip(eipId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void deleteEipList() {
        String eipId1 = "09e27ea7-27c8-4def-8ecf-54c00e185bfd";
        String eipId2 = "7216e894-6a79-4320-b1db-b4ec1fb8d2e0";
        List<String> eipIds = Arrays.asList(eipId1,eipId2);
        ResponseEntity responseEntity = eipServiceImpl.deleteEipList(eipIds);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void deleteEipListNull(){
        String eipId1 = "";
        String eipId2 = "";
        List<String> eipIds = Arrays.asList(eipId1,eipId2);
        ResponseEntity responseEntity = eipServiceImpl.deleteEipList(eipIds);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void listEips() {
        ResponseEntity responseEntity = eipServiceImpl.listEips(0,1,"DOWN");
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listEipsCurrentPageIsOne(){
        ResponseEntity responseEntity = eipServiceImpl.listEips(1,1,"DOWN");
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listEipsLimitIsZero(){
        ResponseEntity responseEntity = eipServiceImpl.listEips(2,0,"DOWN");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetail() {
        String eipId = "7216e894-6a79-4320-b1db-b4ec1fb8d2e0";
        ResponseEntity responseEntity = eipServiceImpl.getEipDetail(eipId);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailIpV6(){
        String eipIdWithIpv6 = "91c71e69-3ee8-4d4e-8ec2-676b1e0a6bae";
        ResponseEntity responseEntity = eipServiceImpl.getEipDetail(eipIdWithIpv6);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailNotFound(){
        String eipIdNotFound = "";
        ResponseEntity responseEntity = eipServiceImpl.getEipDetail(eipIdNotFound);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }


    @Test
    public void getEipByIpAddress() {
        String eip = "10.110.38.30";
        ResponseEntity responseEntity = eipServiceImpl.getEipByIpAddress(eip);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getEipByIpAddressOfOtherUser(){
        String eipOtherUser = "10.110.38.6";
        ResponseEntity responseEntity = eipServiceImpl.getEipByIpAddress(eipOtherUser);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstance() {
        String eipId = "3af556cb-beb1-4cc4-9357-3aaa209a358b";
        String type = "1";
        String ecsId = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        String ecsPortId = "fe84a64d-8765-497c-8dfe-f8fcb0750d88";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eipId,type,ecsId,ecsPortId,null);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceServerIsNull(){
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(null,null,null,null,null);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceInBind(){
        String eipId = "3af556cb-beb1-4cc4-9357-3aaa209a358b";
        String type = HsConstants.ECS;
        String ecsId = "e61994c5-bba0-4831-b847-022d5d01b697";
        String ecsPortId = "e331525b-11ce-415c-98d0-bcbff0e755ee";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eipId,type,ecsId,ecsPortId,null);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceEipNull(){
        String eipId = "123";
        String type = "1";
        String ecsId = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        String ecsPortId = "fe84a64d-8765-497c-8dfe-f8fcb0750d88";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eipId,type,ecsId,ecsPortId,null);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceEipInBind(){
        String eipIdInBind = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        String type = "1";
        String ecsId = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        String ecsPortId = "fe84a64d-8765-497c-8dfe-f8fcb0750d88";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eipIdInBind,type,ecsId,ecsPortId,null);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
    }

    @Test
    public void eipBindWithInstanceEipOfOtherUser(){
        String eipIdOfOtherUser = "3f054122-ba9f-428a-8fda-6afe36725dc4";
        String type = "1";
        String ecsId = "aa022b17-7ce0-46da-9448-c8a439b10e27";
        String ecsPortId = "fe84a64d-8765-497c-8dfe-f8fcb0750d88";
        ResponseEntity responseEntity = eipServiceImpl.eipBindWithInstance(eipIdOfOtherUser,type,ecsId,ecsPortId,null);
        assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode());
    }

    @Test
    public void eipUnbindWithInstacnce() {
        String eipId = "bc57d6ae-73b3-4c89-a029-171fa02f0e98";
        ResponseEntity responseEntity = eipServiceImpl.eipUnbindWithInstacnce(eipId);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }


    @Test
    public void eipUnbindWithInstacnceEipUnBind(){
        String eipId = "3af556cb-beb1-4cc4-9357-3aaa209a358b";
        ResponseEntity responseEntity = eipServiceImpl.eipUnbindWithInstacnce(eipId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void eipUnbindWithInstacnceOfOtherUser(){
        String eipIdOfOtherUser = "f90cd84c-d1cf-45dc-aa39-ff896b490cf4";
        ResponseEntity responseEntity = eipServiceImpl.eipUnbindWithInstacnce(eipIdOfOtherUser);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,responseEntity.getStatusCode());
    }

    @Test
    public void getEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getEipCount();
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getEipStatistics() {
        ResponseEntity responseEntity = eipServiceImpl.getEipStatistics();
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getFreeEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getFreeEipCount();
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getUsingEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getUsingEipCount();
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getUsingEipCountByStatus() {
        ResponseEntity responseEntity = eipServiceImpl.getUsingEipCountByStatus("DOWN");
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getTotalEipCount() {
        ResponseEntity responseEntity = eipServiceImpl.getTotalEipCount();
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void listEipsByBandWidth() {
        ResponseEntity responseEntity = eipServiceImpl.listEipsByBandWidth("DOWN");
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());

    }

    @Test
    public void getEipDetailsByIpAddress() {
        String eip = "10.110.38.30";
        ResponseEntity responseEntity = eipServiceImpl.getEipDetailsByIpAddress(eip);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailsByIpAddressNull(){
        ResponseEntity responseEntity = eipServiceImpl.getEipDetailsByIpAddress(null);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }

    @Test
    public void getEipDetailsByIpAddressEipIsNull(){
        String eipNull = "111.222.333.444";
        ResponseEntity responseEntity = eipServiceImpl.getEipDetailsByIpAddress(eipNull);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
    }
}