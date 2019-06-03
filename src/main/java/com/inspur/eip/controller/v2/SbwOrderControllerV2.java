package com.inspur.eip.controller.v2;

import com.inspur.eip.config.VersionConstant;
import com.inspur.eip.entity.OrderSoftDown;
import com.inspur.eip.entity.ReciveOrder;
import com.inspur.eip.service.V2.BssApiServicev2;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value= VersionConstant.REST_VERSION_1_1, produces={"application/json;charset=UTF-8"})
@Api(value = "/v1.1", description = "sbw API")
@Validated
public class SbwOrderControllerV2 {

    @Autowired
    private BssApiServicev2 bssApiService;

    @PostMapping(value = "/sbws")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity allocateSbw(@Valid @RequestBody ReciveOrder sharedBandWidthRecive) {
        log.info("——————get create Sbw order: {}.",sharedBandWidthRecive.toString());
        return  bssApiService.createShareBandWidth(sharedBandWidthRecive);

    }

    @DeleteMapping(value = "/sbws/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity deleteSbw(@Valid @RequestBody ReciveOrder sbwRecive,
                                    @PathVariable("id") String id,
                                    BindingResult result) {
        log.info("——————delete sbw api called——————");
        return bssApiService.deleteShareBandWidth(sbwRecive);
    }



    @ICPControllerLog
    @PostMapping(value = "/sbws/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity updateSbw(@Valid @RequestBody ReciveOrder sbwRecive,
                                    @PathVariable("id") String sbwId,
                                    BindingResult result) {
        log.info("——————delete sbw api called——————");
        return bssApiService.updateSbwConfig(sbwId, sbwRecive);
    }


    @PostMapping(value = "/sbws/softdown")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity softDown(@RequestBody OrderSoftDown eipReciveOrder) {

        log.info("——————get soft down sbw order:{}.", eipReciveOrder.toString());
        return bssApiService.onReciveSoftDownOrder(eipReciveOrder);

    }
}
