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


    @PutMapping(value = "/eips/{eip_id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="update detail of  eip instance", notes="update")
    @Transactional
    public ResponseEntity updateEip(@PathVariable("eip_id") String eip_id,@RequestBody EipUpdateParamWrapper param) {

        try{
            Optional<Eip> eip = eipService.getEipDetail(eip_id);
            if (eip.isPresent()) {
                Eip eipInstace=eip.get();
                NetFloatingIP bandingFloatIp= eipService.getFloatingIpDetail(eipInstace.getId());//数据表中没有floatingip的id？？？ 咋办？难道要全查出来筛选？？
                if(param.getEipUpdateParam()!=null){
                    //validate the param of bandwidth
                    if(param.getEipUpdateParam().getBandWidth()!=null&&param.getEipUpdateParam().getChargeType()!=null){
                        try {
                            int bandWidth=Integer.parseInt(param.getEipUpdateParam().getBandWidth());
                            String chargetype=param.getEipUpdateParam().getChargeType();
                            eipInstace.setBanWidth(param.getEipUpdateParam().getBandWidth());
                            //eipInstace.set

                        }catch (NumberFormatException e){
                            return new ResponseEntity("bandwidth must is a number",HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    //validate the param of port
                    if(param.getEipUpdateParam().getPortId()!=null){
                            eipService.getFloatingIpDetail("ddddddddddddd");
                            eipInstace.setInstanceId("sssssssssssssxxxxxxxx");
                    }
//                    {
//                        "eip": {
//                                "eipid": "f588ccfa-8750-4d7c-bf5d-2ede24414706",
//                                "status": "PENDING_UPDATE",
//                                "iptype": "5_bgp",
//                                "eip_address": "161.xx.xx.7",
//                                "port_id": "f588ccfa-8750-4d7c-bf5d-2ede24414706",
//                                "bandwidth": 6,
//                                "chargetype": "PostPaid",
//                                "create_at": ""2015-07-16 04:10:52""
//                    }
//                    }

                    eipService.updateEipBandWidth(eipInstace);
                    JSONObject js =new JSONObject();
                    js.put("eip_id",eipInstace.getId());//the id of eip
                    js.put("status",bandingFloatIp.getStatus());//the floating ip status
                    js.put("iptype",eipInstace.getLinkType());//
                    js.put("eip_address",eipInstace.getEipIpv4());//
                    js.put("private_ip_address",eipInstace.getFloatingIpv4());//
                    js.put("bandwidth",Integer.parseInt(eipInstace.getBanWidth()));//
                    js.put("chargetype","计费的逻辑？？？？");
                    js.put("create_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(eipInstace.getCreateTime()));
                    JSONObject returnjs =new JSONObject();
                    returnjs.put("eip",js);
                    log.info(js.toString());



                    return new ResponseEntity("",HttpStatus.INTERNAL_SERVER_ERROR);


                }else{
                    return new ResponseEntity("body param is not correct",HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }else{
                return new ResponseEntity("can not find the instance of this id :"+eip_id,HttpStatus.INTERNAL_SERVER_ERROR);
            }


        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @ICPControllerLog
    @GetMapping(value = "/eips/{eip_id}")
    @ApiOperation(value="get detail of  eip instance", notes="")
    @ApiImplicitParam(paramType="path", name = "eip_id", value = "the id of eip", required = true, dataType = "String")
    public ResponseEntity getEipDetail(@PathVariable("eip_id") String eip_id){

        try {

            Optional<Eip> eip = eipService.getEipDetail(eip_id);
            if (eip.isPresent()) {
                Eip eipEntity=eip.get();
                NetFloatingIP bandingFloatIp= eipService.getFloatingIpDetail(eipEntity.getId());//数据表中没有floatingip的id？？？ 咋办？难道要全查出来筛选？？
                log.info(bandingFloatIp.toString());
//                {
//                    "floatingIpAddress": "10.110.26.4",
//                        "floatingNetworkId": "d9c00a35-fea8-4162-9de1-b8100494a11d",
//                        "id": "7d095f59-03cf-4ee9-8c92-80261fde7f81",
//                        "status": "DOWN",
//                        "tenantId": "65a859f362f749ce95237cbd08c30edf"
//                }

                JSONObject js =new JSONObject();
                js.put("eip_id",eipEntity.getId());//the id of eip
                js.put("status",bandingFloatIp.getStatus());//the floating ip status
                js.put("iptype",eipEntity.getLinkType());//
                js.put("eip_address",eipEntity.getEipIpv4());//
                js.put("private_ip_address",eipEntity.getFloatingIpv4());//
                js.put("bandwidth",Integer.parseInt(eipEntity.getBanWidth()));//
                JSONObject resourceset=new JSONObject();
                resourceset.put("resourcetype",eipEntity.getInstanceType());
                resourceset.put("resource_id",eipEntity.getInstanceId());
                js.put("resourceset",resourceset);
                js.put("chargetype","计费的逻辑？？？？");
                js.put("chargemode","");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                js.put("create_at", formatter.format(eipEntity.getCreateTime()));
                JSONObject returnjs =new JSONObject();
                returnjs.put("eip",js);
                log.info(js.toString());
                return new ResponseEntity<>(returnjs.toString(),HttpStatus.OK);
            }else{
                JSONObject js =new JSONObject();
                js.put("error","can not find instance use this id"+eip_id);
                return new ResponseEntity<>(js.toString(),HttpStatus.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }
}