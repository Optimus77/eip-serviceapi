package com.inspur.eip.controller.v2;

import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.openapi.OpenCreateEip;
import com.inspur.eip.service.OpenApiSbwService;
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

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value= VersionConstant.REST_VERSION_1_1, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1.1", description = "openEip API")
@Validated
public class OpenApiSbwController {

    @Autowired
    private OpenApiSbwService openApiSbwService;

    @PermissionContext(whitelist=true)
    @PostMapping("/eips/createSbw")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiCreateSbw( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Allocate a sbw:{}.", openCreateEip.toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiSbwService.OpenapiCreateSbw(openCreateEip, CommonUtil.getKeycloackToken());
    }

    @PermissionContext(whitelist=true)
    @DeleteMapping("/eips/deleteSbw")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiDeleteSbw( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Delete a sbw:{}.", openCreateEip.toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiSbwService.OpenapiDeleteSbw(openCreateEip, CommonUtil.getKeycloackToken());
    }


    @PermissionContext(whitelist=true)
    @PutMapping("/eips/eipAddSbw")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiEipAddSbw( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Add eip:{} to sbw:{}.", openCreateEip.getEipId(),openCreateEip.getSbwId());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiSbwService.OpenapiEipAddSbw(openCreateEip, CommonUtil.getKeycloackToken());
    }


    @PermissionContext(whitelist=true)
    @PutMapping("/eips/eipRemoveSbw")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiEipRemoveSbw( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Remove eip:{} from sbw:{}.", openCreateEip.getEipId(),openCreateEip.getSbwId());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiSbwService.OpenapiEipRemoveSbw(openCreateEip, CommonUtil.getKeycloackToken());
    }


    @PermissionContext(whitelist=true)
    @PutMapping("/eips/renewSbw")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApirenewEip( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("renew a sbw:{}.", openCreateEip.toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiSbwService.OpenapiRenewSbw(openCreateEip, CommonUtil.getKeycloackToken());
    }


    @PermissionContext(whitelist=true)
    @PutMapping("/eips/updateSbwBandwidth")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity OpenApiSbwupdateBandWidth( @RequestBody OpenCreateEip openCreateEip, BindingResult result) {
        log.info("Update bandwidth of a sbw:{}.", openCreateEip.toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return openApiSbwService.OpenapiSbwUpdateBandwidth(openCreateEip, CommonUtil.getKeycloackToken());
    }



}
