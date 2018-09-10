package com.inspur.eip.controller;

import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.service.EipService;
import com.inspur.eip.util.FastjsonUtil;
import org.openstack4j.model.network.NetFloatingIP;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value= ConstantClassField.VERSION_REST, produces={"application/json;charset=UTF-8"})
@Api(value = "eip", description = "eip API")
public class EipController {
    @Autowired
    private EipService eipService;
    private String floatingnetworkId = null;

    @PostMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="createEip",notes="create")
    public ResponseEntity<String> createeip(@RequestBody Eip eip) {
        ////Do--dao; MO system;Vo  web
        NetFloatingIP floatingIP = eipService.createFloatingIp("region",floatingnetworkId, null, null);
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



    @GetMapping(value = "/eips{eip_id}")
    @ApiOperation(value="createeip",notes="create")
    public ResponseEntity<String> queryeip(@PathVariable("id") String id) {
        return new ResponseEntity<>(id,HttpStatus.OK);
    }

    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.PUT)
    public String updateEip(@RequestParam String id) {
        return " " + id;
    }
    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.DELETE)
    public String deleteEip(@RequestParam String id) {
        return "" + id;
    }


}