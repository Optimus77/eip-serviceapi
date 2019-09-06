package com.inspur.eip.service.impl;

import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.entity.openapi.EipConstant;
import com.inspur.eip.entity.openapi.OpenCreateEip;
import com.inspur.eip.service.TokenUtil;
import com.inspur.eip.util.http.HttpClientUtil;
import groovy.util.logging.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

//import org.powermock.modules.junit4.rule.PowerMockRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(SpringRunner.class)
@ContextConfiguration(classes = OpenApiSbwServiceImpl.class)
@Rollback
@SpringBootTest(classes = EipServiceApplicationTests.class)
@Transactional
@PrepareForTest({HttpClientUtil.class})
public class OpenApiSbwServiceImplTest {


    @Autowired
    @InjectMocks
    OpenApiSbwServiceImpl openApiSbwService;


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void openapiCreateSbw() throws Exception {
        String token = TokenUtil.getToken("lishenghao", "1qaz2wsx3edc");
        OpenCreateEip openCreateEip = new OpenCreateEip();
        openCreateEip.setBandwidth("5");
        openCreateEip.setBillType(EipConstant.BILLTYPE_HOURLYSETTLEMENT);
        openCreateEip.setSbwName("openapitest-2");

        ResponseEntity responseEntityExpect = new ResponseEntity<>(null, HttpStatus.OK);


        PowerMockito.mockStatic(HttpClientUtil.class);
        PowerMockito.when(HttpClientUtil.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(responseEntityExpect);
        ResponseEntity responseEntity = openApiSbwService.OpenapiCreateSbw(openCreateEip, token);
        assertEquals(responseEntityExpect, responseEntity);
    }

    @Test
    public void openapiDeleteSbw() {
    }

    @Test
    public void openapiEipAddSbw() {
    }

    @Test
    public void openapiEipRemoveSbw() {
    }

    @Test
    public void openapiRenewSbw() {
    }

    @Test
    public void openapiSbwUpdateBandwidth() {
    }
}