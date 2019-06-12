package com.inspur.eip.controller;

import com.inspur.eip.entity.*;
import com.inspur.eip.entity.eip.EipAllocateParamWrapper;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
@Api(value = "/v1", description = "eip API")
@Validated
public class EipController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EipServiceImpl eipService;


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
        return eipService.atomCreateEip(eipConfig.getEipAllocateParam(), CommonUtil.getKeycloackToken());
    }


    @DeleteMapping(value = "/eips/{eip_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity atomDeleteEip(@Size(min=36, max=36, message = "Must be uuid.")
                                        @PathVariable("eip_id") String eipId) {
        //Check the parameters
        log.info("Atom delete the Eip:{} ",eipId);
        return eipService.atomDeleteEip(eipId);

    }

    @GetMapping(value = "/health-status")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "health check")
    public ResponseEntity EipHealthCheck() {

        return new ResponseEntity<>("Eip is running", HttpStatus.OK);
    }


    @CrossOrigin(origins = "*",maxAge = 3000)
    @PostMapping(value = "/loggers/{package}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setDebugLevel(@PathVariable("package") String packageName, @RequestBody LogLevel requestBody) {
        log.info("————Set debug level to:{}", requestBody);


        String debugLevel = requestBody.getLevel();

        try{
            Level level = Level.toLevel(debugLevel);
            Logger logger = LogManager.getLogger(packageName);
            logger.setLevel(level);
        }catch (Exception e){
            log.error("Set log level error", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.success(), HttpStatus.OK);
    }


    /**
     * get number of user
     * @return response
     */
    @GetMapping(value = "/eipnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getEipCount(@RequestParam(required = false )String status) {
        if(status == null){
            return  eipService.getEipCount();
        }else {
            return eipService.getUsingEipCountByStatus(status);
        }
    }

    @PostMapping(value = "/deleiplist", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "deleiplist")
    public ResponseEntity deleteEipList(@RequestBody EipDelParam param) {

        log.info("Delete the Eips:{}.", param.getEipids().toString());
        return eipService.deleteEipList(param.getEipids());
    }


    @GetMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listeip",notes="list")
    public ResponseEntity listEip(@RequestParam(required = false) String currentPage ,
                                  @RequestParam(required = false )String limit,
                                  @RequestParam(required = false )String status,
                                  @RequestParam(required = false )String bandWidth) {

        log.debug("EipController listEip, currentPage:{}, limit:{}", currentPage, limit);
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
        if(null !=bandWidth){
            return  eipService.listEipsByBandWidth(status);
        }
        return  eipService.listEips(Integer.parseInt(currentPage),Integer.parseInt(limit),status);
    }


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
                                             @RequestParam(required = false) String key)  {
        if((null == resourceid) && (null == eipaddress) ){
            return new ResponseEntity<>("not found.", HttpStatus.NOT_FOUND);
        }
        if((null != resourceid) && (null != eipaddress) ){
            return new ResponseEntity<>("To be wrong.", HttpStatus.FORBIDDEN);
        }
        if(null != resourceid) {
            log.debug("EipController get eip by instance id:{} ", resourceid);
            return eipService.getEipByInstanceId(resourceid);
        } else if(null != eipaddress) {
            log.debug("EipController get eip by ip:{} ", eipaddress);
            if(null != key){
                if (key.equals("1709d6c6")) {
                    return eipService.getEipDetailsByIpAddress(eipaddress);
                }
            }
            return eipService.getEipByIpAddress(eipaddress);
        }
        return new ResponseEntity<>("not found.", HttpStatus.NOT_FOUND);
    }



    /**
     * get number of user
     * @return response
     */
    @GetMapping(value = "/statistics")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity getFreeEipCount() {
        return  eipService.getEipStatistics();
    }


    @PutMapping(value = "/eips/{eip_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eip", notes = "put")
    public ResponseEntity updateEip(@PathVariable("eip_id") String eipId,
                                    @Valid @RequestBody (required = false)  EipUpdateParamWrapper param ,BindingResult result) {

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
            }else {

                msg = "param not correct. " +
                        "to bind server,body param like{\"eip\" : {\"prot_id\":\"xxx\",\"serverid\":\"xxxxxx\",\"type\":\"[1|2|3]\"}" +
                        "to unbind server , param like {\"eip\" : {\"prot_id\":\"\"} }or   {\"eip\" : {} }" +
                        "to change bindwidht,body param like {\"eip\" : {\"bandWidth\":xxx,\"billType\":\"xxxxxx\"}" +
                        "";
            }
        }

        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msg), HttpStatus.BAD_REQUEST);

    }

}
