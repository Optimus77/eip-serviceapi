package com.inspur.eip.controller.v2;

import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.sbw.SbwUpdateParamWrapper;
import com.inspur.eip.service.impl.SbwServiceImpl;
import com.inspur.eip.util.*;
import com.inspur.eip.util.constant.ReturnStatus;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping(value = VersionConstant.REST_VERSION_1_1, produces = {"application/json;charset=UTF-8"})
@Api(value = "/v1.1", description = "sbw API")
@Validated
public class SbwControllerV2 {

    @Autowired
    private SbwServiceImpl sbwService;
//    @ICPControllerLog
//    @PostMapping(value = "/sbws")
//    @CrossOrigin(origins = "*", maxAge = 3000)
//    @ApiOperation(value = "atomCreateSbw", notes = "createSbw")
//    public ResponseEntity atomAllocateSbw(@Valid @RequestBody SbwUpdateParamWrapper sbwConfig, BindingResult result) {
//        log.info("Create a sbws Atom param:{}.", sbwConfig.getSbw().toString());
//        if (result.hasErrors()) {
//            StringBuilder builder = new StringBuilder();
//            List<FieldError> fieldErrors = result.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                builder.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
//            }
//            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, builder.toString()),
//                    HttpStatus.BAD_REQUEST);
//        }
//        return sbwService.atomCreateSbw(sbwConfig.getSbw(), CommonUtil.getKeycloackToken());
//    }

    @ICPControllerLog
    @GetMapping(value = "/sbws/{pageNo}/{pageSize}")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "listsbw", notes = "list")
    public ResponseEntity listSbw(@PathVariable( required = false, name = "pageNo") String pageNo,
                                  @PathVariable( required = false, name = "pageSize") String pageSize,
                                  @RequestParam(required = false, name = "searchValue") String searchValue) {
        log.debug("Atom listSbw method param pageNo:{}, pageSize:{}, searchValue:{}", pageNo, pageSize, searchValue);
        if (StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize)) {
            pageNo = "0";
            pageSize = "0";
        } else {
            try {
                int currentPageNum = Integer.parseInt(pageNo);
                int limitNum = Integer.parseInt(pageSize);
                if (currentPageNum < 0 || limitNum < 0) {
                    pageNo = "0";
                }
            } catch (Exception e) {
                log.error("number is not correct ");
                pageNo = "0";
                pageSize = "0";
            }
        }
        return sbwService.listShareBandWidth(Integer.parseInt(pageNo), Integer.parseInt(pageSize), searchValue);
    }

//    @DeleteMapping(value = "/sbws/{sbw_id}")
//    @ICPControllerLog
//    @CrossOrigin(origins = "*", maxAge = 3000)
//    public ResponseEntity deleteSbw(@Size(min = 36, max = 36, message = "Must be uuid.")
//                                        @PathVariable("sbw_id") String id) {
//        log.info("Atom delete the sbw , id:{} ", id);
//        ActionResponse actionResponse = sbwService.deleteSbwInfo(id, CommonUtil.getKeycloackToken());
//        if (actionResponse.isSuccess()){
//            return new ResponseEntity(ReturnMsgUtil.success(),HttpStatus.OK);
//        }
//        return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(actionResponse.getCode()), actionResponse.getFault()), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
    /**
     * get sbw instance detail
     *
     * @param sbwId the id of sbw
     * @return retrun
     */
    @ICPControllerLog
    @GetMapping(value = "/sbws/{sbw_id}")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "getSbwDetail", notes = "sbwDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity getSbwDetail(@Size(min=36, max=36, message = "Must be uuid.")@PathVariable("sbw_id") String sbwId) {
        log.debug("Atom get the sbw detail , id:{} ", sbwId);
        return sbwService.getSbwDetail(sbwId);
    }

    /**
     * 当前用户sbw数量，概览页显示
     *
     * @return response
     */
    @ICPControllerLog
    @GetMapping(value = "/sbws/instance-num")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "get Sbw Count", notes = "get number")
    public ResponseEntity getSbwCount(@RequestParam(required = false )String status) {
        if (StringUtils.isNotBlank(status)){
            return sbwService.countSbwNumsByStatus(status);
        }else {
            return sbwService.countSbwNumsByProjectId();
        }
    }
//
//    @ICPControllerLog
//    @PostMapping(value = "/sbws/{sbw_id}/action/renewals")
//    @CrossOrigin(origins = "*", maxAge = 3000)
//    public ResponseEntity renewSbw(@PathVariable("sbw_id") String id,
//                                   @RequestBody SbwUpdateParamWrapper param) {
//        log.info("Atom renew or softdown sbw id:{}, param:{}.", id, param.getSbw().toString());
//        ActionResponse actionResponse = sbwService.restartSbwService(id, param.getSbw(), CommonUtil.getKeycloackToken());
//        if (actionResponse.isSuccess()) {
//            return new ResponseEntity(ReturnMsgUtil.success(),HttpStatus.OK);
//        }
//        return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(actionResponse.getCode()), actionResponse.getFault()), HttpStatus.INTERNAL_SERVER_ERROR);
//    }


    /**
     * modify sbw name
     * @return ret
     */
    @ICPControllerLog
    @PutMapping(value = "/sbws/{sbw_id}/action/rename", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "rename sbw name", notes = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity renameSbw(@PathVariable("sbw_id") String sbwId, @Valid @RequestBody SbwUpdateParamWrapper param , BindingResult result){
        log.info("Atom rename sbw param:{}",param.getSbw().toString());
        if (result.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                builder.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            log.info("{}",builder);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, builder.toString()), HttpStatus.BAD_REQUEST);
        }
        String msg;
        if (StringUtils.isNotBlank(param.getSbw().getSbwName())){
            return sbwService.renameSbw(sbwId, param.getSbw());
        }else {
            msg="The new sbw name must not be blank";
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msg), HttpStatus.BAD_REQUEST);
    }

//
//    @ICPControllerLog
//    @GetMapping(value = "/sbws/{sbw_id}/untyingEips")
//    @CrossOrigin(origins = "*",maxAge = 3000)
//    @ApiOperation(value = "get othereips without the sbw", notes = "get")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
//    })
//    public ResponseEntity getOtherEips(@PathVariable("sbw_id") String id){
//        log.info("Atom get the other Eip have not bind in sbw,Sbwid:{}",id);
//        return sbwService.getOtherEips(id);
//    }

//
//    @ICPControllerLog
//    @PutMapping(value = "/sbws/{sbw_id}/action/adjustBandwidth", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "update Sbw config", notes = "post")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "path", name = "eip", value = "the sbw wrapper ", required = true, dataType = "json"),
//    })
//    @CrossOrigin(origins = "*", maxAge = 3000)
//    public ResponseEntity updateSbwConfig(@PathVariable("sbw_id") String id, @Valid @RequestBody SbwUpdateParamWrapper param, BindingResult result) {
//        log.info("Atom update sbw id:{},param:{}.", id, param.getSbw().toString());
//        if (result.hasErrors()) {
//            StringBuilder builder = new StringBuilder();
//            List<FieldError> fieldErrors = result.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                builder.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
//            }
//            log.info("{}", builder);
//            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, builder.toString()), HttpStatus.BAD_REQUEST);
//        }
//        ActionResponse actionResponse;
//        if (param.getSbw().getBillType() != null ) {
//            log.info("update bandWidth, sbwid:{}, param:{} ", id, param.getSbw());
//             actionResponse = sbwService.updateSbwConfig(id, param.getSbw(), CommonUtil.getKeycloackToken());
//             if (actionResponse.isSuccess()){
//                 return new ResponseEntity(ReturnMsgUtil.success(),HttpStatus.OK);
//             }
//        }
//        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()), HttpStatus.BAD_REQUEST);
//    }
}
