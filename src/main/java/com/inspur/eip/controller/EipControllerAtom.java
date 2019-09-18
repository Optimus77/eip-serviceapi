package com.inspur.eip.controller;


import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipAllocateParamWrapper;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.ReturnMsgUtil;
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
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value= VersionConstant.REST_VERSION_1_1ATOM, produces={"application/json;charset=UTF-8"})
@Api(value = "atom/v1.1",description = "eip atom")
@Validated

public class EipControllerAtom {


    @Autowired
    private EipServiceImpl eipService;

    /**
     * Atome create eip
     * @param eipConfig
     * @param result
     * @return
     */

    @PermissionContext(whitelist=true)
    @PostMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity atomCreateEip(@Valid @RequestBody EipAllocateParamWrapper eipConfig, BindingResult result){
        log.info("Allocate a eip:{}.",eipConfig.getEipAllocateParam().toString());
        if (result.hasErrors()){
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }

        return eipService.atomCreateEip(eipConfig.getEipAllocateParam(),CommonUtil.getKeycloackToken(), "ECS");
    }

    /**
     * Atome delete eip
     * @param eipId
     * @return
     */

    @PermissionContext(whitelist=true)
    @DeleteMapping(value = "/eips/{eip_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity atomDeleteEip(@Size(min=36, max=36, message = "Must be uuid.")
                                        @PathVariable("eip_id") String eipId) {
        //Check the parameters
        log.info("Atom delete the Eip:{} ",eipId);
        return eipService.atomDeleteEip(eipId);

    }

    @PermissionContext(whitelist=true)
    @PostMapping(value = "/groups")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity atomCreateEipGroup(@Valid @RequestBody List<EipAllocateParam> eipConfig, BindingResult result) throws KeycloakTokenException {
        //log.info("Allocate a eip:{}.",eipConfig.getEipAllocateParam().toString());
        if (result.hasErrors()){
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }

        return eipService.atomCreateEipGroup(eipConfig,CommonUtil.getKeycloackToken(), "ECS");
    }

    /**
     * Atome delete eip
     * @param groupId
     * @return
     */

    @PermissionContext(whitelist=true)
    @DeleteMapping(value = "/groups/{group_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity atomDeleteEipGroup(@PathVariable("group_id") String groupId) {
        //Check the parameters
        log.info("Atom delete the Eip:{} ",groupId);
        return eipService.atomDeleteEipGroup(groupId);

    }



}
