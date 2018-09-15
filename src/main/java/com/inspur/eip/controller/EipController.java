package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.service.EipService;
import com.inspur.eip.service.FirewallService;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.FastjsonUtil;
import com.inspur.icp.common.util.annotation.ICPControllerLog;
import io.swagger.annotations.*;
import org.openstack4j.model.network.FloatingIP;
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
    public ResponseEntity<String> createEip(@RequestBody Eip eip) {
        ////Do--dao; MO system;Vo  web
        NetFloatingIP floatingIP = eipService.createFloatingIp("region", floatingnetworkId, null);
        String snat_rule_id = firewallService.addSnate(eip.getFloatingIpv4(), "get_eip", "dev_id");
        String pip_id = firewallService.addQos(eip.getFloatingIpv4(), "get_eip", eip.getBanWidth(),"dev_id");
        Eip  eipMo = new Eip();

        eipMo.setFloatingIpv4(floatingIP.getFloatingIpAddress());
        eipMo.setFixedIpv4(floatingIP.getFixedIpAddress());
        eipMo.setEipIpv4("eip_ip_addr");
        eipMo.setId(floatingIP.getId());
        eipMo.setBanWidth(eip.getBanWidth());
        eipMo.setName(eip.getName());
        eipMo.setVpcId(eip.getVpcId());
        eipRepository.save(eipMo);
        return new ResponseEntity<>(FastjsonUtil.toJSONString(eipMo),HttpStatus.OK);
    }
    @PutMapping(value = "/ports")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="associatePortWithEip",notes="associate")
    public ResponseEntity<String> associatePortWithEip(String portId, String floatingIpId) {
        ////Do--dao; MO system;Vo  web
        Boolean result = eipService.associatePortWithFloatingIp("region", floatingIpId, portId);
        Eip  eipMo = new Eip();

        return new ResponseEntity<>(FastjsonUtil.toJSONString(eipMo),HttpStatus.OK);
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