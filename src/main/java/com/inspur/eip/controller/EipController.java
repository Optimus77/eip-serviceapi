package com.inspur.eip.controller;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.service.EipService;
import com.inspur.eip.util.FastjsonUtil;
import io.swagger.annotations.ApiImplicitParam;
import org.openstack4j.model.network.NetFloatingIP;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(value= ConstantClassField.VERSION_REST, produces={"application/json;charset=UTF-8"})
@Api(value = "eip", description = "eip API")
public class EipController {

    private final static Logger log = Logger.getLogger(EipController.class.getName());
    @Autowired
    private EipService eipService;
    private String floatingnetworkId = "d9c00a35-fea8-4162-9de1-b8100494a11d";


    @PostMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="createEip",notes="create")
    public ResponseEntity<String> createeip(@RequestBody Eip eip) {
        ////Do--dao; MO system;Vo  web
        NetFloatingIP floatingIP = eipService.createFloatingIp("region", floatingnetworkId, null);
        Eip  eipMo = new Eip();

        eipMo.setFloatingIpv4(floatingIP.getFloatingIpAddress());
        eipMo.setFixedIpv4(floatingIP.getFixedIpAddress());
        return new ResponseEntity<>(FastjsonUtil.toJSONString(eipMo),HttpStatus.OK);
    }


    @GetMapping(value = "/eips")
    @ApiOperation(value="listeip",notes="list")
    public ResponseEntity<String> listEip(String maker,String limit) {
        return new ResponseEntity<>(maker,HttpStatus.OK);
    }


    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.PUT)
    public String updateEip(@RequestParam String id) {
        return " " + id;
    }
    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEip(@RequestParam String id) {
        Boolean result = eipService.deleteFloatingIp("name", id);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }



    @GetMapping(value = "/eips/{eip_id}")
    @ApiOperation(value="get detail of  eip instance", notes="")
    @ApiImplicitParam(paramType="path", name = "eip_id", value = "the id of eip", required = true, dataType = "String")
    public ResponseEntity getEipDetail(@PathVariable("eip_id") String eip_id){

        try {
            Optional<Eip> eip = eipService.getEipDetail(eip_id);
            if (eip.isPresent()) {
                Eip eipEntity=eip.get();

                JSONObject js =new JSONObject();
                js.put("eip_id",eipEntity.getId());
                js.put("status","");//
                js.put("iptype",eipEntity.getLinkType());//
                js.put("eip_address",eipEntity.getEipIpv4());//
                js.put("private_ip_address",eipEntity.getFloatingIpv4());//
                js.put("bandwidth",Integer.parseInt(eipEntity.getBanWidth()));//
                JSONObject resourceset=new JSONObject();
                resourceset.put("resourcetype",eipEntity.getInstanceType());
                resourceset.put("resource_id",eipEntity.getInstanceId());
                js.put("resourceset",resourceset);
                js.put("chargetype","计费的逻辑？？？？定义的接口有问题？？？");
                js.put("chargemode","");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                js.put("create at", formatter.format(eipEntity.getCreateTime()));
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