package com.inspur.eip.controller;


import com.inspur.eip.entity.ipv6.EipV6UpdateParam;
import com.inspur.eip.entity.v2.eipv6.EipV6AllocateParamWrapper;
import com.inspur.eip.entity.ipv6.EipV6UpdateParamWrapper;
import com.inspur.eip.service.impl.EipV6ServiceImpl;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.ReturnStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
@Api(value = "/v1", description = "eipv6 API")
@Validated
public class EipV6Controller {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EipV6ServiceImpl eipV6Service;

    @PostMapping(value = "/eipv6")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity allocateEipV6(@Valid @RequestBody EipV6AllocateParamWrapper eipV6Config, BindingResult result) {
        log.info("Allocate a eipv6:{}.", eipV6Config.getEipV6AllocateParam().toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        ResponseEntity responseEntity = eipV6Service.atomCreateEipV6(eipV6Config.getEipV6AllocateParam().getEipId());
        return responseEntity;
    }


    @GetMapping(value = "/eipv6")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listeipv6",notes="list")
    public ResponseEntity listEipV6(@RequestParam(required = false) String currentPage ,
                                    @RequestParam(required = false )String limit,
                                    @RequestParam(required = false )String status) {
        log.debug("EipController listEipv6, currentPage:{}, limit:{}", currentPage, limit);
        if(currentPage==null||limit==null){
            currentPage="0";
            limit="0";
        }else{
            try{
                int currentPageNum = Integer.parseInt(currentPage);
                int limitNum = Integer.parseInt(limit);
                if (currentPageNum < 0 || limitNum < 0) {
                    currentPage = "0";
                }
            }catch (Exception e){
                log.error("number is not correct ");
                currentPage="0";
                limit="0";
            }
        }
        return  eipV6Service.listEipV6s(Integer.parseInt(currentPage),Integer.parseInt(limit),status);

    }

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
    @GetMapping(value = "/eipv6/{eipv6_id}")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "geteipv6Detail", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eipv6_id", value = "the id of eipv6", required = true, dataType = "String"),
    })
    public ResponseEntity geteipV6Detail(@PathVariable("eipv6_id") String eipV6Id) {

        return eipV6Service.getEipV6Detail(eipV6Id);
    }


    @PutMapping(value = "/eipv6/{eipv6_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eipv6", notes = "put")
    public ResponseEntity updateEip(@PathVariable("eipv6_id") String eipV6Id,
                                    @Valid  @RequestBody (required = false) EipV6UpdateParamWrapper param, BindingResult result) {
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
