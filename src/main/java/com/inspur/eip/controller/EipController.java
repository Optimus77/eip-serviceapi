package com.inspur.eip.controller;


import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipAllocateParamWrapper;
import com.inspur.eip.entity.EipReciveOrder;
import com.inspur.eip.entity.EipSoftDownOrder;
import com.inspur.eip.service.BssApiService;
import com.inspur.eip.service.EipServiceImpl;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;


@RestController
@RequestMapping(value = "/v1",produces = {"application/json;charset=UTF-8"})
@Api(value = "/v1",description = "eip Service Api")
@Validated
public class EipController {
    private final static Logger log = LoggerFactory.getLogger(EipController.class);
    @Autowired
    private EipServiceImpl eipService;
    private BssApiService bssApiService;

    @PostMapping(value = "/eips")
    public String createOrder(@Valid @RequestBody EipAllocateParamWrapper eipAllocateJson) {
        log.info("get a eip:{}. ",eipAllocateJson);

        return eipService.createOrder(eipAllocateJson.getEip().toString());
    }
    @DeleteMapping(value = "/eips/{eip_id}")
    public String deleteEipOrder(@Size (min = 36,max = 36,message = "Must be uuid")
                                @PathVariable String eipId) {
        log.info("openApi delete,eipId:{}. ",eipId);
        return eipService.deleteEipOrder(eipId);
    }
    @PostMapping(value = "/orders")
    public JSONObject onReciveCreateOrderResult(@RequestBody EipReciveOrder eipOrder) {
        log.info("get create eip order:{}.", eipOrder);
        return bssApiService.onReciveCreateOrderResult(eipOrder);
    }
    @DeleteMapping(value = "/orders/{id}")
    public JSONObject onReciveDeleteOrderResult(@RequestBody EipReciveOrder eipOrder) {
        log.info("get delete eip order:{}.", eipOrder);
        return bssApiService.onReciveDeleteOrderResult(eipOrder);
    }
    @PostMapping(value = "/orders/{id}")
    public JSONObject onReciveUpdateOrder(@RequestBody EipReciveOrder eipOrder,
                                          @PathVariable String eipId){
        log.info("get delete eip order:{}.", eipOrder,eipId);
        return bssApiService.onReciveUpdateOrder(eipId, eipOrder);
    }
    @PostMapping(value = "/orders/softdown")
    public JSONObject onReciveSoftDownOrder(@RequestBody EipSoftDownOrder eipOrder){

        log.info("get softdown eip order:{}.", eipOrder);
        return bssApiService.onReciveSoftDownOrder(eipOrder);
    }

    @PostMapping(value = "/tests")
    public String createOrderTest(@Valid @RequestBody EipAllocateParamWrapper eipAllocateJson) {
        log.info("create eip test:{}. ",eipAllocateJson);

        return eipService.createOrder(eipAllocateJson.getEip().toString());
    }

}
