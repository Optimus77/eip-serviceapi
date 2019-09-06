package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.sbw.SbwUpdateParamWrapper;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
public class SbwControllerV2Test {
    @InjectMocks
    private SbwControllerV2 sbwControllerV2;
    @Mock
    private SbwServiceImpl sbwService;


    @Before
    public void setUp(){
        ResponseEntity result = new ResponseEntity(HttpStatus.OK);
        Mockito.doReturn(result).when(sbwService).listShareBandWidth(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyString());
        Mockito.doReturn(result).when(sbwService).getSbwDetail(Mockito.anyString());
        Mockito.doReturn(result).when(sbwService).countSbwNumsByStatus(Mockito.anyString());
        Mockito.doReturn(result).when(sbwService).countSbwNumsByProjectId();

    }

    @Test
    public void listSbw(){
        ResponseEntity responseEntity = sbwControllerV2.listSbw("-1","22","33");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void listSbwCase2(){
        ResponseEntity responseEntity = sbwControllerV2.listSbw("-1","a","33");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void getSbwDetail(){
        ResponseEntity responseEntity = sbwControllerV2.getSbwDetail("-1");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void getSbwCount(){
        ResponseEntity responseEntity = sbwControllerV2.getSbwCount("-1");
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }
    @Test
    public void getSbwCountCase2(){
        ResponseEntity responseEntity = sbwControllerV2.getSbwCount(null);
        Assert.assertEquals(responseEntity.getStatusCode().value(),200);
    }

    @Test
    public void renameSbw(){
        SbwUpdateParamWrapper param = new SbwUpdateParamWrapper();
        SbwUpdateParam sbw = new SbwUpdateParam();
        param.setSbw(sbw);
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.hasErrors()).thenReturn(true);
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("11","22","33");
        fieldErrors.add(fieldError);
        Mockito.when(result.getFieldErrors()).thenReturn(fieldErrors);
        ResponseEntity responseEntity = sbwControllerV2.renameSbw("-1",param,result);
        Assert.assertEquals(responseEntity.getStatusCode().value(),400);
    }
}
