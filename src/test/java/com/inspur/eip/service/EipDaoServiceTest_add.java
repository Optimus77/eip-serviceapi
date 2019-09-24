package com.inspur.eip.service;

import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.eip.EipPool;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.service.*;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.common.DateUtils4Jdk8;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openstack4j.model.common.ActionResponse;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.Optional;

@PrepareForTest({CommonUtil.class, DateUtils4Jdk8.class})
@RunWith(PowerMockRunner.class)
public class EipDaoServiceTest_add {
    @InjectMocks
    private EipDaoService eipDaoService;

    @Mock
    private FlowService flowService;

    @Mock
    private EipPoolRepository eipPoolRepository;
    @Mock
    private EipRepository eipRepository;
    @Mock
    private IDevProvider providerService;
    @Mock
    private NeutronService neutronService;
    @Mock
    private EipV6DaoService eipV6DaoService;

    protected static final String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJlYmFhNTY3Mi1iOWM0LTQ2MzQtOWMxNS01NGMzNTI3ZDY4OTUiLCJleHAiOjE1NjI1ODIyMjQsIm5iZiI6MCwiaWF0IjoxNTYyNTc2ODI0LCJpc3MiOiJodHRwczovL2lvcGRldi4xMC4xMTAuMjUuMTIzLnhpcC5pby9hdXRoL3JlYWxtcy9waWNwIiwiYXVkIjpbImFjY291bnQiLCJyZHMtbXlzcWwtYXBpIl0sInN1YiI6IjlkMGI2N2NkLTIwY2ItNDBiNC04ZGM0LWIwNDE1Y2EyNWQ3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImlhYXMtc2VydmVyIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYjg0ZTBmODQtZjRhOC00NDU2LTgzNWQtYTRjNjE3OWYwMDQzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJBQ0NPVU5UX0FETUlOIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfSwicmRzLW15c3FsLWFwaSI6eyJyb2xlcyI6WyJ1c2VyIl19fSwic2NvcGUiOiIiLCJwcm9qZWN0IjoibGlzaGVuZ2hhbyIsImdyb3VwcyI6WyIvZ3JvdXAtbGlzaGVuZ2hhbyJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsaXNoZW5naGFvIiwiZW1haWwiOiJsaXNoZW5naGFvQGluc3B1ci5jb20ifQ.e743AsxR1MGxGThl-CdH3Rf4TKZtsZrdg0NWvA5G8jZQEp_1S8p32U7t7STv6Km2JvVr13LvoAD9spT5Nu1foOx_jPvoeAUXDfyKNHPlFlUynOYDOAQ53n8pgOYqqAsRsasGa-_SbEzgHzP7zf4u7n_8eGK116-Dm1wOty3orBVQtCLmPRIoLy_rQxWzNx7sxNGoZybsr99vg3h62JnCRVbSaaiivpWjJdiXmJ042vGJk5B452qhkoQMQUJNbS0rYFIoK_xiem1XrJJ5gkjWgCLBXBZ62pocpgLWTnqTpKqa8KLJziIJlYIsMHei0JYxncbXxkxWR5iWEZW0tO1qXA";

    @Before
    public void setUp(){
        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.mockStatic(DateUtils4Jdk8.class);

    }

    @Test
    public void allocateEip() throws KeycloakTokenException {
        EipAllocateParam eipConfig = Mockito.mock(EipAllocateParam.class);
        EipPool eip = Mockito.mock(EipPool.class);
        Mockito.doReturn("1").when(eip).getState();
        Mockito.doReturn(eip).when(eipPoolRepository).saveAndFlush(eip);
        Eip result = eipDaoService.allocateEip(eipConfig,eip,"abc",token);
    }
    @Test
    public void allocateEip2() throws KeycloakTokenException {
        EipAllocateParam eipConfig = Mockito.mock(EipAllocateParam.class);
        EipPool eip = Mockito.mock(EipPool.class);

        Mockito.doReturn("0").when(eip).getState();
        Mockito.doReturn("12").when(eip).getIp();
        Mockito.doReturn(eip).when(eipPoolRepository).saveAndFlush(eip);

        EipPool eipPoolCheck = Mockito.mock(EipPool.class);
        Integer integer = 8;
        Mockito.doReturn(integer).when(eipPoolCheck).getId();
        Mockito.doNothing().when(eipPoolRepository).flush();

        Eip eipEntity = Mockito.mock(Eip.class);
        Mockito.doReturn("11").when(eipEntity).getEipAddress();
        Mockito.doReturn("22").when(eipEntity).getId();
        Mockito.doReturn(eipEntity).when(eipRepository).findByEipAddressAndIsDelete(Mockito.anyString(),Mockito.anyInt());

        Mockito.doReturn(eipPoolCheck).when(eipPoolRepository).findByIp(Mockito.anyString());
        Eip result = eipDaoService.allocateEip(eipConfig,eip,"abc",token);
    }
    @Test
    public void allocateEip3() throws KeycloakTokenException {
        EipAllocateParam eipConfig = Mockito.mock(EipAllocateParam.class);
        EipPool eip = Mockito.mock(EipPool.class);
        Mockito.doReturn("hourlyNetflow").when(eipConfig).getBillType();
        Mockito.doReturn("0").when(eip).getState();
        Mockito.doReturn("12").when(eip).getIp();
        Mockito.doReturn("132").when(eip).getFireWallId();
        Mockito.doReturn(eip).when(eipPoolRepository).saveAndFlush(eip);

        EipPool eipPoolCheck = Mockito.mock(EipPool.class);
        Integer integer = 8;
        Mockito.doReturn(integer).when(eipPoolCheck).getId();
        Mockito.doNothing().when(eipPoolRepository).flush();

        Eip eipEntity = Mockito.mock(Eip.class);
        Mockito.doReturn("11").when(eipEntity).getEipAddress();
        Mockito.doReturn("22").when(eipEntity).getId();
        Mockito.doReturn(null).when(eipRepository).findByEipAddressAndIsDelete(Mockito.anyString(),Mockito.anyInt());

        Mockito.doReturn(true).when(providerService).cmdCreateOrDeleteAddressBook(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean());
        Mockito.doReturn(false).when(providerService).cmdOperateStatisticsBook(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean());
        Mockito.doReturn(eipPoolCheck).when(eipPoolRepository).findByIp(Mockito.anyString());
        Eip result = eipDaoService.allocateEip(eipConfig,eip,"abc",token);
    }

//    @Test
//    public void deleteEip(){
//        Eip eipEntity = Mockito.mock(Eip.class);
//        Optional<Eip> optional = Optional.of(eipEntity);
//        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
//        Mockito.doReturn("cn").when(eipEntity).getRegion();
//        Mockito.doReturn("23").when(eipEntity).getInstanceId();
//        Mockito.doReturn("23").when(eipEntity).getStatus();
//        Mockito.doReturn("23").when(eipEntity).getBillType();
//        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
//        ActionResponse delV6Ret = ActionResponse.actionFailed("error",400);
//        Mockito.doReturn(delV6Ret).when(eipV6DaoService).deleteEipV6(Mockito.anyString(),Mockito.anyString());
//
//        Mockito.doReturn(token).when(eipEntity).getProjectId();
//        PowerMockito.mockStatic(CommonUtil.class);
//        PowerMockito.when(CommonUtil.verifyToken(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
////        Mockito.doReturn(true).when(neutronService).deleteFloatingIp(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
//        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
//        ActionResponse reuslt = eipDaoService.deleteEip("11","22",token);
//    }
//    @Test
//    public void deleteEip1(){
//        Eip eipEntity = Mockito.mock(Eip.class);
//        Optional<Eip> optional = Optional.of(eipEntity);
//        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
//        Mockito.doReturn("23").when(eipEntity).getEipAddress();
//        Mockito.doReturn("23").when(eipEntity).getFirewallId();
//
//        Mockito.doReturn("cn").when(eipEntity).getRegion();
//        Mockito.doReturn("23").when(eipEntity).getInstanceId();
//        Mockito.doReturn("23").when(eipEntity).getStatus();
//        Mockito.doReturn("hourlyNetflow").when(eipEntity).getBillType();
//        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
//        ActionResponse delV6Ret = ActionResponse.actionSuccess(400);
//        Mockito.doReturn(delV6Ret).when(eipV6DaoService).deleteEipV6(Mockito.anyString(),Mockito.anyString());
//        Mockito.when(DateUtils4Jdk8.countMinuteFromPoint()).thenReturn(1);
//        Mockito.doReturn(true).when(providerService).cmdOperateStatisticsBook(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean());
//        Mockito.doReturn(false).when(providerService).cmdCreateOrDeleteAddressBook(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean());
//        Mockito.doNothing().when(flowService).reportNetFlowByDbBeforeRelease(Mockito.any());
//
//        EipPool eipPool = Mockito.mock(EipPool.class);
//        Mockito.doReturn(eipPool).when(eipPoolRepository).findByIp(Mockito.anyString());
//        Mockito.doReturn(token).when(eipEntity).getProjectId();
//        PowerMockito.when(CommonUtil.verifyToken(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
////        Mockito.doReturn(true).when(neutronService).deleteFloatingIp(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
//        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
//        ActionResponse reuslt = eipDaoService.deleteEip("11","22",token);
//    }

    @Test
    public void adminDeleteEip(){
        Eip eipEntity = Mockito.mock(Eip.class);
        Optional<Eip> optional = Optional.of(eipEntity);
        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
        Mockito.doReturn("23").when(eipEntity).getEipAddress();
        Mockito.doReturn("23").when(eipEntity).getFirewallId();
        Mockito.doReturn(2).when(eipEntity).getIsDelete();
        Mockito.doReturn("23").when(eipEntity).getDnatId();
        Mockito.doReturn("23").when(eipEntity).getPipId();
        Mockito.doReturn("23").when(eipEntity).getSnatId();

        Mockito.doReturn("cn").when(eipEntity).getRegion();
        Mockito.doReturn("23").when(eipEntity).getInstanceId();
        Mockito.doReturn("23").when(eipEntity).getStatus();
        Mockito.doReturn("hourlyNetflow").when(eipEntity).getBillType();
        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
        ActionResponse delV6Ret = ActionResponse.actionFailed("error",400);
        Mockito.doReturn(delV6Ret).when(eipV6DaoService).adminDeleteEipV6(Mockito.anyString());
//        MethodReturn methodReturn = MethodReturn.builder().eip(eipEntity).build();
        eipDaoService.adminDeleteEip("11");
    }
    @Test
    public void adminDeleteEip2(){
        Eip eipEntity = Mockito.mock(Eip.class);
        Optional<Eip> optional = Optional.of(eipEntity);
        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
        Mockito.doReturn("23").when(eipEntity).getEipAddress();
        Mockito.doReturn("23").when(eipEntity).getFirewallId();
        Mockito.doReturn(2).when(eipEntity).getIsDelete();
        Mockito.doReturn("23").when(eipEntity).getDnatId();
        Mockito.doReturn("23").when(eipEntity).getPipId();
        Mockito.doReturn("23").when(eipEntity).getSnatId();

        Mockito.doReturn("cn").when(eipEntity).getRegion();
        Mockito.doReturn("23").when(eipEntity).getInstanceId();
        Mockito.doReturn("23").when(eipEntity).getStatus();
        Mockito.doReturn("hourlyNetflow").when(eipEntity).getBillType();
        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
        ActionResponse delV6Ret = ActionResponse.actionSuccess(400);
        Mockito.when(DateUtils4Jdk8.countMinuteFromPoint()).thenReturn(1);
        Mockito.doNothing().when(flowService).reportNetFlowByDbBeforeRelease(Mockito.any());
        Mockito.doReturn(true).when(providerService).cmdOperateStatisticsBook(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean());
        Mockito.doReturn(false).when(providerService).cmdCreateOrDeleteAddressBook(Mockito.anyString(),Mockito.anyString(),Mockito.anyBoolean());
        EipPool eipPool = Mockito.mock(EipPool.class);
        Mockito.doReturn(eipPool).when(eipPoolRepository).findByIp(Mockito.anyString());

        Mockito.doReturn(delV6Ret).when(eipV6DaoService).adminDeleteEipV6(Mockito.anyString());
//        MethodReturn methodReturn = MethodReturn.builder().eip(eipEntity).build();
        eipDaoService.adminDeleteEip("11");
    }

    @Test
    public void associateInstanceWithEip() throws KeycloakTokenException {
        Eip eipEntity = Mockito.mock(Eip.class);
        Optional<Eip> optional = Optional.of(eipEntity);
        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
        Mockito.doReturn("23").when(eipEntity).getEipAddress();
        Mockito.doReturn("23").when(eipEntity).getFirewallId();
        Mockito.doReturn(2).when(eipEntity).getIsDelete();
        Mockito.doReturn("23").when(eipEntity).getPipId();
        Mockito.doReturn("cn").when(eipEntity).getRegion();
        Mockito.doReturn("23").when(eipEntity).getInstanceId();
        Mockito.doReturn("23").when(eipEntity).getProjectId();

        Mockito.doReturn("DOWN").when(eipEntity).getStatus();
        Mockito.doReturn("hourlyNetflow").when(eipEntity).getBillType();
        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
        ReflectionTestUtils.setField(eipDaoService,"flpnetworkId",null);
        PowerMockito.when(CommonUtil.getProjectId()).thenReturn("23");
        MethodReturn result = eipDaoService.associateInstanceWithEip("11","22","1","44",null);
    }
    @Test
    public void associateInstanceWithEip2() throws KeycloakTokenException {
        Eip eipEntity = Mockito.mock(Eip.class);
        Optional<Eip> optional = Optional.of(eipEntity);
        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
        Mockito.doReturn("23").when(eipEntity).getEipAddress();
        Mockito.doReturn("23").when(eipEntity).getFirewallId();
        Mockito.doReturn(2).when(eipEntity).getIsDelete();
        Mockito.doReturn("23").when(eipEntity).getPipId();
        Mockito.doReturn("cn").when(eipEntity).getRegion();
        Mockito.doReturn("23").when(eipEntity).getInstanceId();
        Mockito.doReturn("23").when(eipEntity).getProjectId();

        Mockito.doReturn("DOWN").when(eipEntity).getStatus();
        Mockito.doReturn("hourlyNetflow").when(eipEntity).getBillType();
        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
        ReflectionTestUtils.setField(eipDaoService,"flpnetworkId","21");
        PowerMockito.when(CommonUtil.getProjectId()).thenReturn("23");
        MethodReturn result = eipDaoService.associateInstanceWithEip("11","22","1","44",null);
    }
    @Test
    public void associateInstanceWithEip3() throws KeycloakTokenException {
        Eip eipEntity = Mockito.mock(Eip.class);
        Optional<Eip> optional = Optional.of(eipEntity);
        Mockito.doReturn(2).when(eipEntity).getBandWidth();

        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
        Mockito.doReturn("23").when(eipEntity).getEipAddress();
        Mockito.doReturn("23").when(eipEntity).getFirewallId();
        Mockito.doReturn(2).when(eipEntity).getIsDelete();
        Mockito.doReturn("23").when(eipEntity).getPipId();
        Mockito.doReturn("cn").when(eipEntity).getRegion();
        Mockito.doReturn("23").when(eipEntity).getInstanceId();
        Mockito.doReturn("23").when(eipEntity).getProjectId();
        Mockito.doReturn("23").when(eipEntity).getFloatingIp();

        Mockito.doReturn("DOWN").when(eipEntity).getStatus();
        Mockito.doReturn("hourlyNetflow").when(eipEntity).getBillType();
        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
        ReflectionTestUtils.setField(eipDaoService,"flpnetworkId","21");
        PowerMockito.when(CommonUtil.getProjectId()).thenReturn("23");
        MethodReturn fireWallReturn = MethodReturn.builder().message("ee").innerCode("23").httpCode(200).build();
        Mockito.doReturn(fireWallReturn).when(providerService).addNatAndQos(Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString());
        MethodReturn result = eipDaoService.associateInstanceWithEip("11","22","1","44","12");
    }
    @Test
    public void associateInstanceWithEip4() throws KeycloakTokenException {
        Eip eipEntity = Mockito.mock(Eip.class);
        Optional<Eip> optional = Optional.of(eipEntity);
        Mockito.doReturn(2).when(eipEntity).getBandWidth();

        Mockito.doReturn("23").when(eipEntity).getFloatingIpId();
        Mockito.doReturn("23").when(eipEntity).getEipAddress();
        Mockito.doReturn("23").when(eipEntity).getFirewallId();
        Mockito.doReturn(2).when(eipEntity).getIsDelete();
        Mockito.doReturn("23").when(eipEntity).getPipId();
        Mockito.doReturn("cn").when(eipEntity).getRegion();
        Mockito.doReturn("23").when(eipEntity).getInstanceId();
        Mockito.doReturn("23").when(eipEntity).getProjectId();
        Mockito.doReturn("23").when(eipEntity).getFloatingIp();

        Mockito.doReturn("DOWN").when(eipEntity).getStatus();
        Mockito.doReturn("hourlyNetflow").when(eipEntity).getBillType();
        Mockito.doReturn("23").when(eipEntity).getEipV6Id();
        Mockito.doReturn(optional).when(eipRepository).findById(Mockito.anyString());
        ReflectionTestUtils.setField(eipDaoService,"flpnetworkId","21");
        PowerMockito.when(CommonUtil.getProjectId()).thenReturn("23");
        MethodReturn fireWallReturn = MethodReturn.builder().message("ee").innerCode("23").httpCode(201).build();
        Mockito.doReturn(fireWallReturn).when(providerService).addNatAndQos(Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString());
        MethodReturn result = eipDaoService.associateInstanceWithEip("11","22","1","44","12");
    }
}

