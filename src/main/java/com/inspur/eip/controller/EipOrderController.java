package com.inspur.eip.controller;


import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipReciveOrder;
import com.inspur.eip.entity.EipSoftDownOrder;
import com.inspur.eip.service.BssApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
//
@Slf4j
//@RestController
//@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
//@Api(value = "/v1", description = "eip API")
//@Validated
public class EipOrderController {

    @Autowired
    private BssApiService bssApiService;

    @PostMapping(value = "/orders")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity createOrders(@RequestBody EipReciveOrder eipReciveOrder) {

        log.info("get create eip order:{}.", eipReciveOrder.toString());
        JSONObject result = bssApiService.onReciveCreateOrderResult(eipReciveOrder);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }


    @PostMapping(value = "/orders/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity updateOrder(@RequestBody EipReciveOrder eipReciveOrder,  @PathVariable("id") String eipId) {

        log.info("get update eip order:{}.", eipReciveOrder.toString());
        JSONObject result = bssApiService.onReciveUpdateOrder(eipId,eipReciveOrder);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }


    @PostMapping(value = "/orders/softdown")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity softDown(@RequestBody EipSoftDownOrder eipReciveOrder) {

        log.info("get soft down eip order:{}.", eipReciveOrder.toString());
        JSONObject result = bssApiService.onReciveSoftDownOrder(eipReciveOrder);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }


    @DeleteMapping(value = "/orders/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity deleteOrder(@RequestBody EipReciveOrder eipReciveOrder,  @PathVariable("id") String eipId) {

        log.info("get delete eip order:{}.", eipReciveOrder.toString());
        JSONObject result = bssApiService.onReciveDeleteOrderResult(eipReciveOrder);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}