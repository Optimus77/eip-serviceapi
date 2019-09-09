package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.service.impl.SbwServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
public class EipGroupControllerTest {
    @InjectMocks
    private EipGroupController eipGroupController;
    @Mock
    private SbwServiceImpl sbwService;
    @Mock
    private EipServiceImpl eipService;

    @Before
    public void setUp(){
        ResponseEntity result = new ResponseEntity(HttpStatus.OK);
        Mockito.when(sbwService.getOtherEips(Mockito.anyString())).thenReturn(result);
        Mockito.when(sbwService.sbwListEip(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(result);
        Mockito.when(eipService.listEipsByBandWidth(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.getEipByInstanceIdV2(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.getEipGroupByIpAddress(Mockito.anyString())).thenReturn(result);
        Mockito.doReturn(result).when(eipService).eipGroupBindWithInstance(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Mockito.when(eipService.eipGroupUnbindWithInstacnce(Mockito.anyString(),eq(null))).thenReturn(result);
    }

    @Test
    public void listEipCase1(){
        ResponseEntity responseEntity = eipGroupController.listEip("-1","b","UNBIND","12","34");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void listEipCase2(){
        ResponseEntity responseEntity = eipGroupController.listEip("-1","b","BIND","12","34");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void listEipCase3(){
        ResponseEntity responseEntity = eipGroupController.listEip("-1","b","aBIND","12","34");
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
    @Test
    public void listEipCase4(){
        ResponseEntity responseEntity = eipGroupController.listEip("-1","b","BIND","12",null);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void getEipByIPAddressCase1(){
        ResponseEntity responseEntity = eipGroupController.getEipByIPAddress("11","22");
        Assert.assertEquals(responseEntity.getStatusCode().value(),403);
    }
    @Test
    public void getEipByIPAddressCase2(){
        ResponseEntity responseEntity = eipGroupController.getEipByIPAddress("11",null);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void getEipByIPAddressCase3(){
        ResponseEntity responseEntity = eipGroupController.getEipByIPAddress(null,"22");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void updateEipGroup(){
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.hasErrors()).thenReturn(true);
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.when(result.getFieldErrors()).thenReturn(fieldErrors);
        ResponseEntity responseEntity = eipGroupController.updateEipGroup(null,"22",param,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
    @Test
    public void updateEipGroupCase2(){
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        EipUpdateParam eip = new EipUpdateParam();
        eip.setType("ecs");
        eip.setServerId("11");
        eip.setPortId("22");
        eip.setPrivateIp("33");
        param.setEip(eip);
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.hasErrors()).thenReturn(false);
        ResponseEntity responseEntity = eipGroupController.updateEipGroup("11","bind",param,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void updateEipGroupCase3(){
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.hasErrors()).thenReturn(false);
        ResponseEntity responseEntity = eipGroupController.updateEipGroup("11","unbind",param,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void updateEipGroupCase4(){
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.hasErrors()).thenReturn(false);
        ResponseEntity responseEntity = eipGroupController.updateEipGroup("11","aunbind",param,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
}
