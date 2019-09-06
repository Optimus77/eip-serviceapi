package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.openapi.OpenCreateEip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class OpenApiEipControllerTest {
    @InjectMocks
    private OpenApiEipController openApiEipController;

    @Before
    public void setUp(){

    }

    @Test
    public void OpenApiCreateEip(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiEipController.OpenApiCreateEip(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApiCreateEipAddSbw(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiEipController.OpenApiCreateEipAddSbw(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApiDeleteEip(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiEipController.OpenApiDeleteEip(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApirenewEip(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiEipController.OpenApirenewEip(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApiEipUpdateBandWidth(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiEipController.OpenApiEipUpdateBandWidth(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApicreateIptsBandEip(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiEipController.OpenApicreateIptsBandEip(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
}
