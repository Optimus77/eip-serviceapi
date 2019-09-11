package com.inspur.eip.service;

import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.service.impl.EipV6ServiceImpl;
import com.inspur.eip.util.constant.HsConstants;
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


import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SbwDaoService.class)
@Rollback
@SpringBootTest(classes = EipServiceApplicationTests.class)
@Transactional
public class SbwDaoServiceTest {

    @Autowired
    private SbwDaoService sbwDaoService;

    @Autowired
    private FirewallService firewallService;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private SbwRepository sbwRepository;

    @Autowired
    EipDaoService eipDaoService;
    @Autowired
    EipPoolRepository eipPoolRepository;
    @Autowired
    EipV6ServiceImpl eipV6Service;

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
    public void errorIsDeleteDeleteSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        deleteSbw(sbw.getId());
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ActionResponse response = sbwDaoService.deleteSbw(sbw.getId(), token);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getCode());
    }

    @Test
    public void deleteSbwBandEip() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ActionResponse response = sbwDaoService.deleteSbw(sbw.getId(), token);
        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getCode());
    }

    @Test
    public void errorIsDeleteAdminDeleteSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        deleteSbw(sbw.getId());
        ActionResponse response = sbwDaoService.adminDeleteSbw(sbw.getId());

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getCode());
    }

    @Test
    public void deleteSbwAdminBandEip() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        ActionResponse response = sbwDaoService.adminDeleteSbw(sbw.getId());
        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getCode());
    }

    @Test
    public void errorBillTypeRenewSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);

        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ActionResponse actionResponse = sbwDaoService.renewSbwInfo(sbw.getId(), token);
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionResponse.getCode());
    }

    @Test
    public void errorBilltypeAddEipIntoSbw() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.MONTHLY, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(sbw.getBandWidth());
        param.setBillType("monthly");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(sbw.getId());
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eip.getId(), param, token);
        deleteSbw(sbw.getId());
        assertEquals(400, actionResponse.getCode());
    }

    @Test
    public void addEipIntoSbw() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(sbw.getBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(sbw.getId());
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eip.getId(), param, token);

        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(ActionResponse.actionSuccess().getCode(), actionResponse.getCode());
    }

    @Test
    public void errorSbwIdIsBlankAddEipIntoSbw() throws Exception {
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
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

        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eip.getId(), param, token);

        assertEquals(400, actionResponse.getCode());
    }

    @Test
    public void errorEipIdAddEipIntoSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        String eipId = "123";
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(sbw.getBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(sbw.getId());
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eipId, param, token);
        deleteSbw(sbw.getId());
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void errorEipBandV6AddEipIntoSbw() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        eipV6Service.atomCreateEipV6(eip.getId(),token);
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(sbw.getBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(sbw.getId());
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eip.getId(), param, token);
        deleteSbw(sbw.getId());
        eipV6Service.atomDeleteEipV6(eip.getEipV6Id());
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void errorEipAlreadyAddEipIntoSbw() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(sbw.getBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(sbw.getId());
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.addEipIntoSbw(eip.getId(), param, token);
        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(400, actionResponse.getCode());
    }

    @Test
    public void removeEipFromSbwTest() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(eip.getOldBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(sbw.getId());
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw(eip.getId(), param, token);
        deleteSbw(sbw.getId());
        assertEquals(ActionResponse.actionSuccess().getCode(), actionResponse.getCode());
    }

    @Test
    public void errorSbwIdIsBlankRemoveEipFromSbw() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(eip.getOldBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(null);
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw(eip.getId(), param, token);
        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(400, actionResponse.getCode());
    }

    @Test
    public void errorEipIdNotFoundRemoveEipFromSbw() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(eip.getOldBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId(sbw.getId());
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw("123", param, token);
        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(404, actionResponse.getCode());
    }

    @Test
    public void errorSbwIdNotFoundRemoveEipFromSbw() throws Exception {
        Sbw sbw = null;
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        if(null != eip)
            sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(),sbw.getId());
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        EipUpdateParam param = new EipUpdateParam();
        param.setBandwidth(eip.getOldBandWidth());
        param.setBillType("hourlySettlement");
        param.setChargemode(null);
        param.setDuration("1");
        param.setSbwId("123");
        param.setPortId(null);
        param.setPrivateIp(null);
        param.setServerId(null);
        param.setType(null);
        ActionResponse actionResponse = sbwDaoService.removeEipFromSbw(eip.getId(), param, token);
        removeEipFromSbw(eip.getId(),sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(404, actionResponse.getCode());
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

