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
import org.apache.kafka.common.protocol.types.Field;
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

@PrepareForTest({CommonUtil.class})
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
    public void addQosCase1() throws Exception {
        long x = 0L;
        Mockito.doReturn(x).when(eipRepository).countByPipId(Mockito.anyString());
        Mockito.doReturn("112").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        String result = firewallService.addQos("11.12.13.14","22","55","44");
    }
    @Test
    public void addQosCase2() throws Exception {
        long x = 55L;
        Mockito.doReturn(x).when(eipRepository).countByPipId(Mockito.anyString());
        Mockito.doReturn("112").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        String result = firewallService.addQos("11.12.13.14","22","55","44");
    }

    @Test
    public void updateQosBandWidthCase1() throws Exception {
        Mockito.doReturn("112").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Boolean result = firewallService.updateQosBandWidth("11","55.56.57.0","33","44","55.56.57.58","66");
    }
    @Test
    public void updateQosBandWidthCase2() throws Exception {
        Mockito.doReturn("112").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.eq(null));
        Boolean result = firewallService.updateQosBandWidth("11","9dea38f8-f59c-4847-ba43-f0ef61a6986c","33","44","55.56.57.58","66");
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
        Firewall firewall = new Firewall();
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(firewall);
        Boolean result = firewallService.delQos("55.56.57.0","22","55.56.57.10","44");
    }
    @Test
    public void delQosCase2() throws Exception {
        Firewall firewall = new Firewall();
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(firewall);
        Boolean result = firewallService.delQos("054576b5-a3eb-426e-acf7-6e713f7a88f0","22","55.56.57.10","44");
    }
    @Test
    public void delQosCase3() throws Exception {
        Firewall firewall = new Firewall();
        firewall.setIp("11");
        firewall.setPort("22");
        firewall.setUser("adm");
        firewall.setPasswd("mmmm");
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(firewall);
        Boolean result = firewallService.delQos("054576b5-713f7a88f0","22","55.56.57.10","44");
    }


    @Test
    public void addNatAndQosCase1() throws Exception {
        Mockito.doReturn("112=1134=22").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Mockito.doReturn("112").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.eq(null));
        Eip eip = new Eip();
        eip.setSbwId("054576b5-a3eb-426e-acf7-6e713f7a88f0");
        eip.setChargeMode("SharedBandwidth");
        eip.setFirewallId("firewallid");
        Sbw sbw = Sbw.builder().id("id").build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        MethodReturn result = firewallService.addNatAndQos(eip,"12","23",34,"45");
    }
    @Test
    public void addNatAndQosCase2() throws Exception {
        Mockito.doReturn(null).when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
//        Mockito.doReturn("112").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.eq(null));
        Eip eip = new Eip();
        eip.setSbwId("054576b5-a3eb-426e-acf7-6e713f7a88f0");
        eip.setChargeMode("SharedBandwidth");
        eip.setFirewallId("firewallid");
        Sbw sbw = Sbw.builder().id("id").build();
        Optional<Sbw> optional = Optional.of(sbw);
//        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        MethodReturn result = firewallService.addNatAndQos(eip,"12","23",34,"45");
    }
    @Test
    public void addNatAndQosCase3() throws Exception {
        Mockito.doReturn("112=1134=22").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Mockito.doReturn(null).when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.eq(null));
        Eip eip = new Eip();
        eip.setSbwId("054576b5-a3eb-426e-acf7-6e713f7a88f0");
        eip.setChargeMode("SharedBandwidth");
        eip.setFirewallId("firewallid");
        Sbw sbw = Sbw.builder().id("054576b5-a3eb-426e-acf7-6e713f7a88f0").build();
        Optional<Sbw> optional = Optional.of(sbw);
        Mockito.doReturn(optional).when(sbwRepository).findById(Mockito.anyString());
        MethodReturn result = firewallService.addNatAndQos(eip,"12","23",34,"45");
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
        MethodReturn result = firewallService.delNatAndQos(eipEntity);
    }
    @Test
    public void delNatAndQosCase2() throws Exception {
        Eip eipEntity = new Eip();
        eipEntity.setDnatId("dnatid");
        eipEntity.setFirewallId("firewallid");
        eipEntity.setSnatId("snatId");
        eipEntity.setFloatingIp("floatIp");
        eipEntity.setChargeMode("year");
        eipEntity.setSbwId("sbwId");
        eipEntity.setPipId("054576b5-a3eb-426e-acf7-6e713f7a88f0");
        Mockito.doReturn("123").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.eq(null));

        MethodReturn result = firewallService.delNatAndQos(eipEntity);
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
        Mockito.doReturn("123").when(fireWallCommondService).execCustomCommand(Mockito.anyString(),Mockito.anyString(),Mockito.eq(null));
        Firewall firewall = new Firewall();
        firewall.setIp("id");
        firewall.setPort("port");
        firewall.setUser("user");
        firewall.setPasswd("password");
        PowerMockito.when(CommonUtil.class,"getFireWallById",anyString()).thenReturn(firewall);
        Mockito.doReturn(true).when(qosService).removeIpFromPipe(Mockito.anyString(),Mockito.anyString());
        Boolean result = firewallService.removeFipFromSbwQos("11","22","33");
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

//    @Test
//    public void cmdUpdateQosBandWidth() throws Exception {
//        FirewallService spy = PowerMockito.spy(firewallService);
//        PowerMockito.doReturn("66").when(spy,"getRootPipeName",Mockito.anyString());
//        Boolean result = spy.cmdUpdateQosBandWidth("11","22","33","44");
//    }

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
