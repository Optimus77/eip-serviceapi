package com.inspur.eip.controller.v2;

import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.openapi.OpenCreateEip;
import com.inspur.eip.service.OpenApiService;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.ReturnStatus;
import com.inspur.iam.adapter.annotation.PermissionContext;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value= VersionConstant.REST_VERSION_1_1, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1.1", description = "openEip API")
@Validated
public class OpenApiEipController {

    @Autowired
    private OpenApiService openApiService;

    @PermissionContext(whitelist=true)
    @PostMapping("/eips/createEip")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiCreateEip( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Allocate a eip:{}.", openCreateEip.toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiService.OpenapiCreateEip(openCreateEip, CommonUtil.getKeycloackToken());
    }

    @PermissionContext(whitelist=true)
    @PostMapping("/eips/createEipAddSbw")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiCreateEipAddSbw( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Allocate a eip:{}.", openCreateEip.toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiService.OpenapiCreateEipAddSbw(openCreateEip, CommonUtil.getKeycloackToken());
    }


    @PermissionContext(whitelist=true)
    @DeleteMapping("/eips/deleteEip")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiDeleteEip( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Allocate a eip:{}.", openCreateEip.toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiService.OpenapiDeleteEip(openCreateEip, CommonUtil.getKeycloackToken());
    }

}
