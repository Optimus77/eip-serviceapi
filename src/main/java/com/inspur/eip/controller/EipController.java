package com.inspur.eip.controller;

import com.inspur.eip.entity.EipDelParam;
import com.inspur.eip.entity.LogLevel;
import com.inspur.eip.service.EipAtomService;
import com.inspur.eip.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
//@RestController
//@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
//@Api(value = "/v1", description = "eip API")
//@Validated
public class EipController {

    @Autowired
    private EipAtomService eipAtomService;

    @Value("${proxy.target_url}")
    private String eipAtomUrl;

    @GetMapping(value = "/servers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity getServerList(@RequestParam String region, @RequestParam String tag) {
        log.info("get server api called");

        String  uri =eipAtomUrl+ "/eip/v1/servers?region="+region+"&tag="+tag;

        try {
            ReturnResult response = HttpUtil.get(uri, null);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
        }catch (Exception e){
            log.error("Get eip by id exception", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(HsConstants.TIME_OUT, "Time out when request."),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }



    /**
     * get number of user
     * @return response
     */
    @GetMapping(value = "/eipnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getEipCount() {
        String  uri =eipAtomUrl+ "/eip/v1/eipnumbers";
        ReturnResult response ;
        try {
            response = HttpUtil.get(uri, null);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
        }catch (Exception e){
            log.error("Get eip by id exception", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(HsConstants.TIME_OUT, "Time out when request."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping(value = "/deleiplist", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "deleiplist")
    public ResponseEntity deleteEipList(@RequestBody EipDelParam param) {

        String  uri =eipAtomUrl+ "/eip/v1/deleiplist";
        ReturnResult response ;
        try {
            response = HttpUtil.post(uri, null, param.toString());
            return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
        }catch (Exception e){
            log.error("Get eip by id exception", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(HsConstants.TIME_OUT, "Time out when request."),
                HttpStatus.INTERNAL_SERVER_ERROR);
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
        log.info("Set debug level to:{}", requestBody);


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



  }
