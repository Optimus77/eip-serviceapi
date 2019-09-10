package com.inspur.eip.service;

import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.util.common.CommonUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeFloatingIPService;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.api.networking.NetFloatingIPService;
import org.openstack4j.api.networking.NetworkingService;
import org.openstack4j.api.networking.PortService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Addresses;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.network.Port;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@PrepareForTest({CommonUtil.class,NeutronService.class})
@RunWith(PowerMockRunner.class)
public class NeutronServiceTest {
    @InjectMocks
    private NeutronService neutronService;

    @Mock
    private OSClient.OSClientV3 osClientV3;
    @Mock
    private NetworkingService networkingService;
    @Mock
    private NetFloatingIPService netFloatingIPService;
    @Mock
    private PortService portService;
    @Mock
    private  SlbService slbService;
    @Mock
    private ComputeService computeService;
    @Mock
    private ServerService serverService;
    @Mock
    private ComputeFloatingIPService computeFloatingIPService;

    @Before
    public void setUp() throws KeycloakTokenException {
        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.when(CommonUtil.getOsClientV3Util(Mockito.anyString())).thenReturn(osClientV3);
        PowerMockito.when(CommonUtil.getOsClientV3Util(Mockito.anyString(),Mockito.anyString())).thenReturn(osClientV3);
        PowerMockito.when(CommonUtil.getOsClientV3()).thenReturn(osClientV3);
        Mockito.doReturn(networkingService).when(osClientV3).networking();
        Mockito.doReturn(netFloatingIPService).when(networkingService).floatingip();
        Mockito.doReturn(portService).when(networkingService).port();
        Mockito.doReturn(computeService).when(osClientV3).compute();
        Mockito.doReturn(serverService).when(computeService).servers();
        Mockito.when(CommonUtil.getOSClientProjectId(Mockito.anyString(),Mockito.any())).thenReturn("123");
        Mockito.doReturn(computeFloatingIPService).when(computeService).floatingIps();

    }

    @Test
    public void NetFloatingIP() throws  KeycloakTokenException {
        try {
            List<NetFloatingIP> list= new ArrayList<>();
            Mockito.doReturn(list).when(netFloatingIPService).list(Mockito.anyMap());
            Mockito.doReturn(null).when(portService).get(Mockito.anyString());
            neutronService.createFloatingIp("11","22","33");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void deleteFloatingIp(){
        Boolean result = neutronService.deleteFloatingIp("11","22","33","44");
        Assert.assertEquals(false,result);
    }

    @Test
    public void superDeleteFloatingIp(){
        ActionResponse actionResponse = ActionResponse.actionSuccess() ;
        Mockito.doReturn(actionResponse).when(netFloatingIPService).delete(Mockito.anyString());
        Boolean result = neutronService.superDeleteFloatingIp("11","22");
        Assert.assertEquals(true,result);
    }

    @Test
    public void createAndAssociateWithFip() throws KeycloakTokenException {
        Eip eip = new Eip();
        NetFloatingIP result = neutronService.createAndAssociateWithFip("11","22","",eip ,"55");
        Assert.assertEquals(null,result);
    }

    @Test
    public void createAndAssociateWithFipCase2() throws KeycloakTokenException {
        Eip eip = new Eip();
        NetFloatingIP result = neutronService.createAndAssociateWithFip("11","22","33",eip ,"55");
        Assert.assertEquals(null,result);
    }
    @Test
    public void createAndAssociateWithFipCase3() throws Exception {
        Eip eip = new Eip();
        Port port = Mockito.mock(Port.class);
        Mockito.doReturn("123").when(port).getTenantId();
        Server server = Mockito.mock(Server.class);
        Mockito.doReturn("123").when(server).getTenantId();
        Mockito.doReturn(server).when(serverService).get(Mockito.anyString());
        Mockito.doReturn(port).when(portService).get(Mockito.anyString());
        Set<IP> set = new HashSet<>();
        IP ip  = Mockito.mock(IP.class);
        Mockito.doReturn("911").when(ip).getIpAddress();
        set.add(ip);
        Mockito.doReturn(set).when(port).getFixedIps();
        NetFloatingIP netFloatingIP = Mockito.mock(NetFloatingIP.class);
        Mockito.doReturn(netFloatingIP).when(netFloatingIPService).create(Mockito.any());
        NetFloatingIP result = neutronService.createAndAssociateWithFip("11","22","33",eip ,"55");
    }

    @Test
    public void associaInstanceWithFloatingIp() throws KeycloakTokenException {
        Eip eip = new Eip();
        eip.setRegion("CN-NORTH3");
        eip.setFloatingIp("34");
        Server server = Mockito.mock(Server.class);
        Addresses addresses = Mockito.mock(Addresses.class);
        Mockito.doReturn(addresses).when(server).getAddresses();
        Map<String,List> map= new HashMap<>();
        List<Address> list = new ArrayList<>();
        Address address = Mockito.mock(Address.class);
        Mockito.doReturn("fixed").when(address).getType();
        Mockito.doReturn("1987").when(address).getAddr();
        list.add(address);
        map.put("123",list);
        Mockito.doReturn(map).when(addresses).getAddresses();
        ActionResponse actionResponse = ActionResponse.actionSuccess();
        Mockito.doReturn(actionResponse).when(computeFloatingIPService).addFloatingIP(Mockito.eq(server),Mockito.anyString());
        Mockito.doReturn(server).when(serverService).get(Mockito.anyString());
        ActionResponse result = neutronService.associaInstanceWithFloatingIp(eip,"12","34");
        Assert.assertEquals(200,result.getCode());
    }

//    @Test
//    public void disassociateInstanceWithFloatingIp() throws KeycloakTokenException {
//        Mockito.doReturn(true).when(slbService).isFipInUse(Mockito.anyString());
//        ActionResponse actionResponse = neutronService.disassociateInstanceWithFloatingIp("11","22","cn-north3");
//    }
}
