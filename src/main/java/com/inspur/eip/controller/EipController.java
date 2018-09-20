package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.entity.ReturnMsg;
import com.inspur.eip.service.EipService;
import com.inspur.eip.util.FastjsonUtil;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value= ConstantClassField.VERSION_REST, produces={"application/json;charset=UTF-8"})
@Api(value = "eip", description = "eip API")
public class EipController {

    private final static Logger log = Logger.getLogger(EipController.class.getName());
    @Autowired
    private EipService eipService;

    //Todo: find the external net id
    private String floatingnetworkId = "d9c00a35-fea8-4162-9de1-b8100494a11d";

    @ICPControllerLog
    @PostMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="createEip",notes="create")
    public ResponseEntity<String> createEip(@RequestBody Eip eipConfig) {
        Eip eipMo = eipService.createEip(eipConfig, floatingnetworkId, eipConfig.getInstanceId());
        if(null != eipMo) {
            return new ResponseEntity<>(FastjsonUtil.toJSONString(eipMo), HttpStatus.OK);
        }

        return new ResponseEntity<>("Failed to allocate eip.", HttpStatus.EXPECTATION_FAILED);
    }



    @GetMapping(value = "/eips")
    @ApiOperation(value="listeip",notes="list")
    public ResponseEntity<String> listEip(String maker,String vpcId) {
        List<? extends Eip> eips = eipService.listEips(vpcId);
        List<String> eipresult = new ArrayList<>();
        for(Eip eip : eips){
            eipresult.add(eip.getEip());
        }

        return new ResponseEntity<>(eipresult.toString(),HttpStatus.OK);
    }



    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEip(@RequestParam String id) {
        Boolean result = eipService.deleteEip("name", id);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }


    /**
     * get eip instance detail
     * @param eip_id  the id of eip
     * @param authorization --
     * @param region
     * @return
     */
    @ICPControllerLog
    @GetMapping(value = "/eips/{eip_id}")
    @ApiOperation(value = "get detail of  eip instance", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "authorization", value = "the token from the keycolock", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "region", value = "the region ", required = true, dataType = "String")
    })
    public JSONObject getEipDetail(@PathVariable("eip_id") String eip_id, @RequestHeader("authorization")String authorization , @RequestHeader("region")String region){
        return eipService.getEipDetail(eip_id);
    }



    @ICPControllerLog
    @PostMapping(value = "/eips/{eip_id}/port", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "eipBindWithPort", notes = "")
    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "authorization", value = "the token from the keycolock", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "region", value = "the region ", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "body",   name = "param", value = "the json param ", required = true, dataType = "String")
    })
    public ResponseEntity eipBindWithPort(@PathVariable("eip_id") String eip_id, @RequestBody EipUpdateParamWrapper param,@RequestHeader("authorization")String authorization ,@RequestHeader("region")String region) {

        if(param.getEipUpdateParam().getPortId()!=null){
            String result=eipService.eipbindPort(eip_id,param.getEipUpdateParam().getPortId());
            return new ResponseEntity(result, HttpStatus.OK);
        }else{
            return new ResponseEntity("{error:\"port_id is not null\"}", HttpStatus.OK);
        }

    }

    @ICPControllerLog
    @DeleteMapping(value = "/eips/{eip_id}/port", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "eipUnbinWithPort", notes = "")
    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "authorization", value = "the token from the keycolock", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "region", value = "the region ", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "body",   name = "param", value = "the json ", required = true, dataType = "String")
    })
    public ResponseEntity eipUnbindWithPort(@PathVariable("eip_id") String eip_id, @RequestBody EipUpdateParamWrapper param,@RequestHeader("authorization")String authorization ,@RequestHeader("region")String region) {

        String result=eipService.unBindPort(eip_id);
        return new ResponseEntity(result, HttpStatus.OK);

    }

    @ICPControllerLog
    @PutMapping(value = "/eips/{eip_id}/bindwidth", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update eip bandWidth", notes = "")
    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "authorization", value = "the token from the keycolock", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "region", value = "the region ", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "body",   name = "param", value = "the json ", required = true, dataType = "String")
    })
    public ResponseEntity changeEipBandWidht(@PathVariable("eip_id") String eip_id, @RequestBody EipUpdateParamWrapper param,@RequestHeader("authorization")String authorization ,@RequestHeader("region")String region) {
        try {
            String result=eipService.updateEipBandWidth(eip_id,param);
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{

        }

    }


}