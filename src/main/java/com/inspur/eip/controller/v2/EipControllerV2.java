package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.LogLevel;
import com.inspur.eip.service.IEipService;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.service.impl.SbwServiceImpl;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import com.inspur.iam.adapter.annotation.PermissionContext;
import com.inspur.iam.adapter.annotation.ResourceContext;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(value= VersionConstant.REST_VERSION_1_1, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1.1")
@Validated
public class EipControllerV2 {

    @Autowired
    private EipServiceImpl eipService;

    @Autowired
    private SbwServiceImpl sbwService;

    private String authScret = "kitlv7i2";


    @PermissionContext(
            service="eip",
            action="ListEip",
            resourceType="instance")
    @GetMapping(value = "/eips/{pageNo}/{pageSize}")
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
     * get number of user
     * @return response
     */
    @PermissionContext(
            service="eip",
            action="ListEip",
            resourceType="instance")
    @GetMapping(value = "/eips/instance-num")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getEipCount(@RequestParam(required = false )String status) {
        if(status == null){
            return  eipService.getEipCount();
        }else {
            return eipService.getUsingEipCountByStatus(status);
        }
    }




    /**
     * get eip instance detail
     * @param eipId  the id of eip
     * @return  retrun
     */
   @ResourceContext(
            service= IEipService.class,
            method="getEipById")
    @PermissionContext(
            service="eip",
            action="GetEip",
            resourceType="instance",
            resource="{eipId}")
    @GetMapping(value = "/eips/{eip_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "get detail of  eip instance", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
    })
    public ResponseEntity getEipDetailSecond(@PathVariable("eip_id") String eipId){

        return eipService.getEipDetail(eipId);
    }

    /**
     * get eip instance detail
     * @param eipAddress  the id of eip
     * @param resourceId  the id of eip
     * @return  retrun
     */
    @PermissionContext(whitelist=true)
    @GetMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="getEipByConditions",notes="get")
    public ResponseEntity getEipByInstanceIdSecond(@RequestParam(required = false) String resourceId,
                                                   @RequestParam(required = false) String eipAddress,
                                                   @RequestParam(required = false) String key)  {

        if((null != resourceId) && (null != eipAddress) ){
            return new ResponseEntity<>("To be wrong.", HttpStatus.FORBIDDEN);
        } else if(resourceId != null) {
            log.debug("EipController get eip by instance id:{} ", resourceId);
            return eipService.getEipByInstanceIdV2(resourceId);
        } else if (null != eipAddress){
            log.debug("EipController get eip by ip:{} ", eipAddress);
            if(null != key){
                if ("1709d6c6".equals(key)) {
                    return eipService.getEipDetailsByIpAddress(eipAddress);
                }
            }
            return eipService.getEipByIpAddressV2(eipAddress);
        }
        return new ResponseEntity<>("not found.", HttpStatus.NOT_FOUND);
    }

    @ResourceContext(
            service= IEipService.class,
            method="getEipById")
    @PermissionContext(
            service="eip",
            action="UpdateEip",
            resourceType="instance",
            resource="{eipId}")
    @PutMapping(value = "/eips/{eip_id}/action/{action}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eip", notes = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
    })
    public ResponseEntity updateEip(@PathVariable("eip_id") String eipId,
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
                log.info("bind operate, eipid:{}, param:{}", eipId, updateParam);
                if (updateParam.getType() != null) {
                    return eipService.eipBindWithInstance(eipId, updateParam.getType(), updateParam.getServerId(),
                            updateParam.getPortId(), updateParam.getPrivateIp());
                } else {
                    msg = "need param serverid and type";
                }
                break;
            case HsConstants.UNBIND:
                log.info("unbind operate, eipid:{}, param:{} ", eipId, updateParam);
                return eipService.eipUnbindWithInstacnce(eipId);
            default:
                msg="Param error, unknow action type"+action+"";
                log.error("Param error, unknow action type. eipid:{}, param:{} ", eipId, updateParam);
        }

        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msg), HttpStatus.BAD_REQUEST);

    }

    @PermissionContext(whitelist=true)
    @GetMapping(value = "/statistics")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity getEipStatistics() {
        return  eipService.getEipStatistics();
    }



    @PermissionContext(whitelist=true)
    @GetMapping(value = "/health-status")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "health check")
    public ResponseEntity eipHealthCheck() {
        //HealthCheck
        String code;
        String msg;
        code = ReturnStatus.SC_OK;
        msg ="The eip is running";
        log.info(msg);

        return new ResponseEntity<>(ReturnMsgUtil.msg(code, msg, null), HttpStatus.OK);
    }

    @PermissionContext(whitelist=true)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @PostMapping(value = "/loggers/{package}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setDebugLevel(@PathVariable("package") String packageName, @RequestBody LogLevel param) {
        log.info("Set debug level to:{}", param);
        //trace --> debug --> info --> warn --> error -->fatal
        try{
            String configLevel = param.getLevel();
            if(null == configLevel){
                configLevel = "INFO";
            }
            Level level = Level.toLevel(configLevel);
            Logger logger = LogManager.getLogger(packageName);
            logger.setLevel(level);
        }catch (Exception e){
            log.error("Set log level error", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.success(), HttpStatus.OK);
    }




}
