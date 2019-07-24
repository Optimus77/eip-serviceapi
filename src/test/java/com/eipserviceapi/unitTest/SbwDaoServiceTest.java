package com.eipserviceapi.unitTest;

import com.eipserviceapi.TestEipServiceApplication;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.SbwDaoService;
import groovy.util.logging.Slf4j;
import org.apache.http.HttpStatus;
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
import scala.annotation.meta.param;

import javax.print.DocFlavor;
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
@ContextConfiguration(classes = SbwDaoService.class)
@Rollback
@SpringBootTest(classes = TestEipServiceApplication.class)
@Transactional
public class SbwDaoServiceTest {

    @Autowired
    private SbwDaoService sbwDaoService;

    @Before
    public void setUp() throws Exception {
        /*RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequest(){

            @Override
            public String getHeader(String name) {
                //todo 测试之前摘取token
                return "bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkYTU5MDVlNy04NzJiLTQ0OWItYjFkYy03YzRlZmZmZWMxYTUiLCJleHAiOjE1NjE5NTExODEsIm5iZiI6MCwiaWF0IjoxNTYxOTQ1NzgxLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjlkOTY5OTdjLTU2OWEtNDZmNC1iY2FjLTYzMDliODA3NGM5ZCIsImF1dGhfdGltZSI6MTU2MTk0NTc3OSwic2Vzc2lvbl9zdGF0ZSI6ImFlMWUzYzMyLTRkNGItNGM5OS1iNzU0LTMyNzkyZGZkNzEyMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.jJmhFY9G52OlURM0_8hOU4a7A-aLryalggjtsf-nLi5Spqg8gKA82zXTk-O7UtnJ9ZIHXQjCHQw7lrwQjv9dody_W6aDXPdD-6cIjb3X3sCer-CZGV2gWZs5KtlA_8VypNeyjJST1mLwSIp4vtALMSPEUZt229E74GL2uIkp6jmVcrwtN4ez81yIusYsrCZsSs8D6CIe_P0_O14E_HDAvy1h_AwNlNwHSd7k3bGqrkz-0NtMJtq6IKTmOtOqFvsHdp20UaRMmL3hAgu5MnxiM2lXxmkXSNgetMbexVlWGrLpumEpZoYYJ4nB6wQJKGOva3utGRwUpoXnMsGiR6Yn9g";
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
        */
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void errorIsDeleteDeleteSbw() {
        String sbwId = "b40e6e95-9df9-4c5e-8d98-1447b9abfa2a";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkYTU5MDVlNy04NzJiLTQ0OWItYjFkYy03YzRlZmZmZWMxYTUiLCJleHAiOjE1NjE5NTExODEsIm5iZiI6MCwiaWF0IjoxNTYxOTQ1NzgxLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjlkOTY5OTdjLTU2OWEtNDZmNC1iY2FjLTYzMDliODA3NGM5ZCIsImF1dGhfdGltZSI6MTU2MTk0NTc3OSwic2Vzc2lvbl9zdGF0ZSI6ImFlMWUzYzMyLTRkNGItNGM5OS1iNzU0LTMyNzkyZGZkNzEyMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.jJmhFY9G52OlURM0_8hOU4a7A-aLryalggjtsf-nLi5Spqg8gKA82zXTk-O7UtnJ9ZIHXQjCHQw7lrwQjv9dody_W6aDXPdD-6cIjb3X3sCer-CZGV2gWZs5KtlA_8VypNeyjJST1mLwSIp4vtALMSPEUZt229E74GL2uIkp6jmVcrwtN4ez81yIusYsrCZsSs8D6CIe_P0_O14E_HDAvy1h_AwNlNwHSd7k3bGqrkz-0NtMJtq6IKTmOtOqFvsHdp20UaRMmL3hAgu5MnxiM2lXxmkXSNgetMbexVlWGrLpumEpZoYYJ4nB6wQJKGOva3utGRwUpoXnMsGiR6Yn9g";
        ActionResponse response = sbwDaoService.deleteSbw(sbwId, token);

        assertEquals(HttpStatus.SC_NOT_FOUND,response.getCode());
    }
    @Test
    public void deleteSbwBandEip(){
        String sbwId = "0e40e97a-4b30-492e-8472-df3e2c044c2a";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkYTU5MDVlNy04NzJiLTQ0OWItYjFkYy03YzRlZmZmZWMxYTUiLCJleHAiOjE1NjE5NTExODEsIm5iZiI6MCwiaWF0IjoxNTYxOTQ1NzgxLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjlkOTY5OTdjLTU2OWEtNDZmNC1iY2FjLTYzMDliODA3NGM5ZCIsImF1dGhfdGltZSI6MTU2MTk0NTc3OSwic2Vzc2lvbl9zdGF0ZSI6ImFlMWUzYzMyLTRkNGItNGM5OS1iNzU0LTMyNzkyZGZkNzEyMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsImludml0ZWRfcmVnaW9uIjoiW1wiY24tc291dGgtMVwiXSIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.jJmhFY9G52OlURM0_8hOU4a7A-aLryalggjtsf-nLi5Spqg8gKA82zXTk-O7UtnJ9ZIHXQjCHQw7lrwQjv9dody_W6aDXPdD-6cIjb3X3sCer-CZGV2gWZs5KtlA_8VypNeyjJST1mLwSIp4vtALMSPEUZt229E74GL2uIkp6jmVcrwtN4ez81yIusYsrCZsSs8D6CIe_P0_O14E_HDAvy1h_AwNlNwHSd7k3bGqrkz-0NtMJtq6IKTmOtOqFvsHdp20UaRMmL3hAgu5MnxiM2lXxmkXSNgetMbexVlWGrLpumEpZoYYJ4nB6wQJKGOva3utGRwUpoXnMsGiR6Yn9g";
        ActionResponse response = sbwDaoService.deleteSbw(sbwId, token);

        assertEquals(HttpStatus.SC_FORBIDDEN,response.getCode());
    }

    @Test
    public void errorIsDeleteAdminDeleteSbw() {
        String sbwId = "b40e6e95-9df9-4c5e-8d98-1447b9abfa2a";
        ActionResponse response = sbwDaoService.adminDeleteSbw(sbwId);

        assertEquals(HttpStatus.SC_NOT_FOUND,response.getCode());
    }
    @Test
    public void deleteSbwAdminBandEip(){
        String sbwId = "0e40e97a-4b30-492e-8472-df3e2c044c2a";
        ActionResponse response = sbwDaoService.adminDeleteSbw(sbwId);

        assertEquals(HttpStatus.SC_FORBIDDEN,response.getCode());
    }

    @Test
    public void errorBillTypeRenewSbw(){
        String sbwId = "a449f5ba-f1e9-4aa4-bf0e-c03327469b97";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        ActionResponse actionResponse = sbwDaoService.renewSbwInfo(sbwId, token);

        assertEquals(HttpStatus.SC_BAD_REQUEST,actionResponse.getCode());
    }

    @Test
    public void errorBilltypeAddEipIntoSbw(){
        String eipId = "3f054122-ba9f-428a-8fda-6afe36725dc4";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("monthly");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("0e40e97a-4b30-492e-8472-df3e2c044c2a");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eipId, param, token);

        assertEquals(400,actionResponse.getCode());
    }

    @Test
    public void addEipIntoSbw(){
        String eipId = "2fa97681-da20-4991-81d6-e4b60e151a40";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("0e40e97a-4b30-492e-8472-df3e2c044c2a");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eipId, param, token);

        assertEquals(ActionResponse.actionSuccess().getCode(),actionResponse.getCode());
    }

    @Test
    public void errorSbwIdIsBlankAddEipIntoSbw(){
        String eipId = "6d1b7c00-6847-4612-ba3b-5e40745f4cfb";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(null);
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eipId, param, token);

        assertEquals(400,actionResponse.getCode());
    }

    @Test
    public void errorEipIdAddEipIntoSbw(){
        String eipId = "3f054122-ba9f-428a-8fda-6afe36725dc4";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("0e40e97a-4b30-492e-8472-df3e2c044c2a");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eipId, param, token);

        assertEquals(400,actionResponse.getCode());
    }

    @Test
    public void errorEipBandV6AddEipIntoSbw(){
        String eipId = "2955acc9-d9b1-4375-a892-e8642e73f2e4";
        String token = "123qwe";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("896a8637-83d3-4f93-8c4c-095628e7284f");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eipId, param, token);

        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void errorEipAlreadyAddEipIntoSbw(){
        String eipId = "38cd07a1-5650-4826-945b-257406f7e1a1";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("0e40e97a-4b30-492e-8472-df3e2c044c2a");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eipId, param, token);

        assertEquals(400,actionResponse.getCode());
    }

    @Test
    public void removeEipFromSbw(){
        String eipId = "6d1b7c00-6847-4612-ba3b-5e40745f4cfb";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(5);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("0c676dcd-f70c-4a60-a2ab-9b5f9cbf7c95");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw(eipId,param,token);

        assertEquals(ActionResponse.actionSuccess().getCode(),actionResponse.getCode());
    }

    @Test
    public void errorSbwIdIsBlankRemoveEipFromSbw(){
        String eipId = "81f3b43e-5a99-4acd-9a15-09e5aff4019e";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(null);
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw(eipId,param,token);

        assertEquals(400,actionResponse.getCode());
    }

    @Test
    public void errorEipIdNotFoundRemoveEipFromSbw(){
        String eipId = "123";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("0e40e97a-4b30-492e-8472-df3e2c044c2a");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw(eipId,param,token);

        assertEquals(404,actionResponse.getCode());
    }

    @Test
    public void errorSbwIdNotFoundRemoveEipFromSbw(){
        String eipId = "81f3b43e-5a99-4acd-9a15-09e5aff4019e";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiIyMTNiYzI4YS1hMTY1LTQ2MTYtYWUwMy0xNmFlZDNkZmJkYTYiLCJleHAiOjE1NjM0MjE1NjMsIm5iZiI6MCwiaWF0IjoxNTYzNDE2MTYzLCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMWE4YjdiLTBiYTQtNDZjMS05MjM5LWEzOTc2YzJhZWRmZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6ImYwZjY1NTRhLWExNGYtNGYyMS04OGQ3LTViNTRhZmJjNzVkYyIsImF1dGhfdGltZSI6MTU2MzQxNDM1Nywic2Vzc2lvbl9zdGF0ZSI6IjE2ZTA3ODY3LWFmZGUtNGNmOS1hMmZiLTgxN2VjNzM5OGRjYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiQUNDT1VOVF9BRE1JTiIsIm9mZmxpbmVfYWNjZXNzIiwiT1BFUkFURV9BRE1JTiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJyZHMtbXlzcWwtYXBpIjp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCIsInBob25lIjoiMTU5NjU4MTE2OTYiLCJwcm9qZWN0IjoieGluamluZyIsImdyb3VwcyI6WyIvZ3JvdXAteGluamluZyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4aW5qaW5nIn0.lat_JDKXefrXZ4cylqaCf5XrLd-nXYz6NXUMtbhyobt33RU5D6mAO1xT8BuKLm4E4fw9e4KY5v2EgUuvWFnvvuyZNJ7lw6ER6P_YpwBgKuf9ol2nnJjkkcpFKOHPePQurMxV1fFxox-4YcgAeCF6qT2QJAKkRQCz1uacabUJ672POO9HXIhAZRcNhctAvg6VRB-cAlElOfT-48ZVbG2WlN1xn6dr2SdV7waVOQzcTh2ZjTXcQRNQzHtY_8vLiyFXOVE_dghBGnh986FlauobvCxh2tJfamig4AFGJm_1FKXtt5d1pTxKq901cxsS5O1D2aPNqWu7vmQ-H8zKIFet6w";
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(55);
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("123");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw(eipId,param,token);

        assertEquals(404,actionResponse.getCode());
    }
}