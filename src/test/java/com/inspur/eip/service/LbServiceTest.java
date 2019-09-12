package com.inspur.eip.service;
import com.inspur.eip.EipServiceApplicationTests;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.plugin.hillstone.RadwareService;
import com.inspur.plugin.hillstone.RadwareService2Del;
import com.inspur.plugin.hillstone.RadwareService4query;
import com.inspur.plugin.module.entity.AddNat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@PrepareForTest({RadwareService.class, RadwareService4query.class, RadwareService2Del.class})
@RunWith(PowerMockRunner.class)
public class LbServiceTest {

    @InjectMocks
    private LbService lbService;
    @Mock
    private RadwareService service;

    @Before
    public void setUp(){
        ReflectionTestUtils.setField(lbService,"firewallIp","111");
        ReflectionTestUtils.setField(lbService,"firewallPort","112");
        ReflectionTestUtils.setField(lbService,"firewallUser","113");
        ReflectionTestUtils.setField(lbService,"firewallPasswd","114");
        ReflectionTestUtils.setField(lbService,"firewallInterface","115");
        PowerMockito.mockStatic(RadwareService2Del.class);
        PowerMockito.mockStatic(RadwareService4query.class);

    }

    @Test
    public void addDnatInEquipment() throws Exception {
        Eip eip = new Eip();
        eip.setFloatingIp("floatingip");
        eip.setIpType("iptype");
        eip.setEipAddress("eipaddress");
        String  result = lbService.addDnatInEquipment(eip);
        Assert.assertEquals(null,result );
    }

//    @Test
//    public void addSnatInEquipment2() throws Exception {
//        Eip eip = new Eip();
//        eip.setFloatingIp("floatingip");
//        eip.setIpType("iptype");
//        RadwareService radwareService = PowerMockito.mock(RadwareService.class);
//        PowerMockito.whenNew(RadwareService.class).withAnyArguments().thenReturn(radwareService);
//        AddNat addNat = Mockito.mock(AddNat.class);
//        Mockito.when(radwareService.addSnat(Mockito.anyString(),Mockito.anyInt(),Mockito.anyString(),eq(null),Mockito.anyInt(),Mockito.any())).thenReturn(addNat);
//        Mockito.when(addNat.isStatus()).thenReturn(true);
//        lbService.addSnatInEquipment(eip);
//    }

//    @Test
//    public void addSnatInEquipment(){
//        Eip eip = new Eip();
//        eip.setFloatingIp("floatingip");
//        eip.setIpType("iptype");
////        String  result = lbService.addSnatInEquipment(eip);
////        Assert.assertEquals(null,result );
//
////        RadwareService service = new RadwareService();
//        AddNat addNat = new AddNat();
//        addNat.setStatus(false);
//        addNat.setNatFilterIndex(2);
//        Mockito.doReturn(addNat).when(service).addSnat(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.any());
////        Mockito.when(service.addSnat("iptype", 0, "floatingip", null, 1, new BaseObject())).thenReturn(addNat);
//        PowerMockito.mockStatic(RadwareService.class);
////        CreateReturn createReturn = new CreateReturn();
////        createReturn.setIndex(1);
////        createReturn.setSuccess(true);
////        Mockito.when(service.addSnatFilter(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(createReturn);
//        Assert.assertEquals(2,lbService.addSnatInEquipment(eip));
//    }

    @Test
    public void delSnatFromEquiment(){
        Eip eip = new Eip();
        eip.setSnatId("12");
        PowerMockito.when(RadwareService2Del.delFilter(Mockito.anyInt(),Mockito.any())).thenReturn(false);
        Boolean result = lbService.delSnatFromEquiment(eip);
        Assert.assertEquals(false,result);
    }

    @Test
    public void delDnatFromEquiment() throws Exception {
        Eip eip = new Eip();
        eip.setDnatId("dnatId");
        PowerMockito.when(RadwareService4query.queryServerByGroupIndex(Mockito.eq(""),Mockito.any())).thenReturn("123");
        PowerMockito.when(RadwareService2Del.delVirtualServer(Mockito.anyString(),Mockito.any())).thenReturn(true);
        PowerMockito.when(RadwareService2Del.delGroup(Mockito.eq(null),Mockito.any())).thenReturn(true);
        PowerMockito.when(RadwareService2Del.delServer(Mockito.eq(null),Mockito.any())).thenReturn(false);
        Boolean result = lbService.delDnatFromEquiment(eip);
        Assert.assertEquals(false,result);
    }

    @Test
    public void addNatAndQos() throws Exception {
//        LbService lbService= new LbService();
        Eip eip = new Eip();
        eip.setChargeMode("SharedBandwidth");
        RadwareService radwareService = PowerMockito.mock(RadwareService.class);
        PowerMockito.whenNew(RadwareService.class).withAnyArguments().thenReturn(radwareService);
        Mockito.when(radwareService.addDnat(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(new AddNat());
        MethodReturn methodReturn = lbService.addNatAndQos(eip,"fip","eip",2,"firewallid");
        Assert.assertEquals(500,methodReturn.getHttpCode());
    }

    @Test
    public void delNatAndQos(){
        Eip eip = new Eip();
        eip.setId("11");
        eip.setDnatId("22");
        eip.setSnatId("33");
        PowerMockito.when(RadwareService4query.queryServerByGroupIndex(Mockito.eq(""),Mockito.any())).thenReturn("123");
        PowerMockito.when(RadwareService2Del.delVirtualServer(Mockito.anyString(),Mockito.any())).thenReturn(true);
        PowerMockito.when(RadwareService2Del.delGroup(Mockito.eq(null),Mockito.any())).thenReturn(true);
        PowerMockito.when(RadwareService2Del.delServer(Mockito.eq(null),Mockito.any())).thenReturn(false);
        MethodReturn methodReturn = lbService.delNatAndQos(eip);
        Assert.assertEquals(500,methodReturn.getHttpCode());
    }

    @Test
    public void addFipToSbwQos(){

        String result = lbService.addFipToSbwQos("11","22","9dea38f8-f59c-4847-ba43-f0ef61a6986c");
        Assert.assertEquals("9dea38f8-f59c-4847-ba43-f0ef61a6986c",result);
    }

    @Test
    public void ping(){
        Boolean result = lbService.ping("11","22");
        Assert.assertEquals(false,result);
    }

    @Test
    public void getRootPipeName() throws InvocationTargetException, IllegalAccessException {
        Method method = PowerMockito.method(LbService.class,"getRootPipeName",String.class);
        String result =(String) method.invoke(lbService,"12.34.56");
        Assert.assertEquals("12.34.56.0",result);
    }
}
