package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.ipv6.EipV6AllocateParam;
import com.inspur.eip.entity.ipv6.EipV6AllocateParamWrapper;
import com.inspur.eip.entity.ipv6.EipV6UpdateParamWrapper;
import com.inspur.eip.service.impl.EipV6ServiceImpl;
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
import java.util.HashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class EipV6ControllerV2Test {
    @InjectMocks
    private EipV6ControllerV2 eipV6ControllerV2;
    @Mock
    private EipV6ServiceImpl eipV6Service;


    @Before
    public void setUp(){
        ResponseEntity result = new ResponseEntity(HttpStatus.OK);
        Mockito.when(eipV6Service.listEipV6s(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyString())).thenReturn(result);
        Mockito.when(eipV6Service.atomDeleteEipV6(Mockito.anyString())).thenReturn(result);
    }

    @Test
    public void allocateEipV6(){
        EipV6AllocateParamWrapper eipV6Config = new EipV6AllocateParamWrapper();
        EipV6AllocateParam eipV6AllocateParam = new EipV6AllocateParam();
        eipV6Config.setEipV6AllocateParam(eipV6AllocateParam);
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.hasErrors()).thenReturn(true);
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.when(result.getFieldErrors()).thenReturn(fieldErrors);
        ResponseEntity responseEntity = eipV6ControllerV2.allocateEipV6(eipV6Config,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void listEipV6(){
        ResponseEntity responseEntity = eipV6ControllerV2.listEipV6("-1","b","UNBIND");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void deleteEip(){
        ResponseEntity responseEntity = eipV6ControllerV2.deleteEip("UNBIND");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void updateEip(){
        EipV6UpdateParamWrapper param = new EipV6UpdateParamWrapper();
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.hasErrors()).thenReturn(true);
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.when(result.getFieldErrors()).thenReturn(fieldErrors);
        ResponseEntity responseEntity = eipV6ControllerV2.updateEip("UNBIND",param,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
}
