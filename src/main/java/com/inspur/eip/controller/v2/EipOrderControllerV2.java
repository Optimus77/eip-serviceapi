package com.inspur.eip.controller.v2;


import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.OrderSoftDown;
import com.inspur.eip.entity.ReciveOrder;
import com.inspur.eip.service.V2.BssApiServicev2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value= VersionConstant.REST_VERSION_1_1, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1.1", description = "eip API")
@Validated
public class EipOrderControllerV2 {

    @Autowired
    private BssApiServicev2 bssApiService;

    @PostMapping(value = "/orders")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity createOrders(@RequestBody ReciveOrder reciveOrder) {

        log.info("——————get create eip order:{}.", reciveOrder.toString());
        return bssApiService.onReciveCreateOrderResult(reciveOrder);

    }


    @PostMapping(value = "/orders/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity updateOrder(@RequestBody ReciveOrder reciveOrder, @PathVariable("id") String eipId) {

        log.info("——————get update eip order:{}.", reciveOrder.toString());
        return bssApiService.onReciveUpdateOrder(eipId, reciveOrder);

    }


    @PostMapping(value = "/orders/softdown")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity softDown(@RequestBody OrderSoftDown eipReciveOrder) {

        log.info("——————get soft down eip order:{}.", eipReciveOrder.toString());
        return bssApiService.onReciveSoftDownOrder(eipReciveOrder);

    }


    @DeleteMapping(value = "/orders/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity deleteOrder(@RequestBody ReciveOrder reciveOrder, @PathVariable("id") String eipId) {

        log.info("——————get delete eip order:{}.", reciveOrder.toString());
        return bssApiService.onReciveDeleteOrderResult(reciveOrder,eipId);

    }
}
