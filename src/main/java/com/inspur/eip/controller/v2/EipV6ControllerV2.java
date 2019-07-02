package com.inspur.eip.controller.v2;


import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.ipv6.EipV6AllocateParamWrapper;
import com.inspur.eip.entity.ipv6.EipV6UpdateParam;
import com.inspur.eip.entity.ipv6.EipV6UpdateParamWrapper;
import com.inspur.eip.service.impl.EipV6ServiceImpl;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.ReturnStatus;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.iam.adapter.annotation.PermissionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping(value= VersionConstant.REST_VERSION_1_1, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1.1", description = "eipv6 API")
@Validated
public class EipV6ControllerV2 {

    @Autowired
    private EipV6ServiceImpl eipV6Service;

    @PermissionContext(
            service="ipts",
            action="CreateIPv6",
            resourceType="instance")
    @PostMapping(value = "/eipv6")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity allocateEipV6(@Valid @RequestBody EipV6AllocateParamWrapper eipV6Config, BindingResult result) {
        log.info("Allocate a eipv6:{}.", eipV6Config.getEipV6AllocateParam().toString());
        if ( result.hasErrors() ) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>( ReturnMsgUtil.error( ReturnStatus.SC_PARAM_ERROR , msgBuffer.toString() ),
                    HttpStatus.BAD_REQUEST);
        }
        return eipV6Service.atomCreateEipV6( eipV6Config.getEipV6AllocateParam().getEipId(), CommonUtil.getKeycloackToken());
    }


    @PermissionContext(
            service="ipts",
            action="ListIPv6",
            resourceType="instance")
    @GetMapping(value = "/eipv6/{pageNo}/{pageSize}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listeipv6",notes="list")
    public ResponseEntity listEipV6(@PathVariable("pageNo") String pageNo ,
                                    @PathVariable("pageSize")String pageSize,
                                    @RequestParam(required = false )String status) {
        log.debug("EipController listEipv6, currentPage:{}, limit:{}", pageNo, pageSize);
        if(pageNo==null||pageSize==null){
            pageNo="0";
            pageSize="0";
        }else{
            try{
                int currentPageNum = Integer.parseInt(pageNo);
                int limitNum = Integer.parseInt(pageSize);
                if (currentPageNum < 0 || limitNum < 0) {
                    pageNo = "0";
                }
            }catch (Exception e){
                log.error("number is not correct ");
                pageNo="0";
                pageSize="0";
            }
        }
        return  eipV6Service.listEipV6s(Integer.parseInt(pageNo),Integer.parseInt(pageSize),status);

    }

    @PermissionContext(
            service="ipts",
            action="DeleteIPv6",
            resourceType="instance")
    @DeleteMapping(value = "/eipv6/{eipv6_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity deleteEip(@Size(min=36, max=36, message = "Must be uuid.")
                                        @PathVariable("eipv6_id") String eipV6Id) {
        //Check the parameters
        log.info("Atom delete the EipV6Id:{} ",eipV6Id);
        return eipV6Service.atomDeleteEipV6(eipV6Id);

    }

    /**
     * get eipV6 instance detail
     *
     * @param eipV6Id the id of eipV6
     * @return retrun
     */
    @PermissionContext(
            service="ipts",
            action="GetIPv6",
            resourceType="instance")
    @GetMapping(value = "/eipv6/{eipv6_id}")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "getEipv6Detail", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eipv6_id", value = "the id of eipv6", required = true, dataType = "String"),
    })
    public ResponseEntity getEipV6Detail(@PathVariable("eipv6_id") String eipV6Id) {
        return eipV6Service.getEipV6Detail(eipV6Id);
    }

    @PermissionContext(
            service="ipts",
            action="UpdateIPv6",
            resourceType="instance")
    @PutMapping(value = "/eipv6/{eipv6_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eipv6", notes = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eipv6_id", value = "the id of eipv6", required = true, dataType = "String"),
    })
    public ResponseEntity updateEip(@PathVariable("eipv6_id") String eipV6Id, @Valid @RequestBody EipV6UpdateParamWrapper param, BindingResult result) {
        log.info("update ipv6 ");
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            log.info("{}",msgBuffer);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()), HttpStatus.BAD_REQUEST);
        }
        EipV6UpdateParam updateParam = param.getEipv6();
        return eipV6Service.eipV6bindPort(eipV6Id, updateParam.getEipAddress());
    }
}
