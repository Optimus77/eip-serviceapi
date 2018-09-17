package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.entity.EipPool;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.service.EipService;
import com.inspur.eip.service.FirewallService;
import com.inspur.eip.util.FastjsonUtil;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import io.swagger.annotations.*;
import org.openstack4j.model.network.NetFloatingIP;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Optional;
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

    @Autowired
    private FirewallService firewallService;

    @Autowired
    private EipRepository eipRepository;
    //Todo: find the external net id
    private String floatingnetworkId = "d9c00a35-fea8-4162-9de1-b8100494a11d";


    @PostMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="createEip",notes="create")
    public ResponseEntity<String> createEip(@RequestBody Eip eipConfig) {
        ////Do--dao; MO system;Vo  web
        EipPool eip = eipService.allocateEip("region", "network_id");
        if(null != eip) {
            NetFloatingIP floatingIP = eipService.createFloatingIp("region", floatingnetworkId, null);
            Eip eipMo = new Eip();

            eipMo.setFloatingIpv4(floatingIP.getFloatingIpAddress());
            eipMo.setFixedIpv4(floatingIP.getFixedIpAddress());
            eipMo.setEipIpv4(eip.getId());
            eipMo.setDevId(eip.getDevId());
            eipMo.setId(floatingIP.getId());
            eipMo.setBanWidth(eipConfig.getBanWidth());
            eipMo.setName(eipConfig.getName());
            eipMo.setVpcId(eipConfig.getVpcId());
            eipRepository.save(eipMo);
            return new ResponseEntity<>(FastjsonUtil.toJSONString(eipMo), HttpStatus.OK);
        }
        return new ResponseEntity<>(FastjsonUtil.toJSONString(eipConfig), HttpStatus.EXPECTATION_FAILED);
    }
    @PutMapping(value = "/ports")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="associatePortWithEip",notes="associate")
    public ResponseEntity<String> associatePortWithEip(String portId, String floatingIpId) {
        Eip eip = eipRepository.getOne(floatingIpId);
        if(eipService.associatePortWithFloatingIp("region", floatingIpId, portId)){
            String dnatRuleId = firewallService.addDnat(eip.getFloatingIpv4(), eip.getEipIpv4(), "dev_id");
            String snatRuleId = firewallService.addSnat(eip.getFloatingIpv4(), eip.getEipIpv4(), "dev_id");
            String pipId = firewallService.addQos(eip.getFloatingIpv4(), "get_eip", eip.getBanWidth(), "dev_id");
            eip.setInstanceId(portId);
            eip.setInstanceType("VM_type"); //Todo: define type
            eip.setDnatId(dnatRuleId);
            eip.setSnatId(snatRuleId);
            eip.setPipId(pipId);
            eip.setStat("1");
            eipRepository.save(eip);
            return new ResponseEntity<>(FastjsonUtil.toJSONString(eip),HttpStatus.OK);
        }
        return new ResponseEntity<>(FastjsonUtil.toJSONString(eip),HttpStatus.EXPECTATION_FAILED);
    }

    @PutMapping(value = "/ports")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="disassociatePortWithEip",notes="disassociate")
    public ResponseEntity<String> disassociatePortWithEip(String floatingIpId) {
        Eip eip = eipRepository.getOne(floatingIpId);
        if(eipService.disassociateFloatingIpFromPort("region", floatingIpId)){
            Boolean result1 = firewallService.delDnat(eip.getDnatId(), eip.getDevId());
            Boolean result2 = firewallService.delSnat(eip.getDnatId(), eip.getDevId());
            if(result1 && result2) {
                if(firewallService.delQos(eip.getPipId(), eip.getDevId())){
                    eip.setInstanceId(null);
                    eip.setInstanceType("VM_type"); //Todo: define type
                    eip.setDnatId(null);
                    eip.setSnatId(null);
                    eip.setPipId(null);
                    eip.setStat("0");
                    eipRepository.save(eip);
                    return new ResponseEntity<>(FastjsonUtil.toJSONString(eip), HttpStatus.OK);
                }

            }
        }
        return new ResponseEntity<>(FastjsonUtil.toJSONString(eip),HttpStatus.EXPECTATION_FAILED);
    }

    @GetMapping(value = "/eips")
    @ApiOperation(value="listeip",notes="list")
    public ResponseEntity<String> listEip(String maker,String limit) {
        List<? extends NetFloatingIP> floatingIpList = eipService.listFloatingIps();
        List<String> floatingips = new ArrayList<>();
        for(NetFloatingIP floatingip : floatingIpList){
            floatingips.add(floatingip.getFloatingIpAddress());
        }

        return new ResponseEntity<>(floatingips.toString(),HttpStatus.OK);
    }



    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEip(@RequestParam String id) {
        Boolean result = eipService.deleteFloatingIp("name", id);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /**
     * get eip instance detail
     * @param eip_id the id of eip
     * @return
     */
    //@ICPControllerLog
    @GetMapping(value = "/eips/{eip_id}")
    @ApiOperation(value = "get detail of  eip instance", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "header", name = "authorization", value = "the token from the keycolock", required = true, dataType = "String")
    })
    public ResponseEntity getEipDetail(@RequestHeader("authorization")String authorization ,@PathVariable("eip_id") String eip_id){
        String result="{}";
        try {
             log.info("get request head authorization:"+authorization);
             result=eipService.getEipDetail(eip_id);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ResponseEntity(result, HttpStatus.OK);
        }

    }


    @ICPControllerLog
    @PutMapping(value = "/eips/{eip_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update detail of  eip instance", notes = "")
    @Transactional
    public ResponseEntity updateEip(@PathVariable("eip_id") String eip_id, @RequestBody EipUpdateParamWrapper param) {
        try {

        } catch (Exception e) {
            e.printStackTrace();

        }finally{
            return new ResponseEntity("", HttpStatus.OK);
        }

    }

    @ICPControllerLog
    @PutMapping(value = "/eips/{eip_id}/bindwidth", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update the bindWidth of  eip instance", notes = "")
    @Transactional
    public ResponseEntity updateEipBindWidth(@PathVariable("eip_id") String eip_id, @RequestBody EipUpdateParamWrapper param) {

        String result="{}";
        try {
            result=eipService.updateEipBandWidth(eip_id,param);
        } catch (Exception e) {
            e.printStackTrace();

        }finally{
            return new ResponseEntity("", HttpStatus.OK);
        }

    }

    @ICPControllerLog
    @PutMapping(value = "/eips/{eip_id}/floatingip", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "the eip instance", notes = "")
    @Transactional
    public ResponseEntity eipBindFloatingIP(@PathVariable("eip_id") String eip_id, @RequestBody EipUpdateParamWrapper param) {
        try {

        } catch (Exception e) {
            e.printStackTrace();

        }finally{
            return new ResponseEntity("", HttpStatus.OK);
        }

    }

}