package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.entity.LogLevel;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.service.impl.SbwServiceImpl;
import org.hibernate.validator.constraints.ModCheck;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@PrepareForTest()
@RunWith(PowerMockRunner.class)
public class EipControllerV2Test {
    @InjectMocks
    private EipControllerV2 eipControllerV2;
    @Mock
    private SbwServiceImpl sbwService;
    @Mock
    private EipServiceImpl eipService;

    public BindingResult result = new BindingResult() {
        @Override
        public Object getTarget() {
            return null;
        }

        @Override
        public Map<String, Object> getModel() {
            return null;
        }

        @Override
        public Object getRawFieldValue(String field) {
            return null;
        }

        @Override
        public PropertyEditor findEditor(String field, Class<?> valueType) {
            return null;
        }

        @Override
        public PropertyEditorRegistry getPropertyEditorRegistry() {
            return null;
        }

        @Override
        public String[] resolveMessageCodes(String errorCode) {
            return new String[0];
        }

        @Override
        public String[] resolveMessageCodes(String errorCode, String field) {
            return new String[0];
        }

        @Override
        public void addError(ObjectError error) {

        }

        @Override
        public String getObjectName() {
            return null;
        }

        @Override
        public void setNestedPath(String nestedPath) {

        }

        @Override
        public String getNestedPath() {
            return null;
        }

        @Override
        public void pushNestedPath(String subPath) {

        }

        @Override
        public void popNestedPath() throws IllegalStateException {

        }

        @Override
        public void reject(String errorCode) {

        }

        @Override
        public void reject(String errorCode, String defaultMessage) {

        }

        @Override
        public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {

        }

        @Override
        public void rejectValue(String field, String errorCode) {

        }

        @Override
        public void rejectValue(String field, String errorCode, String defaultMessage) {

        }

        @Override
        public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {

        }

        @Override
        public void addAllErrors(Errors errors) {

        }

        @Override
        public boolean hasErrors() {
            return true;
        }

        @Override
        public int getErrorCount() {
            return 0;
        }

        @Override
        public List<ObjectError> getAllErrors() {
            return null;
        }

        @Override
        public boolean hasGlobalErrors() {
            return false;
        }

        @Override
        public int getGlobalErrorCount() {
            return 0;
        }

        @Override
        public List<ObjectError> getGlobalErrors() {
            return null;
        }

        @Override
        public ObjectError getGlobalError() {
            return null;
        }

        @Override
        public boolean hasFieldErrors() {
            return false;
        }

        @Override
        public int getFieldErrorCount() {
            return 0;
        }

        @Override
        public List<FieldError> getFieldErrors() {
            List<FieldError> fieldErrors = new ArrayList<>();
            FieldError fieldError = new FieldError("11","22","33");
            fieldErrors.add(fieldError);
            return fieldErrors;
        }

        @Override
        public FieldError getFieldError() {
            return null;
        }

        @Override
        public boolean hasFieldErrors(String field) {
            return false;
        }

        @Override
        public int getFieldErrorCount(String field) {
            return 0;
        }

        @Override
        public List<FieldError> getFieldErrors(String field) {
            return null;
        }

        @Override
        public FieldError getFieldError(String field) {
            return null;
        }

        @Override
        public Object getFieldValue(String field) {
            return null;
        }

        @Override
        public Class<?> getFieldType(String field) {
            return null;
        }
    };

    @Before
    public void setUp(){
        ResponseEntity result = new ResponseEntity(HttpStatus.OK);
        Mockito.when(sbwService.getOtherEips(Mockito.anyString())).thenReturn(result);
        Mockito.when(sbwService.sbwListEip(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(result);
        Mockito.when(eipService.listEipsByBandWidth(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.getEipCount()).thenReturn(result);
        Mockito.when(eipService.getUsingEipCountByStatus(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.getEipDetail(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.getEipByInstanceIdV2(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.getEipDetailsByIpAddress(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.eipBindWithInstance(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.eipUnbindWithInstacnce(Mockito.anyString())).thenReturn(result);
        Mockito.when(eipService.getEipStatistics()).thenReturn(result);
    }

    @Test
    public void listEipCase1(){
        ResponseEntity responseEntity = eipControllerV2.listEip("1","2","UNBIND","bandwidth","sbwid");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void listEipCase2(){
        ResponseEntity responseEntity = eipControllerV2.listEip("1","2","BIND","bandwidth","sbwid");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void listEipCase3(){
        ResponseEntity responseEntity = eipControllerV2.listEip("a","2","BIND","bandwidth",null);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void getEipCount(){
        ResponseEntity responseEntity = eipControllerV2.getEipCount("abc");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void getEipCount_Null(){
        ResponseEntity responseEntity = eipControllerV2.getEipCount(null);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void getEipDetailSecond(){
        ResponseEntity responseEntity = eipControllerV2.getEipDetailSecond("12");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void getEipByInstanceIdSecondCase1(){
        ResponseEntity responseEntity = eipControllerV2.getEipByInstanceIdSecond("12","34","56");
        Assert.assertEquals(responseEntity.getStatusCode().value(),403);
    }
    @Test
    public void getEipByInstanceIdSecondCase2(){
        ResponseEntity responseEntity = eipControllerV2.getEipByInstanceIdSecond("12",null,"56");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void getEipByInstanceIdSecondCase3(){
        ResponseEntity responseEntity = eipControllerV2.getEipByInstanceIdSecond(null,"34","1709d6c6");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void updateEipCase1(){
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        ResponseEntity responseEntity = eipControllerV2.updateEip("11","22",param,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
    @Test
    public void updateEipCase2(){
        BindingResult result1 = Mockito.mock(BindingResult.class);
        Mockito.when(result1.hasErrors()).thenReturn(false);
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        EipUpdateParam Eip = new EipUpdateParam();
        param.setEip(Eip);
        ResponseEntity responseEntity = eipControllerV2.updateEip("11","BIND",param,result1);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
    @Test
    public void updateEipCase3(){
        BindingResult result1 = Mockito.mock(BindingResult.class);
        Mockito.when(result1.hasErrors()).thenReturn(false);
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        EipUpdateParam eip = new EipUpdateParam();
        eip.setType("ecs");
        eip.setServerId("11");
        eip.setPortId("22");
        eip.setPrivateIp("33");
        param.setEip(eip);
        ResponseEntity responseEntity = eipControllerV2.updateEip("11","bind",param,result1);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void updateEipCase4(){
        BindingResult result1 = Mockito.mock(BindingResult.class);
        Mockito.when(result1.hasErrors()).thenReturn(false);
        EipUpdateParamWrapper param = new EipUpdateParamWrapper();
        EipUpdateParam eip = new EipUpdateParam();
        ResponseEntity responseEntity = eipControllerV2.updateEip("11","unbind",param,result1);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void getEipStatistics(){
        ResponseEntity responseEntity = eipControllerV2.getEipStatistics();
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void eipHealthCheck(){
        ResponseEntity responseEntity = eipControllerV2.eipHealthCheck();
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void setDebugLevel(){
        LogLevel param = LogLevel.builder().level(null).build();
        ResponseEntity responseEntity = eipControllerV2.setDebugLevel("abc",param);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }


}

