package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.ReturnMsg;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipAllocateParamWrapper;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.util.ReturnMsgUtil;
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
import org.springframework.web.context.WebApplicationContext;

import java.beans.PropertyEditor;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;

@PrepareForTest()
@RunWith(PowerMockRunner.class)
public class EipControllerAtomTest  {

    @InjectMocks
    private EipControllerAtom eipControllerAtom;
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
            List<FieldError> fieldErrors= new ArrayList<>();
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

    }

    @Test
    public void atomCreateEip() {
        EipAllocateParamWrapper eipConfig = new EipAllocateParamWrapper();
        EipAllocateParam eipAllocateParam = new EipAllocateParam();
        eipAllocateParam.setIpType("mobile");
        eipConfig.setEipAllocateParam(eipAllocateParam);
        ResponseEntity responseEntity = eipControllerAtom.atomCreateEip(eipConfig,result);
        Assert.assertEquals(responseEntity.getStatusCode().value() , 400);
    }

    @Test
    public void atomDeleteEip()  {
        ResponseEntity result = new ResponseEntity(HttpStatus.BAD_REQUEST);
        Mockito.when(eipService.atomDeleteEip(Mockito.anyString(),Mockito.anyString())).thenReturn(result);
        ResponseEntity responseEntity = eipControllerAtom.atomDeleteEip("abc","def");
        Assert.assertEquals(responseEntity.getStatusCode().value() , 400);

    }
}
