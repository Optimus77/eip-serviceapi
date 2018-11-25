package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.EipAllocateParamWrapper;
import com.inspur.eip.service.EipServiceImpl;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.ReturnStatus;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import com.inspur.icp.common.util.annotation.ICPServiceLog;
import io.swagger.annotations.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;


@RestController
@RequestMapping(value= ConstantClassField.VERSION_REST, produces={"application/json;charset=UTF-8"})
@Api(value = "eip", description = "eip API")
@Validated
@CrossOrigin(origins = "*")
public class EipController {

    private final static Logger log = LoggerFactory.getLogger(EipController.class);

    @Autowired
    private EipServiceImpl eipService;



//    @ICPControllerLog
//    @PostMapping(value = "/eips")
//    @CrossOrigin(origins = "*",maxAge = 3000)
//    public ResponseEntity atomAllocateEip(@Valid @RequestBody EipAllocateParamWrapper eipConfig, BindingResult result) {
//        log.info("Allocate a eip:{}.", eipConfig.getEipAllocateParam().toString());
//        if (result.hasErrors()) {
//            StringBuffer msgBuffer = new StringBuffer();
//            List<FieldError> fieldErrors = result.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                msgBuffer.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
//            }
//            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msgBuffer.toString()),
//                    HttpStatus.BAD_REQUEST);
//        }
//        return eipService.atomCreateEip(eipConfig.getEipAllocateParam());
//    }

//
//    @DeleteMapping(value = "/eips/{eip_id}")
//    @ICPControllerLog
//    @CrossOrigin(origins = "*",maxAge = 3000)
//    public ResponseEntity atomDeleteEip(@Size(min=36, max=36, message = "Must be uuid.")
//                                        @PathVariable("eip_id") String eipId) {
//        //Check the parameters
//        log.info("Atom delete the Eip:{} ",eipId);
//        return eipService.atomDeleteEip(eipId);
//
//    }


    /**
     *  list of the eip
     * @param currentPage  the current page
     * @param limit  element of per page
     * @return       result
     */
    @GetMapping(value = "/eips")
    @ApiOperation(value="listEips",notes="list")
    @ICPServiceLog
    public ResponseEntity listEips(int currentPage, int limit, String status){
        return eipService.listEips(currentPage,limit,status);
    }

    /**
     *  list of the servers
     * @return       result
     */
    @GetMapping(value = "/servers")
    @ApiOperation(value = "show all servers", notes = "get")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity getServerList(@RequestParam String region) {
        return eipService.getServerList(region);
    }

    /**
     *  list of the servers
     * @return       result
     */
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
        return  eipService.getEipDetail(eipId);
    }
}
