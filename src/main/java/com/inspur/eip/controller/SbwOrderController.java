package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.OrderSoftDown;
import com.inspur.eip.entity.ReciveOrder;
import com.inspur.eip.service.BssApiService;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
@Api(value = "/v1", description = "sbw API")
@Validated
public class SbwOrderController {

    @Autowired
    private BssApiService bssApiService;

    @PostMapping(value = "/sbws")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public JSONObject allocateSbw(@Valid @RequestBody ReciveOrder sharedBandWidthRecive) {
        log.info("——————get create Sbw order: {}.",sharedBandWidthRecive.toString());
        return  bssApiService.createShareBandWidth(sharedBandWidthRecive);

    }

    @DeleteMapping(value = "/sbws/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public JSONObject deleteSbw(@Valid @RequestBody ReciveOrder sbwRecive,
                                    @PathVariable("id") String id,
                                    BindingResult result) {
        log.info("——————delete sbw api called——————");
        return bssApiService.deleteShareBandWidth(sbwRecive);
    }



    @ICPControllerLog
    @PostMapping(value = "/sbws/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public JSONObject updateSbw(@Valid @RequestBody ReciveOrder sbwRecive,
                                    @PathVariable("id") String sbwId,
                                    BindingResult result) {
        log.info("——————delete sbw api called——————");
        return bssApiService.updateSbwConfig(sbwId, sbwRecive);
    }


    /*@PostMapping(value = "/sbws/softdown")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public JSONObject softDown(@RequestBody OrderSoftDown eipReciveOrder) {

        log.info("——————get soft down sbw order:{}.", eipReciveOrder.toString());
        return bssApiService.onReciveSoftDownOrder(eipReciveOrder);

    }*/
}
