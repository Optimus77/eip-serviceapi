package com.inspur.eip.controller;

import com.inspur.eip.entity.Eip;
import com.inspur.eip.service.EipService;
import org.junit.Before;
import org.junit.Test;
import org.openstack4j.model.network.NetFloatingIP;

import static org.junit.Assert.*;

public class EipControllerTest {
    private EipService eipService;
    private String floatingnetworkId = "d9c00a35-fea8-4162-9de1-b8100494a11d";

    @Before
    public void alloc(){
        eipService = new EipService();
    }

    @Test
    public void createeip() {

//        NetFloatingIP floatingIP = eipService.createFloatingIp("region", floatingnetworkId, null);
//        Eip eip = new Eip();
//        eip.setFixedIpv4("2.3.4.5");
//        assertNotEquals(null, floatingIP);
//        System.out.println("the out is:"+ floatingIP);
    }
}