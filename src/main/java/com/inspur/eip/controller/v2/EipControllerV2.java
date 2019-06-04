package com.inspur.eip.controller.v2;

import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.v2.LogLevel;
import com.inspur.eip.entity.v2.eip.*;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.v2.ReturnMsgUtil;
import com.inspur.eip.util.ReturnStatus;
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
@RequestMapping(value= ConstantClassField.VERSION_REST, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1", description = "eip API")
@Validated
public class EipControllerV2 {

    @Autowired
    private EipServiceImpl eipService;

    private String authScret = "kitlv7i2";

    @PostMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity atomAllocateEip(@Valid @RequestBody EipAllocateParamWrapper eipConfig, BindingResult result) {
        log.info("Allocate a eip:{}.", eipConfig.getEipAllocateParam().toString());
        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
                    HttpStatus.BAD_REQUEST);
        }
        return eipService.atomCreateEip(eipConfig.getEipAllocateParam());
    }

    @DeleteMapping(value = "/eips/{eip_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity atomDeleteEip(@Size(min=36, max=36, message = "Must be uuid.")
                                        @PathVariable("eip_id") String eipId) {
        //Check the parameters
        log.info("Atom delete the Eip:{} ",eipId);
        return eipService.atomDeleteEip(eipId);

    }

    @GetMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listeip",notes="list")
    public ResponseEntity listEip(@RequestParam(required = false) String currentPage ,
                                  @RequestParam(required = false )String limit,
                                  @RequestParam(required = false )String status,
                                  @RequestParam(required = false )String bandWidth) {
        log.debug("EipController listEip, currentPage:{}, limit:{}", currentPage, limit);
        if(StringUtils.isBlank(currentPage) ||StringUtils.isBlank(limit)){
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
        if(StringUtils.isNotBlank(bandWidth)){
            return  eipService.listEipsByBandWidth(status);
        }
        return  eipService.listEips(Integer.parseInt(currentPage),Integer.parseInt(limit),status);
    }

    /**
     * get eip instance detail
     * @param eipId  the id of eip
     * @return  retrun
     */
    @GetMapping(value = "/eips/{eip_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "get detail of  eip instance", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
    })
    public ResponseEntity getEipDetail(@PathVariable("eip_id") String eipId){
        return eipService.getEipDetail(eipId);
    }



    @GetMapping(value = "/eips/search")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="getEipByInstanceId",notes="get")
    public ResponseEntity getEipByInstanceId(@RequestParam(required = false) String resourceid,
                                             @RequestParam(required = false) String eipaddress,
                                             @RequestParam(required = false) String key) {
            if ((StringUtils.isBlank(resourceid)) && (StringUtils.isBlank(eipaddress))) {
                return new ResponseEntity<>("not found.", HttpStatus.NOT_FOUND);
            }
            if ((StringUtils.isNotBlank(resourceid)) && (StringUtils.isNotBlank(eipaddress))) {
                return new ResponseEntity<>("To be wrong.", HttpStatus.FORBIDDEN);
            }
            if (StringUtils.isNotBlank(resourceid)) {
                log.debug("EipController get eip by instance id:{} ", resourceid);
                return eipService.getEipByInstanceId(resourceid);
            } else if (StringUtils.isNotBlank(eipaddress)) {
                log.debug("EipController get eip by ip:{} ", eipaddress);
                if (null != key) {
                    if (key.equals("1709d6c6")) {
                        return eipService.getEipDetailsByIpAddress(eipaddress);
                    }
                }
                return eipService.getEipByIpAddress(eipaddress);
            }
        return new ResponseEntity<>("not found.", HttpStatus.NOT_FOUND);
    }


    @PutMapping(value = "/eips/{eip_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eip", notes = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
    })
    public ResponseEntity updateEip(@PathVariable("eip_id") String eipId, @Valid @RequestBody EipUpdateParamWrapper param , BindingResult result) {

        if (result.hasErrors()) {
            StringBuffer msgBuffer = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            log.info("{}",msgBuffer);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()), HttpStatus.BAD_REQUEST);
        }
        String msg="";
        EipUpdateParam updateParam = param.getEip();

        if (updateParam.getServerId() != null){
            //may be unbind oprate or bind oprate,use this param ,chargetype and bindwidth do nothing
            if (updateParam.getServerId().trim().equals("")){
                log.info("unbind operate, eipid:{}, param:{} ", eipId, updateParam);
                return eipService.eipUnbindWithInstacnce(eipId, null);
            } else {
                log.info("bind operate, eipid:{}, param:{}", eipId, updateParam);
                if (updateParam.getType() != null) {
                    return eipService.eipBindWithInstance(eipId, updateParam.getType(), updateParam.getServerId(),
                            updateParam.getPortId(), updateParam.getPrivateIp());
                } else {
                    msg = "need param serverid and type";
                }
            }
        } else {
            if(updateParam.getBillType()==null&&updateParam.getBandwidth()==0) {
                log.info("unbind operate, eipid:{}, param:{} ", eipId, param.getEip());
                return eipService.eipUnbindWithInstacnce(eipId, null);
            }else if (updateParam.getBandwidth() != 0 && updateParam.getBillType() != null) {
                if (updateParam.getSbwId() != null) {
                    if (updateParam.getChargemode().equalsIgnoreCase("SharedBandwidth")) {
                        log.info("add eip to shared bandWidth:{}", updateParam.toString());
                        return eipService.addEipToSbw(eipId, updateParam);
                    } else if (updateParam.getChargemode().equalsIgnoreCase("Bandwidth")) {
                        log.info("remove eip from shared bandWidth:{}", updateParam.toString());
                        return eipService.removeEipFromSbw(eipId, updateParam);
                    }
                }

               boolean chargeTypeFlag = false;
                if (updateParam.getBillType().equals(HsConstants.MONTHLY) ||
                        updateParam.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                    chargeTypeFlag = true;
                } else {
                    msg = "chargetype must be [monthly |hourlySettlement]";
                }
                if (chargeTypeFlag) {
                    log.info("update bandWidth, eipid:{}, param:{} ", eipId, updateParam);
                    return eipService.updateEipBandWidth(eipId, updateParam);
                }
            } else {
                msg = "param not correct. " +
                        "to bind server,body param like{\"eip\" : {\"prot_id\":\"xxx\",\"serverid\":\"xxxxxx\",\"type\":\"[1|2|3]\"}" +
                        "to unbind server , param like {\"eip\" : {\"prot_id\":\"\"} }or   {\"eip\" : {} }" +
                        "to change bindwidht,body param like {\"eip\" : {\"bandWidth\":xxx,\"billType\":\"xxxxxx\"}" +
                        "";
            }
       }

        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msg), HttpStatus.BAD_REQUEST);

    }

    /**
     * get number of user
     * @return response
     */
    @GetMapping(value = "/eipnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getEipCount(@RequestParam(required = false )String DimensionName,
                                        @RequestParam(required = false )String status) {
        if(StringUtils.isBlank(DimensionName)){
            return  eipService.getEipCount();
        }else {
            if(StringUtils.isBlank(status)){
                if(DimensionName.equals("freeeipnumbers")){
                    return  eipService.getFreeEipCount();
                }else if(DimensionName.equals("totaleipnumbers")){
                    return  eipService.getTotalEipCount();
                }
                return  eipService.getUsingEipCount();
            }else{
                return eipService.getUsingEipCountByStatus(status);
            }
        }

    }

    @GetMapping(value = "/statistics")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity getEipStatistics() {
        return  eipService.getEipStatistics();
    }

    @PostMapping(value = "/eips/{eip_id}/renew")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity renewEip(@PathVariable("eip_id") String eipId,
                                   @RequestBody EipUpdateParam param ) {
        log.info("Renew a eip:{}, order:{}.", eipId, param.toString());
        return eipService.renewEip(eipId, param);
    }

    @PostMapping(value = "/deleiplist", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "deleiplist")
    public ResponseEntity deleteEipList(@RequestBody EipDelParam param) {
        //Check the parameters

        log.info("Delete the Eips:{}.", param.getEipids().toString());
        return eipService.deleteEipList(param.getEipids());
    }

    @GetMapping(value = "/health-status")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "health check")
    public ResponseEntity EipHealthCheck() {
        //HealthCheck
        String code;
        String msg;
        code = ReturnStatus.SC_OK;
        msg ="The eip is running";
        log.info(msg);

        return new ResponseEntity<>(ReturnMsgUtil.msg(code, msg, null), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*",maxAge = 3000)
    @PostMapping(value = "/eips/bind/slb/{eip_id}/{slb_id}/{ip_addr}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "eipBindWithSlb", notes = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
    })
    public ResponseEntity eipBindWithSlb(@PathVariable("slb_id") String slbId
            , @PathVariable("eip_id") String eipId
            ,@PathVariable("ip_addr") String ipAddr) {
        log.info("Bind eip.{}, {}, {}", slbId,ipAddr,eipId);
        return eipService.eipBindWithInstance(eipId, "3", slbId, null, ipAddr);
    }

    @CrossOrigin(origins = "*",maxAge = 3000)
    @PostMapping(value = "/eips/unbind/slb/{slb_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "eipBindWithSlb", notes = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
    })
    public ResponseEntity eipUnbinWithSlb(@PathVariable("slb_id") String slbId) {
        log.info("unBind eip.{}, {}, {}", slbId);
        return eipService.eipUnbindWithInstacnce(null, slbId);
    }

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
