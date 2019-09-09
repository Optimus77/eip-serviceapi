package com.inspur.eip.service.impl;

import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.service.TokenUtil;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipPoolV6Repository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.util.constant.HsConstants;
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
import java.util.*;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SbwServiceImpl.class)
@Rollback
@SpringBootTest(classes = EipServiceApplicationTests.class)
@Transactional
public class SbwServiceImplTest {

    @Autowired
    SbwServiceImpl sbwService;
    @Autowired
    SbwDaoService sbwDaoService;
    @Autowired
    EipPoolV6Repository eipPoolV6Repository;
    @Autowired
    EipDaoService eipDaoService;
    @Autowired
    EipPoolRepository eipPoolRepository;
    @Autowired
    EipRepository eipRepository;

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
    public void atomCreateSbw() throws Exception {
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration("1");
        param.setBandwidth(100);
        param.setBillType("hourlySettlement");
        param.setRegion("cn-north-3");
        param.setSbwName("atomUnitTestNoOtherBuilder");
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ResponseEntity responseEntity = sbwService.atomCreateSbw(param, token);
        List<Sbw> list = sbwDaoService.findByProjectId("9d0b67cd-20cb-40b4-8dc4-b0415ca25d72");
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSbwName().equals("atomUnitTestNoOtherBuilder")) {
                deleteSbw(sbw.getId());
                sbw = list.get(i);
                break;
            }
        }
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    public void listByIdAndIsDeleteShareBandWidth() throws Exception {
        Integer pageIndex = 20;
        Integer pageSize = 100;
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listByIsDeleteAndSbwName() throws Exception {
        Integer pageIndex = 20;
        Integer pageSize = 100;
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, sbw.getSbwName());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listByIsDelete() {
        Integer pageIndex = 20;
        Integer pageSize = 100;
        String searchValue = null;
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, searchValue);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void listByProjectId() {
        Integer pageIndex = 0;
        Integer pageSize = 100;
        String searchValue = null;
        ResponseEntity responseEntity = sbwService.listShareBandWidth(pageIndex, pageSize, searchValue);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void deleteSbwInfo() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ActionResponse response = sbwService.deleteSbwInfo(sbw.getId(), token);

        assertEquals(200, response.getCode());
    }

    @Test
    public void errorDeletesbwIdIsBlank() throws Exception {
        String sbwId = null;
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ActionResponse response = sbwService.deleteSbwInfo(sbwId, token);

        assertEquals(400, response.getCode());
    }

    @Test
    public void bssSoftDeleteSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        ActionResponse actionResponse = sbwService.bssSoftDeleteSbw(sbw.getId());

        assertEquals(200, actionResponse.getCode());
    }

    @Test
    public void errorBssSoftDeleteSbw() {
        String sbwId = null;
        ActionResponse actionResponse = sbwService.bssSoftDeleteSbw(sbwId);

        assertEquals(400, actionResponse.getCode());
    }

    @Test
    public void getSbwDetail() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity sbwDetail = sbwService.getSbwDetail(sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, sbwDetail.getStatusCode());
    }

    @Test
    public void getSbwDetailSbwIdIsNull() throws Exception {
        ResponseEntity sbwDetail = sbwService.getSbwDetail(null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, sbwDetail.getStatusCode());
    }

    @Test
    public void getSbwDetailSbwIsNull() throws Exception {
        ResponseEntity sbwDetail = sbwService.getSbwDetail("123");
        assertEquals(HttpStatus.NOT_FOUND, sbwDetail.getStatusCode());
    }

    @Test
    public void updateSbwConfig() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        SbwUpdateParam param = new SbwUpdateParam();
        param.setSbwName("unitTestCheckout");
        param.setRegion("cn-north-3");
        param.setBillType("hourlySettlement");
        param.setBandwidth(202);
        param.setDuration("1");
        ActionResponse response = sbwService.updateSbwConfig(sbw.getId(), param, token);
        deleteSbw(sbw.getId());
        assertEquals(200, response.getCode());
    }

    @Test
    public void errorUpdateSbwConfig() throws Exception {
        String sbwId = null;
        SbwUpdateParam param = new SbwUpdateParam();
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        ActionResponse response = sbwService.updateSbwConfig(sbwId, param, token);

        assertEquals(500, response.getCode());
    }

    @Test
    public void countSbwNumsByProjectId() {
        ResponseEntity sbwCount = sbwService.countSbwNumsByProjectId();

        assertEquals(HttpStatus.OK, sbwCount.getStatusCode());
    }

    @Test
    public void countSbwNumsByStatus() {
        String status = "ACTIVE";
        ResponseEntity responseEntity = sbwService.countSbwNumsByStatus(status);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    /*@Test
    public void countSbwNumsByStatusError() {
        String status = "ERROR";
        ResponseEntity responseEntity = sbwService.countSbwNumsByStatus(status);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }*/

    @Test
    public void restartSbwService() throws Exception {
        Sbw sbw = creatSbw(HsConstants.MONTHLY, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration("1");
        param.setRegion("cn-north-3");
        ActionResponse response = sbwService.restartSbwService(sbw.getId(), param, token);
        deleteSbw(sbw.getId());
        assertEquals(200, response.getCode());
    }

    @Test
    public void errorRestartSbwService() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        SbwUpdateParam param = new SbwUpdateParam();
        ActionResponse response = sbwService.restartSbwService(sbw.getId(), param, token);
        deleteSbw(sbw.getId());
        assertEquals(400, response.getCode());
    }

    @Test
    public void stopSbwService() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration("0");
        ActionResponse response = sbwService.stopSbwService(sbw.getId(), param);
        deleteSbw(sbw.getId());
        assertEquals(200, response.getCode());
    }

    @Test
    public void errorStopSbwService() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        SbwUpdateParam param = new SbwUpdateParam();
        param.setDuration(null);
        ActionResponse response = sbwService.stopSbwService(sbw.getId(), param);
        deleteSbw(sbw.getId());
        assertEquals(400, response.getCode());
    }

    @Test
    public void sbwListEip() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        Eip eip = creatEip(HsConstants.HOURLYSETTLEMENT, null);
        addEipToSbw(eip.getId(), sbw.getId());
        Integer currentPage = 20;
        Integer limit = 50;
        ResponseEntity responseEntity = sbwService.sbwListEip(sbw.getId(), currentPage, limit);
        removeEipFromSbw(eip.getId(), sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    public void sbwListEipWithSbwIsNull() {
        String sbwId = "123";
        Integer currentPage = 0;
        Integer limit = 50;
        ResponseEntity responseEntity = sbwService.sbwListEip(sbwId, currentPage, limit);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void sbwListEipWithNOCurrentPage() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        Integer currentPage = 0;
        Integer limit = 50;
        ResponseEntity responseEntity = sbwService.sbwListEip(sbw.getId(), currentPage, limit);
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void renameSbw() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        SbwUpdateParam param = new SbwUpdateParam();
        param.setSbwName("rename");
        ResponseEntity responseEntity = sbwService.renameSbw(sbw.getId(), param);
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void errorrRenameSbw() {
        String sbwId = null;
        SbwUpdateParam param = new SbwUpdateParam();
        param.setSbwName(null);
        ResponseEntity responseEntity = sbwService.renameSbw(sbwId, param);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }


    @Test
    public void getOtherEips() throws Exception {
        Sbw sbw = creatSbw(HsConstants.HOURLYSETTLEMENT, null);
        ResponseEntity otherEips = sbwService.getOtherEips(sbw.getId());
        deleteSbw(sbw.getId());
        assertEquals(HttpStatus.OK, otherEips.getStatusCode());
    }

    @Test
    public void getOtherEipsSbwIsNull(){
        String sbwId = "123";
        ResponseEntity otherEips = sbwService.getOtherEips(sbwId);
        assertEquals(HttpStatus.BAD_REQUEST, otherEips.getStatusCode());
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