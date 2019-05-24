package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.ReciveOrder;
import com.inspur.eip.service.BssApiService;
import com.inspur.eip.service.SbwAtomService;
import com.inspur.eip.util.*;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
//@RestController
//@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
//@Api(value = "/v1", description = "sbw API")
//@Validated
public class SbwController {

    @Autowired
    private BssApiService bssApiService;
    @Autowired
    private SbwAtomService sbwAtomService;

    @Value("${proxy.target_url}")
    private String sbwAtomUrl;

    @PostMapping(value = "/sbws")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity allocateSbw(@Valid @RequestBody ReciveOrder sharedBandWidthRecive, BindingResult result) {
        log.info("get server api called");
        JSONObject ret =  bssApiService.createShareBandWidth(sharedBandWidthRecive);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }


    @DeleteMapping(value = "/sbws/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity deleteSbw(@Valid @RequestBody ReciveOrder sbwRecive,
                                          @PathVariable("id") String id,
                                          BindingResult result) {
        log.info("delete sbw api called");
        JSONObject ret = bssApiService.deleteShareBandWidth(sbwRecive);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ICPControllerLog
    @PostMapping(value = "/sbws/{id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity updateSbw(@Valid @RequestBody ReciveOrder sbwRecive,
                                    @PathVariable("id") String sbwId,
                                    BindingResult result) {
        log.info("delete sbw api called");
        JSONObject ret = bssApiService.updateSbwConfig(sbwId, sbwRecive);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping(value = "/sbws")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listsbw",notes="list")
    public ResponseEntity listSbw(@RequestParam(required = false) String pageIndex ,
                                  @RequestParam(required = false )String pageSize,
                                  @RequestParam(required = false )String searchValue) {
        log.info("SbwController listSbw, currentPage:{}, limit:{}", pageIndex, pageSize);
        String url=sbwAtomUrl + "/eip/v1/sbws?"+"pageIndex="+pageIndex+"&pageSize="+pageSize+"&searchValue="+searchValue;
        ReturnResult response ;
        try {
            log.info("Send order to url:{}", url);
            response = HttpUtil.get(url, null);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
        }catch (Exception e){
            log.error("list sbw exception", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(HsConstants.TIME_OUT, "Time out when request."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping(value = "/sbws/search")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="getSbwByProjectId",notes="get")
    public ResponseEntity getSbwByProjectId(@RequestParam(required = false) String projectId) {
        String url=sbwAtomUrl + "/eip/v1/sbws?"+"projectId="+projectId;
        ReturnResult response ;
        try {
            log.info("Send order to url:{}", url);
            response = HttpUtil.get(url, null);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
        }catch (Exception e){
            log.error("list sbw exception", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(HsConstants.TIME_OUT, "Time out when request."),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }


    /**
     * get sbw instance detail
     * @param sbwId  the id of sbw
     * @return  retrun
     */
    @GetMapping(value = "/sbws/{sbw_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "get detail of  sbw instance", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity getSbwDetail(@PathVariable("sbw_id") String sbwId){
        log.info("get sbw detail api called");
        String url=sbwAtomUrl + "/eip/v1/sbws/"+sbwId;
        ReturnResult response ;
        try {
            log.info("Send order to url:{}", url);
            response = HttpUtil.get(url, null);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
        }catch (Exception e){
            log.error("list sbw exception", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(HsConstants.TIME_OUT, "Time out when request."),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * get sbw number of user
     * @return response
     */
    @GetMapping(value = "/sbwnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getSbwCount() {

        String url=sbwAtomUrl + "/eip/v1/sbwnumbers";
        ReturnResult response ;
        try {
            log.info("Send order to url:{}", url);
            response = HttpUtil.get(url, null);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.OK);
        }catch (Exception e){
            log.error("list sbw exception", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(HsConstants.TIME_OUT, "Time out when request."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
