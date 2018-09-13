package com.inspur.eip.controller;

import com.inspur.eip.config.ConstantClassField;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.service.EipService;
import com.inspur.eip.service.FirewallService;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value= ConstantClassField.VERSION_REST, produces={"application/json;charset=UTF-8"})
@Api(value = "eip", description = "eip API")
public class EipController {
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


    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.PUT)
    public String updateEip(@RequestParam String id) {
        return " " + id;
    }

    @RequestMapping(value = "/eips/{eip_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEip(@RequestParam String id) {
        Boolean result = eipService.deleteFloatingIp("name", id);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }


}