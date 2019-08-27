package com.inspur.eip.service;


import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.eip.*;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.ExtNetRepository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.common.MethodReturnUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.NetFloatingIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class EipDaoService {


    @Value("${fipNetworkId}")
    private String flpnetworkId;

    @Autowired
    private EipPoolRepository eipPoolRepository;

    @Autowired
    private ExtNetRepository extNetRepository;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    @Qualifier("radwareService")
    private IDevProvider providerService;

    @Autowired
    private NeutronService neutronService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EipV6DaoService eipV6DaoService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private LbService lbService;


    /**
     * allocate eip
     *
     * @param eipConfig eipconfig
     * @return result
     */
    @Transactional(rollbackFor = Exception.class)
    public Eip allocateEip(EipAllocateParam eipConfig, EipPool eip, String operater, String token) throws KeycloakTokenException {

        if (!eip.getState().equals("0")) {
            log.error("Fatal Error! eip state is not free, state:{}.", eip.getState());
            eipPoolRepository.saveAndFlush(eip);
            return null;
        }

        EipPool eipPoolCheck = eipPoolRepository.findByIp(eip.getIp());
        if (eipPoolCheck != null) {
            log.error("==================================================================================");
            log.error("Fatal Error! get a duplicate eip from eip pool, eip_address:{}.", eip.getIp());
            log.error("===================================================================================");
            eipPoolRepository.deleteById(eipPoolCheck.getId());
            eipPoolRepository.flush();
        }

        Eip eipEntity = eipRepository.findByEipAddressAndIsDelete(eip.getIp(), 0);
        if (null != eipEntity) {
            log.error("Fatal Error! get a duplicate eip from eip pool, eip_address:{} id:{}.",
                    eipEntity.getEipAddress(), eipEntity.getId());
            return null;
        }
        //按需 且 计费模式为流量计费
//        if (HsConstants.HOURLYSETTLEMENT.equalsIgnoreCase(eipConfig.getBillType()) &&
//                HsConstants.CHARGE_MODE_TRAFFIC.equalsIgnoreCase(eipConfig.getChargeMode())){
//            //创建地址簿 和 监控地址簿
//            if (providerService.cmdCreateOrDeleteAddressBook(eip.getIp(), eip.getFireWallId(), true)){
//                providerService.cmdOperateStatisticsBook(eip.getIp(), eip.getFireWallId(), true);
//            }
//        }

        Eip eipMo = new Eip();
        eipMo.setEipAddress(eip.getIp());
        eipMo.setStatus(HsConstants.DOWN);
        eipMo.setFirewallId(eip.getFireWallId());

        eipMo.setIpType(eipConfig.getIpType());
        eipMo.setBillType(eipConfig.getBillType());
        eipMo.setChargeMode(eipConfig.getChargeMode());
        eipMo.setDuration(eipConfig.getDuration());
        eipMo.setBandWidth(eipConfig.getBandwidth());
        eipMo.setRegion(eipConfig.getRegion());
        eipMo.setSbwId(eipConfig.getSbwId());
        eipMo.setGroupId(eipConfig.getGroupId());
        String projectId = CommonUtil.getProjectId(token);
        log.debug("get tenantid:{} from clientv3", projectId);
        eipMo.setProjectId(projectId);
        eipMo.setUserId(CommonUtil.getUserId(token));
        eipMo.setUserName(CommonUtil.getUsername(token));
        eipMo.setIsDelete(0);
        if (null != operater) {
            eipMo.setName(operater);
        }
        eipMo.setCreatedTime(CommonUtil.getGmtDate());
        eipRepository.saveAndFlush(eipMo);
        log.debug("User:{} success allocate eip:{}", projectId, eipMo.getId());
        return eipMo;
    }


    @Transactional
    public ActionResponse deleteEip(String eipid, String token) {
        String msg;
        Optional<Eip> optional = eipRepository.findById(eipid);
        if (optional.isPresent()) {
            Eip eipEntity = optional.get();
            if (eipEntity.getIsDelete() == 1) {
                msg = "Faild to find eip by id:" + eipid;
                log.error(msg);
                return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
            }
            if (eipEntity.getStatus().equalsIgnoreCase(HsConstants.ACTIVE)){
                msg = "Failed to delete eip,please unbind eip first." + eipEntity.toString();
                log.error(msg);
                return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            if (eipEntity.getBillType().equals(HsConstants.MONTHLY)) {
                msg = "Failed to delete ,monthly eip can not delete by user." + eipEntity.toString();
                log.error(msg);
                return ActionResponse.actionFailed(msg, HttpStatus.SC_FORBIDDEN);
            }
            if (!CommonUtil.verifyToken(token, eipEntity.getProjectId())) {
                log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), eipid);
                return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
            }
            if (null != eipEntity.getFloatingIpId() ) {

                if(neutronService.deleteFloatingIp(eipEntity.getRegion(), eipEntity.getFloatingIpId(), eipEntity.getInstanceId(), token)){
                    eipEntity.setFloatingIp(null);
                    eipEntity.setFloatingIpId(null);
                } else {
                    msg = "Failed to delete floating ip, floatingIpId:" + eipEntity.getFloatingIpId();
                    log.error(msg);
                }

            }
            if(eipEntity.getEipV6Id() != null){
                ActionResponse delV6Ret = eipV6DaoService.deleteEipV6(eipEntity.getEipV6Id(), token);
                if (!delV6Ret.isSuccess()) {
                    msg = "Faild to delete ipv6 address.";
                    log.error(msg);
                    return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
            }
            //删除 监控集和地址簿，需保证关联的监控集已经删掉，否则无法删除地址簿
//            if(HsConstants.CHARGE_MODE_TRAFFIC.equalsIgnoreCase(eipEntity.getChargeMode())){
//                providerService.cmdCreateOrDeleteAddressBook(eipEntity.getEipAddress(),eipEntity.getFirewallId(),false);
//            }
            eipEntity.setIsDelete(1);
            eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
            eipEntity.setEipV6Id(null);
            eipRepository.saveAndFlush(eipEntity);
            if (eipEntity.getStatus().equals(HsConstants.ERROR)) {
                return ActionResponse.actionSuccess();
            }
            EipPool eipPool = eipPoolRepository.findByIp(eipEntity.getEipAddress());
            if (null != eipPool) {
                log.error("******************************************************************************");
                log.error("Fatal error, eip has already exist in eip pool. can not add to eip pool.{}",
                        eipEntity.getEipAddress());
                log.error("******************************************************************************");
            } else {
                EipPool eipPoolMo = new EipPool();
                eipPoolMo.setFireWallId(eipEntity.getFirewallId());
                eipPoolMo.setIp(eipEntity.getEipAddress());
                eipPoolMo.setState("0");
                eipPoolMo.setType(eipEntity.getIpType());
                eipPoolRepository.saveAndFlush(eipPoolMo);
                log.info("Success delete eip:{}", eipEntity.getEipAddress());
            }
            return ActionResponse.actionSuccess();
        }
        msg = "Faild to find eip by id:" + eipid;
        log.error(msg);
        return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
    }


    @Transactional
    public ActionResponse adminDeleteEip(String eipid) {
        String msg;
        Optional<Eip> optional = eipRepository.findById(eipid);
        if (optional.isPresent()) {
            Eip eipEntity = optional.get();
            if (eipEntity.getIsDelete() ==1){
                msg = "Faild to find eip by id:" + eipid;
                log.error(msg);
                return ActionResponse.actionSuccess();
            }
            if ((null != eipEntity.getPipId())
                    || (null != eipEntity.getDnatId())
                    || (null != eipEntity.getSnatId())) {
                msg = "Failed to delete eip,please unbind eip first." + eipEntity.toString();
                providerService.delNatAndQos(eipEntity);
                log.error(msg);
            }
            if (null != eipEntity.getFloatingIpId() ) {
                if(neutronService.superDeleteFloatingIp( eipEntity.getFloatingIpId(), eipEntity.getInstanceId())){
                    eipEntity.setFloatingIp(null);
                    eipEntity.setFloatingIpId(null);
                } else {
                    msg = "Failed to delete floating ip, floatingIpId:" + eipEntity.getFloatingIpId();
                    log.error(msg);
                }
            }

            if(eipEntity.getEipV6Id() != null){
                ActionResponse delV6Ret = eipV6DaoService.adminDeleteEipV6(eipEntity.getEipV6Id());
                if (!delV6Ret.isSuccess()) {
                    msg = "Faild to delete ipv6 address.";
                    log.error(msg);
                    return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
            }
            //删除地址簿，需保证关联的监控集已经删掉
//            if(HsConstants.CHARGE_MODE_TRAFFIC.equalsIgnoreCase(eipEntity.getChargeMode())){
//                providerService.cmdCreateOrDeleteAddressBook(eipEntity.getEipAddress(),eipEntity.getFirewallId(),false);
//            }
            eipEntity.setIsDelete(1);
            eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
            eipEntity.setEipV6Id(null);
            eipRepository.saveAndFlush(eipEntity);
            if (eipEntity.getStatus().equals(HsConstants.ERROR)) {
                return ActionResponse.actionSuccess();
            }
            EipPool eipPool = eipPoolRepository.findByIp(eipEntity.getEipAddress());
            if (null != eipPool) {
                log.error("******************************************************************************");
                log.error("Fatal error, eip has already exist in eip pool. can not add to eip pool.{}",
                        eipEntity.getEipAddress());
                log.error("******************************************************************************");
            } else {
                EipPool eipPoolMo = new EipPool();
                eipPoolMo.setFireWallId(eipEntity.getFirewallId());
                eipPoolMo.setIp(eipEntity.getEipAddress());
                eipPoolMo.setState("0");
                eipPoolMo.setType(eipEntity.getIpType());
                eipPoolRepository.saveAndFlush(eipPoolMo);
                log.info("Success delete eip:{}", eipEntity.getEipAddress());
            }

            return ActionResponse.actionSuccess();
        }
        msg = "Faild to find eip by id:" + eipid;
        log.error(msg);
        return ActionResponse.actionSuccess();
    }


    @Transactional
    public ActionResponse softDownEip(String eipid) {
        String msg;
        Optional<Eip> optional = eipRepository.findById(eipid);
        if (!optional.isPresent()) {
            msg = "Faild to find eip by id:" + eipid;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        Eip eipEntity = optional.get();

        eipEntity.setStatus(HsConstants.STOP);
        eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
        MethodReturn fireWallReturn = providerService.delNatAndQos(eipEntity);
        if (fireWallReturn.getHttpCode() == HttpStatus.SC_OK) {
            eipRepository.saveAndFlush(eipEntity);
            return ActionResponse.actionSuccess();
        } else {
            eipEntity.setStatus(HsConstants.ERROR);
            eipRepository.saveAndFlush(eipEntity);
            return ActionResponse.actionFailed(fireWallReturn.getMessage(), fireWallReturn.getHttpCode());
        }
    }

    /**
     * associate port with eip
     *
     * @param eipid        eip
     * @param serverId     server id
     * @param instanceType instance type
     * @return true or false
     */
    @Transactional(rollbackFor = Exception.class)
    public MethodReturn associateInstanceWithEip(String eipid, String serverId, String instanceType, String portId, String fip) {
        NetFloatingIP floatingIP;
        String returnStat;
        String returnMsg;
        MethodReturn fireWallReturn = null;

        Optional<Eip> optional = eipRepository.findById(eipid);
        if (!optional.isPresent()) {
            log.error("In associate process, failed to find the eip by id:{} ", eipid);
            return MethodReturnUtil.error(HttpStatus.SC_NOT_FOUND, ReturnStatus.SC_NOT_FOUND,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_NOT_FOND));
        }
        Eip eip = optional.get();
        if (!(HsConstants.DOWN.equals(eip.getStatus())) || (null != eip.getDnatId()) || (null != eip.getSnatId())) {
            return MethodReturnUtil.error(HttpStatus.SC_BAD_REQUEST, ReturnStatus.EIP_BIND_HAS_BAND,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_HAS_BAND));
        }
        eip.setStatus(HsConstants.BINDING);
        eipRepository.saveAndFlush(eip);

        try {
            if (!eip.getProjectId().equals(CommonUtil.getProjectId())) {
                log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), eipid);
                return MethodReturnUtil.error(HttpStatus.SC_FORBIDDEN, ReturnStatus.SC_FORBIDDEN,
                        CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDDEN));
            }

            if (null == fip && instanceType.equals(HsConstants.ECS)) {
//                String networkId = getExtNetId(eip.getRegion());
                if (null == flpnetworkId) {
                    log.error("Failed to get external net in region:{}. ", eip.getRegion());
                    return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ReturnStatus.SC_OPENSTACK_FIP_UNAVAILABLE,
                            CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_OPENSTACK_ERROR));
                }
                floatingIP = neutronService.createAndAssociateWithFip(eip.getRegion(), flpnetworkId, portId, eip, serverId);
                if (null == floatingIP) {
                    log.error("Fatal Error! Can not get floating when bind ip in network:{}, region:{}, portId:{}.",
                            flpnetworkId, eip.getRegion(), portId);
                    return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ReturnStatus.SC_OPENSTACK_FIP_UNAVAILABLE,
                            CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_OPENSTACK_ERROR));
                }
                eip.setFloatingIp(floatingIP.getFloatingIpAddress());
                eip.setFloatingIpId(floatingIP.getId());
            } else {
                eip.setFloatingIp(fip);
            }
            fireWallReturn = providerService.addNatAndQos(eip, eip.getFloatingIp(), eip.getEipAddress(),
                    eip.getBandWidth(), eip.getFirewallId());
            returnMsg = fireWallReturn.getMessage();
            returnStat = fireWallReturn.getInnerCode();
            if (fireWallReturn.getHttpCode() == HttpStatus.SC_OK) {
                boolean bindRet = eipV6DaoService.bindIpv6WithInstance(eip.getEipAddress(), eip.getFloatingIp(), eip.getProjectId());
                if (!bindRet) {
                    providerService.delNatAndQos(eip);
                    neutronService.disassociateAndDeleteFloatingIp(eip.getFloatingIp(),
                            eip.getFloatingIpId(), serverId, eip.getRegion());
                    eip.setFloatingIp(null);
                    eip.setFloatingIpId(null);
                    eip.setStatus(HsConstants.DOWN);
                    return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, returnStat, returnMsg);
                }
                //流量计费类型的eip,从地址簿中删除匹配条件--floating ip
//                if (HsConstants.CHARGE_MODE_TRAFFIC.equalsIgnoreCase(eip.getChargeMode())){
//                    providerService.cmdInsertOrRemoveParamInAddressBook(eip.getEipAddress(),eip.getFloatingIp(),"ip",eip.getFirewallId(),true);
//                }
                eip.setInstanceId(serverId);
                eip.setInstanceType(instanceType);
                eip.setPortId(portId);
                eip.setStatus(HsConstants.ACTIVE);
                eip.setUpdatedTime(CommonUtil.getGmtDate());

                log.info("Bind eip with instance successfully. eip:{}, instance:{}, portId:{}",
                        eip.getEipAddress(), eip.getInstanceId(), eip.getPortId());
                return MethodReturnUtil.success(eip);
            } else {
                neutronService.disassociateAndDeleteFloatingIp(eip.getFloatingIp(),
                        eip.getFloatingIpId(), serverId, eip.getRegion());
                eip.setFloatingIp(null);
                eip.setFloatingIpId(null);
            }
        } catch (Exception e) {
            log.error("band server exception", e);
            returnStat = ReturnStatus.SC_OPENSTACK_SERVER_ERROR;
            returnMsg = e.getMessage();
        } finally {
            if (null == fireWallReturn || fireWallReturn.getHttpCode() != HttpStatus.SC_OK) {
                eip.setStatus(HsConstants.DOWN);
            }
            eipRepository.saveAndFlush(eip);
        }
        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, returnStat, returnMsg);
    }

    @Transactional
    public ActionResponse disassociateInstanceWithEip(Eip eipEntity) {

        String msg = null;

        if (null == eipEntity) {
            log.error("disassociateInstanceWithEip In disassociate process,failed to find the eip ");
            return ActionResponse.actionFailed("Not found.", HttpStatus.SC_NOT_FOUND);
        }
        if (!CommonUtil.isAuthoried(eipEntity.getProjectId())) {
            log.error("User have no write to delete eip:{}", eipEntity.getId());
            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
        }

        if (eipEntity.getStatus().equals(HsConstants.DOWN)) {
            msg = "Error status when disassociate eip:" + eipEntity.toString();
            log.error(msg);
            return ActionResponse.actionSuccess();
        }
        try {
            if (null != eipEntity.getFloatingIp() && null != eipEntity.getInstanceId()) {
                ActionResponse actionResponse = neutronService.disassociateAndDeleteFloatingIp(eipEntity.getFloatingIp(),
                        eipEntity.getFloatingIpId(),
                        eipEntity.getInstanceId(), eipEntity.getRegion());
                if (!actionResponse.isSuccess()) {
                    msg = "Failed to disassociate port with fip:" + eipEntity.toString();
                    log.error(msg);
                    return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
            }

            MethodReturn fireWallReturn = providerService.delNatAndQos(eipEntity);
            if (fireWallReturn.getHttpCode() != HttpStatus.SC_OK) {
                msg += fireWallReturn.getMessage();
                eipEntity.setStatus(HsConstants.ERROR);
            } else {
                eipEntity.setStatus(HsConstants.DOWN);
            }
            eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
            String eipAddress = eipEntity.getEipAddress();
            boolean unbindIpv6Ret = eipV6DaoService.unBindIpv6WithInstance(eipAddress, eipEntity.getProjectId());
            if (!unbindIpv6Ret) {
                neutronService.associaInstanceWithFloatingIp(eipEntity, eipEntity.getInstanceId(), eipEntity.getPortId());
                providerService.addNatAndQos(eipEntity, eipEntity.getFloatingIp(),
                        eipEntity.getEipAddress(), eipEntity.getBandWidth(), eipEntity.getFirewallId());
                msg = "Failed to disassociate  with natPt";
                log.error(msg);
                return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            //从地址簿中删除匹配条件--floating ip
//            if (HsConstants.CHARGE_MODE_TRAFFIC.equalsIgnoreCase(eipEntity.getChargeMode())){
//                //解绑前向bss发送流量统计数据
//                int i = CommonUtil.countMinuteFromPoint();
//                //如果是整点，则以定时任务为准
//                if (i !=0){
//                    flowService.releaseReportFlowAccount(i,eipEntity);
//                }
//                //删除监控集地址本
//                boolean statistics = providerService.cmdOperateStatisticsBook(eipEntity.getEipAddress(), eipEntity.getFirewallId(), false);
//            }
            eipEntity.setInstanceId(null);
            eipEntity.setInstanceType(null);
            eipEntity.setPrivateIpAddress(null);
            eipEntity.setPortId(null);
            eipEntity.setFloatingIp(null);
            eipEntity.setFloatingIpId(null);
            eipRepository.saveAndFlush(eipEntity);
        } catch (Exception e) {
            log.error("Exception  when disassociateInstanceWithEip", e);
            msg += e.getMessage() + "";
            eipRepository.saveAndFlush(eipEntity);
        }
        if (null != msg) {
            return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } else {
            return ActionResponse.actionSuccess();
        }
    }

    @Transactional
    public ActionResponse updateEipEntity(String eipid, EipUpdateParam param, String token) {

        Optional<Eip> optional = eipRepository.findById(eipid);
        if (!optional.isPresent()) {
            log.error("updateEipEntity In disassociate process,failed to find the eip by id:{} ", eipid);
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_NOT_FOND), HttpStatus.SC_NOT_FOUND);
        }
        Eip eipEntity = optional.get();
        if (StringUtils.isNotBlank(eipEntity.getEipV6Id())) {
            log.error("EIP is already bound to eipv6");
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_EIPV6_ERROR), HttpStatus.SC_NOT_FOUND);
        }
        if (!CommonUtil.verifyToken(token, eipEntity.getProjectId())) {
            log.error("User have no write to operate eip:{}", eipid);
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDDEN), HttpStatus.SC_FORBIDDEN);
        }
        if (param.getBillType().equals(HsConstants.MONTHLY) && param.getBandwidth() < eipEntity.getBandWidth()) {
            //can’t sub
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_CHANGE_BANDWIDHT_PREPAID_INCREASE_ERROR),
                    HttpStatus.SC_BAD_REQUEST);
        }
        boolean updateStatus;
        if (null == eipEntity.getPipId() || eipEntity.getPipId().isEmpty()) {
            updateStatus = true;
        } else {
            updateStatus = true;
//                    providerService.updateQosBandWidth(eipEntity.getFirewallId(),
//                    eipEntity.getPipId(), eipEntity.getId(),
//                    String.valueOf(param.getBandwidth()),
//                    eipEntity.getFloatingIp(), eipEntity.getEipAddress());
        }

        if (updateStatus || CommonUtil.qosDebug) {
            eipEntity.setOldBandWidth(eipEntity.getBandWidth());
            eipEntity.setBandWidth(param.getBandwidth());
            eipEntity.setBillType(param.getBillType());
            eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
            eipRepository.saveAndFlush(eipEntity);
            return ActionResponse.actionSuccess();
        } else {
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_CHANGE_BANDWIDTH_ERROR), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
    public ActionResponse reNewEipEntity(String eipId, String addTime) {

        Optional<Eip> optional = eipRepository.findById(eipId);
        if (!optional.isPresent()) {
            return ActionResponse.actionFailed("Can not find the eip by id:{}" + eipId, HttpStatus.SC_NOT_FOUND);
        }
        Eip eipEntity = optional.get();
        if ((null == eipEntity.getSnatId()) && (null == eipEntity.getDnatId()) && (null != eipEntity.getFloatingIp())) {
            MethodReturn fireWallReturn = providerService.addNatAndQos(eipEntity, eipEntity.getFloatingIp(),
                    eipEntity.getEipAddress(), eipEntity.getBandWidth(), eipEntity.getFirewallId());
            if (fireWallReturn.getHttpCode() == HttpStatus.SC_OK) {
                log.info("renew eip entity add nat and qos,{}.  ", eipEntity);
                eipEntity.setStatus(HsConstants.ACTIVE);
                eipEntity.setDuration(addTime);
                eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
                eipRepository.saveAndFlush(eipEntity);
            } else {
                log.error("renew eip error {}", fireWallReturn.getMessage());
                return ActionResponse.actionFailed("firewall error when renew eip", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
        }
        return ActionResponse.actionSuccess();
    }


    @Transactional
    public ActionResponse reNewEipEntity(String eipId, String addTime, String token) {

        Optional<Eip> optional = eipRepository.findById(eipId);
        if (!optional.isPresent()) {
            return ActionResponse.actionFailed("Can not find the eip by id:{}" + eipId, HttpStatus.SC_NOT_FOUND);
        }
        Eip eipEntity = optional.get();
        if (!CommonUtil.verifyToken(token, eipEntity.getProjectId())) {
            log.error("User have no write to renew eip:{}", eipId);
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDDEN), HttpStatus.SC_FORBIDDEN);
        }

        if ((null == eipEntity.getSnatId()) && (null == eipEntity.getDnatId()) && (null != eipEntity.getFloatingIp())) {
            MethodReturn fireWallReturn = providerService.addNatAndQos(eipEntity, eipEntity.getFloatingIp(),
                    eipEntity.getEipAddress(), eipEntity.getBandWidth(), eipEntity.getFirewallId());
            if (fireWallReturn.getHttpCode() == HttpStatus.SC_OK) {
                log.info("renew eip entity add nat and qos,{}.  ", eipEntity);
                eipEntity.setStatus(HsConstants.ACTIVE);
                eipEntity.setDuration(addTime);
                eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
                eipRepository.saveAndFlush(eipEntity);
            } else {
                log.error("renew eip error {}", fireWallReturn.getMessage());
                return ActionResponse.actionFailed("firewall error when renew eip", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
        }
        return ActionResponse.actionSuccess();
    }

    public List<Eip> findByProjectId(String projectId) {
        return eipRepository.findByProjectIdAndIsDelete(projectId, 0);
    }

    public Eip findByEipAddress(String eipAddr) throws KeycloakTokenException {
        return eipRepository.findByEipAddressAndProjectIdAndIsDelete(eipAddr, CommonUtil.getProjectId(), 0);
    }

    public Eip findByInstanceId(String instanceId) {
        return eipRepository.findByInstanceIdAndIsDelete(instanceId, 0);
    }

    @Transactional
    public Eip getEipById(String id) {

        Eip eipEntity = null;
        Optional<Eip> eip = eipRepository.findById(id);
        if (eip.isPresent()) {
            eipEntity = eip.get();
        }

        return eipEntity;
    }


    public long getInstanceNum(String projectId) {

        String sql = "select count(1) as num from eip where project_id='" + projectId + "'" + "and is_delete=0";

        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        long num = (long) map.get("num");
        log.debug("{}, result:{}", sql, num);


        return num;

    }


    public int getFreeEipCount() {

        String sql = "select count(*) as num from eip_pool";

        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        long num = (long) map.get("num");
        log.debug("{}, result:{}", sql, num);


        return (int) num;

    }


    public int getUsingEipCount() {

        String sql = "select count(*) as num from eip where is_delete=0";

        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        long num = (long) map.get("num");
        log.debug("{}, result:{}", sql, num);


        return (int) num;

    }

    public int getTotalBandWidth() {
        String sql = "select sum(band_width) as sum from eip where is_delete=0 and charge_mode='Bandwidth'";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);

        String sbwSql = "select sum(band_width) as sbwsum from sbw where is_delete=0";
        Map<String, Object> sbwMap = jdbcTemplate.queryForMap(sbwSql);
        int bandWidth = Integer.parseInt(map.get("sum").toString());
        int sbwBandWidth = Integer.parseInt(sbwMap.get("sbwsum").toString());
        log.info("sbw band width:{}, eip band width:{}", sbwBandWidth, bandWidth);
        return bandWidth + sbwBandWidth;

    }

    public int getUsingEipCountByStatus(String status) {

        String sql = "select count(*) as num from eip where status='" + status + "'" + "and is_delete=0";

        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        long num = (long) map.get("num");
        log.debug("{}, result:{}", sql, num);


        return (int) num;

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized EipPool getOneEipFromPool(String type) {
        if(type == null){
            type="BGP";
        }
        EipPool eipAddress = eipPoolRepository.getEipByRandom(type);
        if (null != eipAddress) {
            eipPoolRepository.deleteById(eipAddress.getId());
            eipPoolRepository.flush();
        }
        return eipAddress;
    }


    private String getExtNetId(String region) {
        List<ExtNet> extNets = extNetRepository.findByRegion(region);
        String extNetId = null;
        for (ExtNet extNet : extNets) {
            if (null != extNet.getNetId()) {
                extNetId = extNet.getNetId();
            }
        }
        return extNetId;
    }


    public Map<String, Object> getDuplicateEip() {

        String sql = "select eip_address, count(*) as num from eip group by eip_address having num>1";


        Map<String, Object> map = jdbcTemplate.queryForMap(sql);

        log.info("{}", map);

        return map;

    }

    public Map<String, Object> getDuplicateEipFromPool() {

        String sql = "select ip, count(*) as num from eip_pool group by ip having num>1";

        Map<String, Object> map = jdbcTemplate.queryForMap(sql);

        log.info("{}, result:{}", sql, map);

        return map;

    }


    public Eip get(String instanceId) {
        return eipRepository.findByInstanceIdAndIsDelete(instanceId, 0);
    }

    public int statisEipCountBySbw(String sbwId, int isDelete) {
        return (int) eipRepository.countBySbwIdAndIsDelete(sbwId, 0);
    }

    @Transactional
    public List<Eip> findFlowAccountEipList(String chargeMode){
        return eipRepository.findByChargeModeAndIsDelete(chargeMode, 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public MethodReturn associateInstanceWithEipGroup(String groupId, String serverId, String instanceType, String portId, String fip) {
        NetFloatingIP floatingIP;
        String returnStat = null;
        String returnMsg = null;
        MethodReturn fireWallReturn = null;
        String flag = "failed";

        List<Eip> eipList = eipRepository.findByGroupIdAndIsDelete(groupId, 0);
        if (eipList.isEmpty()) {
            log.error("In associate process, failed to find the eip by groupId:{} ", groupId);
            return MethodReturnUtil.error(HttpStatus.SC_NOT_FOUND, ReturnStatus.SC_NOT_FOUND,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_NOT_FOND));
        }
        for (Eip eip : eipList) {

            if (!(HsConstants.DOWN.equals(eip.getStatus())) || (null != eip.getDnatId()) || (null != eip.getSnatId())) {
                return MethodReturnUtil.error(HttpStatus.SC_BAD_REQUEST, ReturnStatus.EIP_BIND_HAS_BAND,
                        CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_HAS_BAND));
            }
            eip.setStatus(HsConstants.BINDING);
            eipRepository.saveAndFlush(eip);

            try {
                if (!eip.getProjectId().equals(CommonUtil.getProjectId())) {
                    log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), eip.getId());
                    return MethodReturnUtil.error(HttpStatus.SC_FORBIDDEN, ReturnStatus.SC_FORBIDDEN,
                            CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDDEN));
                }

                if (null == fip && instanceType.equals(HsConstants.ECS)) {
//                String networkId = getExtNetId(eip.getRegion());
                    if (null == flpnetworkId) {
                        log.error("Failed to get external net in region:{}. ", eip.getRegion());
                        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ReturnStatus.SC_OPENSTACK_FIP_UNAVAILABLE,
                                CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_OPENSTACK_ERROR));
                    }
                    floatingIP = neutronService.createAndAssociateWithFip(eip.getRegion(), flpnetworkId, portId, eip, serverId);
                    if (null == floatingIP) {
                        log.error("Fatal Error! Can not get floating when bind ip in network:{}, region:{}, portId:{}.",
                                flpnetworkId, eip.getRegion(), portId);
                        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ReturnStatus.SC_OPENSTACK_FIP_UNAVAILABLE,
                                CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_OPENSTACK_ERROR));
                    }
                    eip.setFloatingIp(floatingIP.getFloatingIpAddress());
                    eip.setFloatingIpId(floatingIP.getId());
                } else {
                    eip.setFloatingIp(fip);
                }
                fireWallReturn = providerService.addNatAndQos(eip, eip.getFloatingIp(), eip.getEipAddress(),
                        eip.getBandWidth(), eip.getFirewallId());
                returnMsg = fireWallReturn.getMessage();
                returnStat = fireWallReturn.getInnerCode();
                if (fireWallReturn.getHttpCode() == HttpStatus.SC_OK) {
                    boolean bindRet = eipV6DaoService.bindIpv6WithInstance(eip.getEipAddress(), eip.getFloatingIp(), eip.getProjectId());
                    if (!bindRet) {
                        providerService.delNatAndQos(eip);
                        neutronService.disassociateAndDeleteFloatingIp(eip.getFloatingIp(),
                                eip.getFloatingIpId(), serverId, eip.getRegion());
                        eip.setFloatingIp(null);
                        eip.setFloatingIpId(null);
                        eip.setStatus(HsConstants.DOWN);
                        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, returnStat, returnMsg);
                    }
                    //流量计费类型的eip,从地址簿中删除匹配条件--floating ip
//                if (HsConstants.CHARGE_MODE_TRAFFIC.equalsIgnoreCase(eip.getChargeMode())){
//                    providerService.cmdInsertOrRemoveParamInAddressBook(eip.getEipAddress(),eip.getFloatingIp(),"ip",eip.getFirewallId(),true);
//                }
                    eip.setInstanceId(serverId);
                    eip.setInstanceType(instanceType);
                    eip.setPortId(portId);
                    eip.setStatus(HsConstants.ACTIVE);
                    eip.setUpdatedTime(CommonUtil.getGmtDate());

                    log.info("Bind eip with instance successfully. eip:{}, instance:{}, portId:{}",
                            eip.getEipAddress(), eip.getInstanceId(), eip.getPortId());
                    flag = "success";

                } else {
                    neutronService.disassociateAndDeleteFloatingIp(eip.getFloatingIp(),
                            eip.getFloatingIpId(), serverId, eip.getRegion());
                    eip.setFloatingIp(null);
                    eip.setFloatingIpId(null);
                }
            } catch (Exception e) {
                log.error("band server exception", e);
                returnStat = ReturnStatus.SC_OPENSTACK_SERVER_ERROR;
                returnMsg = e.getMessage();
            } finally {
                if (null == fireWallReturn || fireWallReturn.getHttpCode() != HttpStatus.SC_OK) {
                    eip.setStatus(HsConstants.DOWN);
                }
                eipRepository.saveAndFlush(eip);
            }
        }
        if(flag.equals("success")){
            return MethodReturnUtil.success();
        }

        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, returnStat, returnMsg);
    }

    @Transactional
    public ActionResponse disassociateInstanceWithEipGroup(Eip eipEntity) {

        String msg = null;

        if (null == eipEntity) {
            log.error("disassociateInstanceWithEip In disassociate process,failed to find the eip ");
            return ActionResponse.actionFailed("Not found.", HttpStatus.SC_NOT_FOUND);
        }
        if (!CommonUtil.isAuthoried(eipEntity.getProjectId())) {
            log.error("User have no write to delete eip:{}", eipEntity.getId());
            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
        }

        if (eipEntity.getStatus().equals(HsConstants.DOWN)) {
            msg = "Error status when disassociate eip:" + eipEntity.toString();
            log.error(msg);
            return ActionResponse.actionSuccess();
        }
        try {
            if (null != eipEntity.getFloatingIp() && null != eipEntity.getInstanceId()) {
                ActionResponse actionResponse = neutronService.disassociateAndDeleteFloatingIp(eipEntity.getFloatingIp(),
                        eipEntity.getFloatingIpId(),
                        eipEntity.getInstanceId(), eipEntity.getRegion());
                if (!actionResponse.isSuccess()) {
                    msg = "Failed to disassociate port with fip:" + eipEntity.toString();
                    log.error(msg);
                    return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
            }

            MethodReturn fireWallReturn = providerService.delNatAndQos(eipEntity);
            if (fireWallReturn.getHttpCode() != HttpStatus.SC_OK) {
                msg += fireWallReturn.getMessage();
                eipEntity.setStatus(HsConstants.ERROR);
            } else {
                eipEntity.setStatus(HsConstants.DOWN);
            }
            eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
            String eipAddress = eipEntity.getEipAddress();
            boolean unbindIpv6Ret = eipV6DaoService.unBindIpv6WithInstance(eipAddress, eipEntity.getProjectId());
            if (!unbindIpv6Ret) {
                neutronService.associaInstanceWithFloatingIp(eipEntity, eipEntity.getInstanceId(), eipEntity.getPortId());
                providerService.addNatAndQos(eipEntity, eipEntity.getFloatingIp(),
                        eipEntity.getEipAddress(), eipEntity.getBandWidth(), eipEntity.getFirewallId());
                msg = "Failed to disassociate  with natPt";
                log.error(msg);
                return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            //从地址簿中删除匹配条件--floating ip
//            if (HsConstants.CHARGE_MODE_TRAFFIC.equalsIgnoreCase(eipEntity.getChargeMode())){
//                //解绑前向bss发送流量统计数据
//                int i = CommonUtil.countMinuteFromPoint();
//                //如果是整点，则以定时任务为准
//                if (i !=0){
//                    flowService.releaseReportFlowAccount(i,eipEntity);
//                }
//                //删除监控集地址本
//                boolean statistics = providerService.cmdOperateStatisticsBook(eipEntity.getEipAddress(), eipEntity.getFirewallId(), false);
//            }
            eipEntity.setInstanceId(null);
            eipEntity.setInstanceType(null);
            eipEntity.setPrivateIpAddress(null);
            eipEntity.setPortId(null);
            eipEntity.setFloatingIp(null);
            eipEntity.setFloatingIpId(null);
            eipRepository.saveAndFlush(eipEntity);
        } catch (Exception e) {
            log.error("Exception  when disassociateInstanceWithEip", e);
            msg += e.getMessage() + "";
            eipRepository.saveAndFlush(eipEntity);
        }
        if (null != msg) {
            return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } else {
            return ActionResponse.actionSuccess();
        }
    }

    @Transactional
    public List<Eip> getEipListByGroupId(String groupId) {

        Eip eipEntity = null;
        List<Eip> eipList = eipRepository.findByGroupIdAndIsDelete(groupId,0);
        return eipList;
    }

    public List<Eip> findByInstanceIdAndIsDelete(String instanceId) {

        String sql = "select * from eip where instance_id='" + instanceId + "'" + "and is_delete=0";

        RowMapper<Eip> rm = BeanPropertyRowMapper.newInstance(Eip.class);
        List<Eip> listEips = jdbcTemplate.query(sql, rm);

        return listEips;

    }

}
