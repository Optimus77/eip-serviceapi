package com.inspur.eip.controller.v2;

import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.service.impl.SbwServiceImpl;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import com.inspur.iam.adapter.annotation.PermissionContext;
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
@RequestMapping(value= VersionConstant.REST_VERSION_1_1, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1.1")
@Validated
public class EipGroupController {

    @Autowired
    private EipServiceImpl eipService;

    @Autowired
    private SbwServiceImpl sbwService;


    @PermissionContext(
            service="eip",
            action="ListEip",
            resourceType="instance")
    @GetMapping(value = "/groups/{pageNo}/{pageSize}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listeip",notes="list")
    public ResponseEntity listEip(@PathVariable("pageNo") String pageNo ,
                                  @PathVariable("pageSize") String pageSize,
                                  @RequestParam(required = false )String status,
                                  @RequestParam(required = false )String bandwidth,
                                  @Size(min=36, max=36, message = "Must be uuid.") @RequestParam(required = false )String sbwId) {
        log.debug("EipController listEip, currentPage:{}, limit:{}", pageNo, pageSize);

        if(StringUtils.isBlank(pageNo) ||StringUtils.isBlank(pageSize)){
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
        if(StringUtils.isNotBlank(sbwId)){
            if(HsConstants.SBW_UNBIND.equalsIgnoreCase(status)){
                return sbwService.getOtherEips(sbwId);
            }else if(HsConstants.SBW_BIND.equalsIgnoreCase(status)) {
                return sbwService.sbwListEip(sbwId, Integer.parseInt(pageNo), Integer.parseInt(pageSize));
            }else {
                return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.SC_PARAM_ERROR.getCode(),ErrorStatus.SC_PARAM_ERROR.getMessage()+" :status"), HttpStatus.BAD_REQUEST);
            }
        }
        if(StringUtils.isNotBlank(bandwidth)){
            return  eipService.listEipsByBandWidth(status);
        }
        return  eipService.listEips(Integer.parseInt(pageNo),Integer.parseInt(pageSize),status);
    }


    /**
     * get eip instance detail
     * @param groupId  the id of eip
     * @return  retrun
     */
   /* @ResourceContext(
            service= IEipService.class,
            method="getEipById")*/
    @PermissionContext(
            service="eip",
            action="GetEip",
            resourceType="instance")
    @GetMapping(value = "/groups/{group_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "get detail of  eip instance", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "group_id", value = "the id of group", required = true, dataType = "String"),
    })
    public ResponseEntity getEipGroupDetailSecond(@PathVariable("group_id") String groupId){

        return eipService.getEipGroupDetail(groupId);
    }


/*    @ResourceContext(
            service= IEipService.class,
            method="getEipById")*/
    @PermissionContext(
            service="eip",
            action="UpdateEip",
            resourceType="instance")
    @PutMapping(value = "/groups/{group_id}/action/{action}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eipGroup", notes = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "group_id", value = "the id of group", required = true, dataType = "String"),
    })
    public ResponseEntity updateEipGroup(@PathVariable("group_id") String groupId,
                                         @PathVariable("action") String action,
                                         @Valid @RequestBody EipUpdateParamWrapper param , BindingResult result) {

        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            log.info("{}",msgBuffer);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()), HttpStatus.BAD_REQUEST);
        }
        String msg;
        EipUpdateParam updateParam = param.getEip();

        switch (action){
            case HsConstants.BIND:
                log.info("bind operate, groupid:{}, param:{}", groupId, updateParam);
                if (updateParam.getType() != null) {
                    return eipService.eipGroupBindWithInstance(groupId, updateParam.getType(), updateParam.getServerId(),
                            updateParam.getPortId(), updateParam.getPrivateIp());
                } else {
                    msg = "need param serverid and type";
                }
                break;
            case HsConstants.UNBIND:
                log.info("unbind operate, groupid:{}, param:{} ", groupId, updateParam);
                return eipService.eipGroupUnbindWithInstacnce(groupId, null);
            default:
                msg="Param error, unknow action type"+action+"";
                log.error("Param error, unknow action type. groupid:{}, param:{} ", groupId, updateParam);
        }

        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msg), HttpStatus.BAD_REQUEST);

    }



}
