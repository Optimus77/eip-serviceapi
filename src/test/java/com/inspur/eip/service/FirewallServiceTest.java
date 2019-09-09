package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.fw.Firewall;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.common.MethodReturnUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.omg.PortableServer.POA;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@PrepareForTest({FirewallService.class,CommonUtil.class})
@RunWith(PowerMockRunner.class)
public class FirewallServiceTest {
    @InjectMocks
    private FirewallService firewallService;

    @Mock
    private FireWallCommondService fireWallCommondService;
    @Mock
    private EipRepository eipRepository;
    @Mock
    private SbwRepository sbwRepository;
    @Mock
    private QosService qosService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(CommonUtil.class);
        Firewall firewall = new Firewall();
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(firewall);
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(null);

    }

    @Test
    public void addDnat() throws Exception {
//        FirewallService spy = PowerMockito.spy(firewallService);
//        PowerMockito.when(spy,"addDnat",anyString(),anyString(),anyString());
        Mockito.when(fireWallCommondService.execCustomCommand(anyString(),anyString(),anyString())).thenReturn("123");
        Method method = PowerMockito.method(FirewallService.class,"addDnat",String.class,String.class,String.class);
        method.invoke(firewallService,"11","22","33");
    }

    @Test
    public void addSnat() throws InvocationTargetException, IllegalAccessException {
        Method method = PowerMockito.method(FirewallService.class,"addSnat",String.class,String.class,String.class);
        method.invoke(firewallService,"12","34","56");
    }

    @Test
    public void addQosCase1() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("66").when(spy,"getRootPipeName",Mockito.anyString());
        long x = 0L;
        Mockito.doReturn(x).when(eipRepository).countByPipId(Mockito.anyString());
        PowerMockito.doReturn(true).when(spy,"cmdAddRootPipe",anyString(),anyString(),anyString(),anyString(),anyString(),anyString());
        spy.addQos("11","22","55","44");
    }
    @Test
    public void addQosCase2() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("66").when(spy,"getRootPipeName",Mockito.anyString());
        long x = 2L;
        Mockito.doReturn(x).when(eipRepository).countByPipId(Mockito.anyString());
//        PowerMockito.doReturn(true).when(spy,"cmdAddRootPipe",anyString(),anyString(),anyString(),anyString(),anyString(),anyString());
        spy.addQos("11","22","55","44");
    }

    @Test
    public void updateQosBandWidthCase1() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("22").when(spy,"getRootPipeName",Mockito.anyString());
        spy.updateQosBandWidth("11","22","33","44","55","66");
    }
    @Test
    public void updateQosBandWidthCase2() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("22").when(spy,"getRootPipeName",Mockito.anyString());
        spy.updateQosBandWidth("11","9dea38f8-f59c-4847-ba43-f0ef61a6986c","33","44","55","66");
    }

    @Test
    public void cmdUpdateRootQosBandWidth() throws InvocationTargetException, IllegalAccessException {
        Method method = PowerMockito.method(FirewallService.class,"cmdUpdateRootQosBandWidth",String.class,String.class,String.class);
        method.invoke(firewallService,"11","55","66");
    }
    @Test
    public void cmdUpdateRootQosBandWidthCase2() throws InvocationTargetException, IllegalAccessException {
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),eq(null))).thenReturn("11");
        Method method = PowerMockito.method(FirewallService.class,"cmdUpdateRootQosBandWidth",String.class,String.class,String.class);
        method.invoke(firewallService,"11","55","66");
    }

    @Test
    public void delQosCase1() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("11").when(spy,"getRootPipeName",Mockito.anyString());
        spy.delQos("11","22","33","44");
    }
    @Test
    public void delQosCase2() throws Exception {
        Firewall firewall = new Firewall();
        QosService qs = Mockito.mock(QosService.class);
        PowerMockito.whenNew(QosService.class).withAnyArguments().thenReturn(qs);
        Map<String,String> map = new HashMap<>();
        map.put("success","true");
        Mockito.doReturn(map).when(qs).delQosPipe(Mockito.anyString());
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(firewall);
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("22").when(spy,"getRootPipeName",Mockito.anyString());
        spy.delQos("11","22","33","44");
    }
    @Test
    public void delQosCase3() throws Exception {
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(null);
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("8").when(spy,"getRootPipeName",Mockito.anyString());
        spy.delQos("11","22","33","44");
    }

    @Test
    public void addNatAndQosCase1() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("dnatRuleId").when(spy,"addDnat",Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        PowerMockito.doReturn("snatRuleId").when(spy,"addSnat",Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Eip eip = new Eip();
        eip.setSbwId("sbwid");
        eip.setChargeMode("SharedBandwidth");
        eip.setFirewallId("firewallid");
        Sbw sbw = Sbw.builder().id("id").build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        Mockito.doReturn("pipid").when(spy).addFipToSbwQos(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        spy.addNatAndQos(eip,"12","23",34,"45");
    }
    @Test
    public void addNatAndQosCase2() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("dnatRuleId").when(spy,"addDnat",Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        PowerMockito.doReturn("snatRuleId").when(spy,"addSnat",Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Eip eip = new Eip();
        eip.setSbwId("sbwid");
        eip.setChargeMode("SharedBandwidth");
        eip.setFirewallId("firewallid");
        Sbw sbw = Sbw.builder().id("id").build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        Mockito.doReturn(null).when(spy).addFipToSbwQos(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        MethodReturn methodReturn= spy.addNatAndQos(eip,"12","23",34,"45");
        Assert.assertEquals(methodReturn.getHttpCode(),500);
    }
    @Test
    public void addNatAndQosCase3() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("dnatRuleId").when(spy,"addDnat",Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        PowerMockito.doReturn(null).when(spy,"addSnat",Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Eip eip = new Eip();
        MethodReturn methodReturn= spy.addNatAndQos(eip,"12","23",34,"45");
        Assert.assertEquals(methodReturn.getHttpCode(),500);
    }
    @Test
    public void addNatAndQosCase4() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn(null).when(spy,"addDnat",Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Eip eip = new Eip();
        MethodReturn methodReturn= spy.addNatAndQos(eip,"12","23",34,"45");
        Assert.assertEquals(methodReturn.getHttpCode(),500);
    }
    @Test
    public void delNatAndQosCase1() throws Exception {
        Eip eipEntity = new Eip();
        eipEntity.setDnatId("dnatid");
        eipEntity.setFirewallId("firewallid");
        eipEntity.setSnatId("snatId");
        eipEntity.setFloatingIp("floatIp");
        eipEntity.setChargeMode("SharedBandwidth");
        eipEntity.setSbwId("sbwId");
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn(true).when(spy,"delDnat",Mockito.anyString(),Mockito.anyString());
        PowerMockito.doReturn(true).when(spy,"delSnat",Mockito.anyString(),Mockito.anyString());
        spy.delNatAndQos(eipEntity);
    }
    @Test
    public void delNatAndQosCase2() throws Exception {
        Eip eipEntity = new Eip();
        eipEntity.setDnatId("dnatid");
        eipEntity.setFirewallId("firewallid");
        eipEntity.setSnatId("snatId");
        eipEntity.setFloatingIp("floatIp");
        eipEntity.setChargeMode("Shared");
        eipEntity.setSbwId("sbwId");
        eipEntity.setPipId("pipid");
        eipEntity.setEipAddress("eipaddress");
        FirewallService spy = PowerMockito.spy(firewallService);
        Mockito.doReturn(false).when(spy).delQos(anyString(),anyString(),anyString(),anyString());
        PowerMockito.doReturn(false).when(spy,"delDnat",Mockito.anyString(),Mockito.anyString());
        PowerMockito.doReturn(false).when(spy,"delSnat",Mockito.anyString(),Mockito.anyString());
        spy.delNatAndQos(eipEntity);
    }

    @Test
    public void addFipToSbwQos(){
        String result = firewallService.addFipToSbwQos("12","34","9dea38f8-f59c-4847-ba43-f0ef61a6986c");
    }

    @Test
    public void removeFipFromSbwQos(){
        Boolean result = firewallService.removeFipFromSbwQos("11",null,"33");
    }
    @Test
    public void removeFipFromSbwQosCase2() throws Exception {
        Firewall firewall = new Firewall();
        firewall.setIp("id");
        firewall.setPort("port");
        firewall.setUser("user");
        firewall.setPasswd("password");
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(firewall);
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn(false).when(spy,"cmdDelIpInSbwPipe",anyString(),anyString(),anyString());
        Mockito.doReturn(true).when(qosService).removeIpFromPipe(Mockito.anyString(),Mockito.anyString());
        Boolean result = spy.removeFipFromSbwQos("11","22","33");
    }

    @Test
    public void delSnat() throws InvocationTargetException, IllegalAccessException {
        Method method = PowerMockito.method(FirewallService.class,"delSnat",String.class,String.class);
        Boolean result =(Boolean) method.invoke(firewallService,"11","22");
    }

    @Test
    public void delDnat() throws InvocationTargetException, IllegalAccessException {
        Method method = PowerMockito.method(FirewallService.class,"delDnat",String.class,String.class);
        Boolean result =(Boolean) method.invoke(firewallService,"11","22");
    }

    @Test
    public void cmdDelQos(){
        Boolean result = firewallService.cmdDelQos("11","22","33");
    }
    @Test
    public void cmdDelQosCase2(){
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("^-----");
        Boolean result = firewallService.cmdDelQos("11","22","33");
    }
    @Test
    public void cmdDelQosCase3(){
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("1");
        Boolean result = firewallService.cmdDelQos("11","22","33");
    }

    @Test
    public void cmdUpdateQosBandWidth() throws Exception {
        FirewallService spy = PowerMockito.spy(firewallService);
        PowerMockito.doReturn("66").when(spy,"getRootPipeName",Mockito.anyString());
        Boolean result = spy.cmdUpdateQosBandWidth("11","22","33","44");
    }

    @Test
    public void getRootPipeName() throws InvocationTargetException, IllegalAccessException {
        Method method = PowerMockito.method(FirewallService.class,"getRootPipeName",String.class);
        String result =(String) method.invoke(firewallService,"1.1.0.9");
    }

    @Test
    public void cmdAddRootPipe() throws InvocationTargetException, IllegalAccessException {
        Method method = PowerMockito.method(FirewallService.class,"cmdAddRootPipe",String.class,String.class,String.class,String.class,String.class,String.class);
        Boolean result = (Boolean) method.invoke(firewallService,"1","2","3","4","5","6");
    }

    @Test
    public void cmdAddSbwQos(){
        Boolean result = firewallService.cmdAddSbwQos("11","55","60");
    }

    @Test
    public void cmdDelSbwQos(){
        Boolean result = firewallService.cmdDelSbwQos("11","22");
    }
    @Test
    public void cmdDelSbwQosCase2(){
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),eq(null))).thenReturn("12");
        Boolean result = firewallService.cmdDelSbwQos("11","22");
    }

    @Test
    public void cmdDelIpInSbwPipe() throws InvocationTargetException, IllegalAccessException {
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),eq(null))).thenReturn("12");
        Method method = PowerMockito.method(FirewallService.class,"cmdDelIpInSbwPipe",String.class,String.class,String.class);
        Boolean result =(Boolean) method.invoke(firewallService,"11","22","33");
    }

    @Test
    public void cmdCreateOrDeleteAddressBook(){
        Boolean result = firewallService.cmdCreateOrDeleteAddressBook("11","22",false);
    }
    @Test
    public void cmdCreateOrDeleteAddressBookCase2(){
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("unrecognized keyword");
        Boolean result = firewallService.cmdCreateOrDeleteAddressBook("11","22",false);
    }

    @Test
    public void cmdInsertOrRemoveParamInAddressBook(){
        Boolean result = firewallService.cmdInsertOrRemoveParamInAddressBook("11","22","ip","33",false);
    }
    @Test
    public void cmdInsertOrRemoveParamInAddressBookCase2(){
        Boolean result = firewallService.cmdInsertOrRemoveParamInAddressBook("11","22","host","33",false);
    }
    @Test
    public void cmdInsertOrRemoveParamInAddressBookCase3(){
        Boolean result = firewallService.cmdInsertOrRemoveParamInAddressBook("11","22","range","33",false);
    }
    @Test
    public void cmdInsertOrRemoveParamInAddressBookCase4(){
        Boolean result = firewallService.cmdInsertOrRemoveParamInAddressBook("11","22","country","33",false);
    }
    @Test
    public void cmdInsertOrRemoveParamInAddressBookCase5(){
        Boolean result = firewallService.cmdInsertOrRemoveParamInAddressBook("11","22","member","33",false);
    }

    @Test
    public void cmdOperateStatisticsBook(){
        Boolean result = firewallService.cmdOperateStatisticsBook("11","22",false);
    }
    @Test
    public void cmdOperateStatisticsBookCase2(){
        Mockito.when(fireWallCommondService.execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn("unrecognized keyword");
        Boolean result = firewallService.cmdOperateStatisticsBook("11","22",false);
    }

    @Test
    public void cmdShowStatisticsByAddressBook(){
        Mockito.when(fireWallCommondService.cmdShowStasiticsAddress(Mockito.anyString(),Mockito.anyString())).thenReturn(new JSONObject());
        JSONObject result = firewallService.cmdShowStatisticsByAddressBook("11","","33");
    }
    @Test
    public void cmdShowStatisticsByAddressBookCase2(){
        Mockito.when(fireWallCommondService.cmdShowStasiticsAddress(Mockito.anyString(),Mockito.anyString())).thenReturn(new JSONObject());
        JSONObject result = firewallService.cmdShowStatisticsByAddressBook("11","current","33");
    }
    @Test
    public void cmdShowStatisticsByAddressBook3(){
        Mockito.when(fireWallCommondService.cmdShowStasiticsAddress(Mockito.anyString(),Mockito.anyString())).thenReturn(new JSONObject());
        JSONObject result = firewallService.cmdShowStatisticsByAddressBook("11","lastday","33");
    }
    @Test
    public void cmdShowStatisticsByAddressBook4(){
        Mockito.when(fireWallCommondService.cmdShowStasiticsAddress(Mockito.anyString(),Mockito.anyString())).thenReturn(new JSONObject());
        JSONObject result = firewallService.cmdShowStatisticsByAddressBook("11","lastmonth","33");
    }
}
