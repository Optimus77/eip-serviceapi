package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.openapi.OpenCreateEip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class OpenApiSbwControllerTest {
    @InjectMocks
    private OpenApiSbwController openApiSbwController;

    @Before
    public void setUp(){

    }

    @Test
    public void OpenApiCreateSbw(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiSbwController.OpenApiCreateSbw(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApiDeleteSbw(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiSbwController.OpenApiDeleteSbw(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApiEipAddSbw(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiSbwController.OpenApiEipAddSbw(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApiEipRemoveSbw(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiSbwController.OpenApiEipRemoveSbw(openCreateEip,result);
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
        ResponseEntity responseEntity = openApiSbwController.OpenApirenewEip(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }

    @Test
    public void OpenApiSbwupdateBandWidth(){
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.doReturn(true).when(result).hasErrors();
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.doReturn(fieldErrors).when(result).getFieldErrors();
        OpenCreateEip openCreateEip = new OpenCreateEip();
        ResponseEntity responseEntity = openApiSbwController.OpenApiSbwupdateBandWidth(openCreateEip,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
}
