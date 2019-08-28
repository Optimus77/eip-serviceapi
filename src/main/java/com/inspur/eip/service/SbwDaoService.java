package com.inspur.eip.service;

import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.*;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.common.ValidatorUtil;
import com.inspur.eip.util.constant.ConstantClassField;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HsConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class SbwDaoService {

    @Value("${firewall.id}")
    private String firewallId;

    @Autowired
    private SbwRepository sbwRepository;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private IDevProvider firewallService;

    @Autowired
    private QosService qosService;


    public List<Sbw> findByProjectId(String projectId) {
        return sbwRepository.findByProjectIdAndIsDelete(projectId, 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized Sbw allocateSbw(SbwUpdateParam sbwConfig, String token) throws EipInternalServerException {
        try {
            //sbw instance id
            String sbwId = CommonUtil.getUUID();
            if (!ValidatorUtil.isDOT_STANDARD_STR(sbwConfig.getSbwName())) {
                log.warn(ErrorStatus.VALIADATE_NAME_ERROR.getMessage() + ":{}", sbwConfig.getSbwName());
                throw new EipBadRequestException(ErrorStatus.VALIADATE_NAME_ERROR.getCode(), ErrorStatus.VALIADATE_NAME_ERROR.getMessage());
            }
            if (StringUtils.isNotBlank(sbwConfig.getDescription()) && !ValidatorUtil.isDESCRIPTION_STANDARD_STR(sbwConfig.getDescription())){
                log.warn(ErrorStatus.VALIADATE_NAME_ERROR.getMessage() + ":{}", sbwConfig.getDescription());
                throw new EipBadRequestException(ErrorStatus.VALIADATE_NAME_ERROR.getCode(), ErrorStatus.VALIADATE_NAME_ERROR.getMessage());
            }
            // todo 替换成 radware
            Boolean qosResult = firewallService.cmdAddSbwQos(sbwId, String.valueOf(sbwConfig.getBandwidth()), firewallId);
            // 防火墙qos添加成功
            if (qosResult) {
                Sbw sbw = Sbw.builder().id(sbwId)
                        .sbwName(sbwConfig.getSbwName())
                        .billType(sbwConfig.getBillType())
                        .duration(sbwConfig.getDuration())
                        .bandWidth(sbwConfig.getBandwidth())
                        .pipeId(sbwId)              //此处由管道id更替为sbwId
                        .region(sbwConfig.getRegion())
                        .createdTime(CommonUtil.getGmtDate())
                        .updatedTime(CommonUtil.getGmtDate())
                        .ipType(sbwConfig.getIpType())
                        .projectId(CommonUtil.getProjectId(token))
                        .isDelete(0)
                        .status(HsConstants.ACTIVE)
                        .userName(CommonUtil.getUsername(token))
                        .userId(CommonUtil.getUserId(token))
                        .description(sbwConfig.getDescription())
                        .build();
                sbw = sbwRepository.saveAndFlush(sbw);
                return sbw;
            } else {
                //防火墙qos 添加失败，回滚处理
                log.error("Create sbw error-" + ConstantClassField.FIREWALL_QOS_ADD_ERROR);
                throw new EipInternalServerException(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getCode(), ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getMessage());
            }
        } catch (Exception e) {
            log.error("Create Sbw Exception in add qos:{} ", e);
        }
        return null;
    }

    @Transactional
    public Sbw getSbwById(String sbwId) {
        Optional<Sbw> sbw = sbwRepository.findById(sbwId);
        if (sbw.isPresent()) {
            return sbw.get();
        } else {
            log.error("Faild to find sbw by id:" + sbwId);
            return null;
        }
    }

    /**
     * delete
     *
     * @param sbwId id
     * @return ret
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse deleteSbw(String sbwId, String token) throws EipInternalServerException {
        String msg;
        long ipCount;
        Optional<Sbw> optional = sbwRepository.findById(sbwId);
        if (optional.isPresent()) {
            Sbw sbwBean = optional.get();
            if (sbwBean.getIsDelete() == 1) {
                log.error("Faild to find sbw by id:{}" + sbwId);
                return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);
            }
            if (!CommonUtil.verifyToken(token, sbwBean.getProjectId())) {
                log.error(CodeInfo.getCodeMessage(CodeInfo.SBW_FORBIDDEN), sbwId);
                return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
            }
            ipCount = eipRepository.countBySbwIdAndIsDelete(sbwBean.getId(), 0);
            if (ipCount > 0) {
                msg = "EIP in sbw so that sbw cannot be removed ，please remove first !,ipCount:{}" + ipCount;
                log.error(msg);
                return ActionResponse.actionFailed(msg, HttpStatus.SC_FORBIDDEN);
            }
            // 防火墙qos存在，删除防火墙qos
            if (StringUtils.isNotBlank(sbwBean.getPipeId())) {
                //根据qos名称删除qos,pipeId即是sbwId
                boolean delQos = firewallService.cmdDelSbwQos(sbwBean.getId(), firewallId);
                if (delQos) {
                    sbwBean.setIsDelete(1);
                    sbwBean.setStatus(HsConstants.DELETE);
                    sbwBean.setUpdatedTime(CommonUtil.getGmtDate());
                    sbwRepository.saveAndFlush(sbwBean);
                    log.info("user delete sbw qos success, qosName:{}", sbwId);
                    return ActionResponse.actionSuccess();
                } else {
                    //防火墙qos删除失败，
                    log.error("delete qos" + ConstantClassField.FIREWALL_QOS_DELETE_ERROR);
                    throw new EipInternalServerException(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getCode(), ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getMessage());
                }
            } else {
                //防火墙qos不存在，可能是异常数据
                sbwBean.setIsDelete(1);
                sbwBean.setStatus(HsConstants.DELETE);
                sbwBean.setUpdatedTime(CommonUtil.getGmtDate());
                sbwRepository.saveAndFlush(sbwBean);
                log.info("Atom user delete sbw successfully, id:{}", sbwId);
                return ActionResponse.actionSuccess();
            }
        }
        log.error("Faild to find sbw by id:" + sbwId);
        return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);

    }

    /**
     * mq have no token
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse adminDeleteSbw(String sbwId) throws EipInternalServerException {
        long ipCount;
        Optional<Sbw> optional = sbwRepository.findById(sbwId);
        if (optional.isPresent()) {
            Sbw sbwBean = optional.get();
            if (sbwBean.getIsDelete() == 1) {
                log.warn(ErrorStatus.ENTITY_NOT_FOND_IN_DB + sbwId);
                return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);
            }
            //此处如果该共享带宽中有其他EIP  则删除失败，需要用户先手动去移出共享带宽中eip
            ipCount = eipRepository.countBySbwIdAndIsDelete(sbwBean.getId(), 0);
            if (ipCount > 0) {
                log.error(ErrorStatus.EIP_IN_SBW_SO_THAT_CAN_NOT_DELETE.getMessage(), HttpStatus.SC_BAD_REQUEST);
                return ActionResponse.actionFailed(ErrorStatus.EIP_IN_SBW_SO_THAT_CAN_NOT_DELETE.getMessage(), HttpStatus.SC_FORBIDDEN);
            }
            if (StringUtils.isNotBlank(sbwBean.getPipeId())) {
                boolean delQos = firewallService.delQos(sbwBean.getId(), null, null, firewallId);
                if (delQos) {
                    sbwBean.setIsDelete(1);
                    sbwBean.setStatus(HsConstants.DELETE);
                    sbwBean.setUpdatedTime(CommonUtil.getGmtDate());
                    sbwRepository.saveAndFlush(sbwBean);
                    log.info("admin delete sbw success, qosName:{}", sbwId);
                    return ActionResponse.actionSuccess();
                } else {
                    //qos
                    log.error("delete qos" + ConstantClassField.FIREWALL_QOS_DELETE_ERROR);
                    throw new EipInternalServerException(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getCode(), ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getMessage());
                }
            } else {
                sbwBean.setIsDelete(1);
                sbwBean.setStatus(HsConstants.DELETE);
                sbwBean.setUpdatedTime(CommonUtil.getGmtDate());
                sbwRepository.saveAndFlush(sbwBean);
                return ActionResponse.actionSuccess();
            }
        }
        log.warn(ErrorStatus.ENTITY_NOT_FOND_IN_DB + sbwId);
        return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    public ActionResponse stopSbwService(String sbwId) {
        Optional<Sbw> optional = sbwRepository.findById(sbwId);

        if (!optional.isPresent()) {
            log.error(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage() + sbwId);
            return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);
        }
        Sbw sbw = optional.get();
        // 订单测主动发起MQ，无token
//        if (!CommonUtil.isAuthoried(sbw.getProjectId())) {
//            log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), id);
//            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
//        }
        if (StringUtils.isNotEmpty(sbw.getStatus()) && HsConstants.ACTIVE.equalsIgnoreCase(sbw.getStatus())) {
            boolean stop = qosService.controlPipe(firewallId, sbwId, true);
            if (stop) {
                sbw.setUpdatedTime(CommonUtil.getGmtDate());
                sbw.setStatus(HsConstants.STOP);
                sbwRepository.saveAndFlush(sbw);
                return ActionResponse.actionSuccess();
            } else {
                return ActionResponse.actionFailed(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getMessage(), Integer.parseInt(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getCode()));
            }
            //已经是禁用状态|异常数据没有状态值
        } else {
            sbw.setUpdatedTime(CommonUtil.getGmtDate());
            sbw.setStatus(HsConstants.STOP);
            sbwRepository.saveAndFlush(sbw);
            return ActionResponse.actionSuccess();
        }
    }

    /**
     * 共享带宽续费操作，仅包年包月共享带宽支持，续费操作不受实例状态限制
     *
     * @param sbwId
     * @param token
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse renewSbwInfo(String sbwId, String token) {
        String msg;
        Optional<Sbw> optional = sbwRepository.findById(sbwId);
        if (!optional.isPresent()) {
            msg = "Faild to find in restart by id:{}" + sbwId;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        Sbw sbw = optional.get();
        if (!CommonUtil.verifyToken(token, sbw.getProjectId())) {
            log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), sbwId);
            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
        }
        if (!sbw.getBillType().equals(HsConstants.MONTHLY)) {
            msg = "BillType is not monthly SBW cannot be renewed:{}" + sbwId;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
        }
        if (StringUtils.isNotEmpty(sbw.getStatus()) && HsConstants.STOP.equalsIgnoreCase(sbw.getStatus())) {
            if (StringUtils.isNotEmpty(sbw.getPipeId())) {
                boolean enable = qosService.controlPipe(firewallId, sbwId, false);
                if (enable) {
                    sbw.setUpdatedTime(CommonUtil.getGmtDate());
                    sbw.setStatus(HsConstants.ACTIVE);
                    sbwRepository.saveAndFlush(sbw);
                    return ActionResponse.actionSuccess();
                } else {
                    return ActionResponse.actionFailed(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getMessage(), Integer.parseInt(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getCode()));
                }
            }
        } else {
            sbw.setUpdatedTime(CommonUtil.getGmtDate());
            sbw.setStatus(HsConstants.ACTIVE);
            sbwRepository.saveAndFlush(sbw);
            return ActionResponse.actionSuccess();
        }
        return ActionResponse.actionFailed(ErrorStatus.SC_INTERNAL_SERVER_ERROR.getMessage(), Integer.parseInt(ErrorStatus.SC_INTERNAL_SERVER_ERROR.getCode()));
    }

    /**
     * 停服重开
     *
     * @param sbwId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse resumeSbwInfo(String sbwId) {
        String msg;
        Optional<Sbw> optional = sbwRepository.findById(sbwId);
        if (!optional.isPresent()) {
            msg = "Faild to find in resume by id:{}" + sbwId;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        Sbw sbw = optional.get();
        if (!sbw.getBillType().equals(HsConstants.MONTHLY)) {
            msg = "BillType is not monthly SBW cannot be renewed:{}" + sbwId;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
        }
//        Firewall firewall = firewallRepository.findFirewallByRegion(sbw.getRegion());
//        if (firewall == null) {
//            msg = "Can't find firewall by sbw region:{}" + sbw.getRegion();
//            log.error(msg);
//            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
//        }
        if (StringUtils.isNotEmpty(sbw.getStatus()) && HsConstants.STOP.equalsIgnoreCase(sbw.getStatus())) {
            boolean resume = qosService.controlPipe(firewallId, sbwId, false);
            if (resume) {
                sbw.setUpdatedTime(CommonUtil.getGmtDate());
                sbw.setStatus(HsConstants.ACTIVE);
                sbwRepository.saveAndFlush(sbw);
                return ActionResponse.actionSuccess();
            } else {
                return ActionResponse.actionFailed(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getMessage(), Integer.parseInt(ErrorStatus.SC_FIREWALL_QOS_UNAVAILABLE.getCode()));
            }
        } else {
            sbw.setUpdatedTime(CommonUtil.getGmtDate());
            sbw.setStatus(HsConstants.ACTIVE);
            sbwRepository.saveAndFlush(sbw);
            return ActionResponse.actionSuccess();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Sbw renameSbw(String sbwId, SbwUpdateParam param) {
        if (StringUtils.isNotBlank(param.getSbwName())){
            if (!ValidatorUtil.isDOT_STANDARD_STR(param.getSbwName())) {
                log.warn(ErrorStatus.VALIADATE_NAME_ERROR.getMessage() + ":{}", param.getSbwName());
                throw new EipBadRequestException(ErrorStatus.VALIADATE_NAME_ERROR.getCode(), ErrorStatus.VALIADATE_NAME_ERROR.getMessage());
            }
        }
        if (StringUtils.isNotBlank(param.getDescription())){
            if (StringUtils.isNotBlank(param.getDescription()) && !ValidatorUtil.isDESCRIPTION_STANDARD_STR(param.getDescription())){
                log.warn(ErrorStatus.VALIADATE_NAME_ERROR.getMessage() + ":{}", param.getDescription());
                throw new EipBadRequestException(ErrorStatus.VALIADATE_NAME_ERROR.getCode(), ErrorStatus.VALIADATE_NAME_ERROR.getMessage());
            }
        }
        Sbw sbw = null;
        try {
            Optional<Sbw> optional = sbwRepository.findById(sbwId);
            if (optional.isPresent()) {
                sbw = optional.get();
                if (sbw.getIsDelete() == 1) {
                    log.warn("In rename sbw process,failed to find the sbw by id:{} ", sbwId);
                    throw new EipNotFoundException(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getCode(), ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage());
                }
                if (!CommonUtil.isAuthoried(sbw.getProjectId())) {
                    log.warn("User have no write to operate sbw:{}", sbwId);
                    throw new EipUnauthorizedException(HttpStatus.SC_FORBIDDEN, ErrorStatus.SC_FORBIDDEN.getCode(), ErrorStatus.SC_FORBIDDEN.getMessage(), CommonUtil.getProjectId());
                }
                if (StringUtils.isNotBlank(param.getSbwName())){
                    sbw.setSbwName(param.getSbwName());
                }
                if (StringUtils.isNotBlank(param.getDescription())){
                    sbw.setDescription(param.getDescription());
                }
                sbw.setUpdatedTime(CommonUtil.getGmtDate());
                sbwRepository.saveAndFlush(sbw);
                log.info(ConstantClassField.UPDATE_SBW_CONFIG_SUCCESS + ":{}", sbw.toString());
            } else {
                log.warn("In rename sbw process,failed to find the sbw by id:{} ", sbwId);
                throw new EipNotFoundException(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getCode(), ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage());
            }

        } catch (KeycloakTokenException e) {
            log.error(ConstantClassField.EXCEPTION_SBW_RENAEM, e.getMessage());
        }
        return sbw;
    }

    /**
     * 按需的共享带宽-> 支持可大可小
     * 包年包月的共享带宽 -> 只能调大不能调小
     *
     * @param sbwId
     * @param param
     * @param token
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse updateSbwEntity(String sbwId, SbwUpdateParam param, String token) {

        Optional<Sbw> optional = sbwRepository.findById(sbwId);
        if (!optional.isPresent()) {
            log.error("In update sbw bandWidth  process,failed to find the sbw by id:{} ", sbwId);
            return ActionResponse.actionFailed(ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
        Sbw sbwEntity = optional.get();
        if (!CommonUtil.verifyToken(token, sbwEntity.getProjectId())) {
            log.error("User  not have permission to update sbw bandWidth id:{}", sbwId);
            return ActionResponse.actionFailed(ErrorStatus.SC_FORBIDDEN.getMessage(), HttpStatus.SC_FORBIDDEN);
        }
        if (sbwEntity.getBillType().equals(HsConstants.MONTHLY) && param.getBandwidth() < sbwEntity.getBandWidth()) {
            //can’t  modify
            return ActionResponse.actionFailed(ErrorStatus.BILL_TYPE_NOT_CORRECT.getMessage(), HttpStatus.SC_NOT_ACCEPTABLE);
        }
        if (sbwEntity.getPipeId() == null) {
            sbwEntity.setBandWidth(param.getBandwidth());
            sbwEntity.setUpdatedTime(CommonUtil.getGmtDate());
            sbwRepository.saveAndFlush(sbwEntity);
            return ActionResponse.actionSuccess();
        }
        boolean updateStatus = true;
        //todo radware 暂不支持
//        firewallService.updateQosBandWidth(firewallId, sbwEntity.getPipeId(), sbwEntity.getId(), String.valueOf(param.getBandwidth()), null, null);
        if (updateStatus || CommonUtil.qosDebug) {
            sbwEntity.setBandWidth(param.getBandwidth());
            sbwEntity.setUpdatedTime(CommonUtil.getGmtDate());
            sbwRepository.saveAndFlush(sbwEntity);

            Stream<Eip> stream = eipRepository.findByProjectIdAndIsDeleteAndSbwId(sbwEntity.getProjectId(), 0, sbwId).stream();
            stream.forEach(eip -> {
                eip.setBandWidth(param.getBandwidth());
                eip.setUpdatedTime(CommonUtil.getGmtDate());
                eipRepository.saveAndFlush(eip);
            });
            log.info("update sbw qos bandwidth  and eip bandwidth success, id:{}", sbwId);
            return ActionResponse.actionSuccess();
        }
        return ActionResponse.actionFailed(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * 往共享带宽中添加弹性eip，仅按需的eip支持加入到共享带宽
     *
     * @param eipId
     * @param eipUpdateParam
     * @param token
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse addEipIntoSbw(String eipId, EipUpdateParam eipUpdateParam, String token) {

        String pipeId;
        String sbwId = eipUpdateParam.getSbwId();
        /*check sbwId request param*/
        if (StringUtils.isBlank(sbwId)) {
            log.error("param have no sbwId,Eip add to sbw must transmit:{} ", sbwId);
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.SBW_NOT_FOND_BY_ID), HttpStatus.SC_BAD_REQUEST);
        }
        Optional<Eip> optionalEip = eipRepository.findById(eipId);
        if (!optionalEip.isPresent()) {
            log.error("In addEipIntoSbw process,failed to find the eip by id:{} ", eipId);
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_NOT_FOND), HttpStatus.SC_NOT_FOUND);
        }
        Eip eipEntity = optionalEip.get();
        if (StringUtils.isNotBlank(eipEntity.getEipV6Id())) {
            log.error("EIP is already bound to eipv6");
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_EIPV6_ERROR), HttpStatus.SC_NOT_FOUND);
        }
        if (!CommonUtil.verifyToken(token, eipEntity.getProjectId())) {
            log.error("User have no write to operate eip:{}", eipId);
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDDEN), HttpStatus.SC_FORBIDDEN);
        }
        //1.ensure eip is billed on hourlySettlement
        if (eipEntity.getBillType().equals(HsConstants.MONTHLY)) {
            log.error("The eip billType isn't hourlySettment!", eipEntity.getBillType());
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_BILLTYPE_NOT_HOURLYSETTLEMENT), HttpStatus.SC_BAD_REQUEST);
        }
        //3.check eip had not adding any Shared bandWidth
        if (StringUtils.isNotBlank(eipEntity.getSbwId())) {
            log.error("The shared band id not null, this mean the eip had already added other SBW !", eipEntity.getSbwId());
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.EIP_SHARED_BAND_WIDTH_ID_NOT_NULL), HttpStatus.SC_BAD_REQUEST);
        }
        Optional<Sbw> sbwOptional = sbwRepository.findById(sbwId);
        if (!sbwOptional.isPresent()) {
            log.error("Failed to find sbw by id:{} ", sbwId);
            return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.SBW_NOT_FOND_BY_ID), HttpStatus.SC_NOT_FOUND);
        }
        Sbw sbwEntity = sbwOptional.get();
        boolean updateStatus = true;
        if (eipEntity.getStatus().equalsIgnoreCase(HsConstants.ACTIVE)) {
            if (eipUpdateParam.getBandwidth() != sbwEntity.getBandWidth()) {
                return ActionResponse.actionFailed(CodeInfo.getCodeMessage(CodeInfo.SBW_THE_NEW_BANDWIDTH_VALUE_ERROR), HttpStatus.SC_NOT_FOUND);
            }
            pipeId = HsConstants.UUID_LENGTH;
            // firewallService.addFipToSbwQos(eipEntity.getFirewallId(), eipEntity.getFloatingIp(), sbwEntity.getId());
            if (null != pipeId) {
                updateStatus = true;
//                firewallService.delQos(eipEntity.getPipId(), eipEntity.getEipAddress(), eipEntity.getFloatingIp(), eipEntity.getFirewallId());
                if (StringUtils.isBlank(sbwEntity.getPipeId())) {
                    sbwEntity.setPipeId(pipeId);
                }
            } else {
                updateStatus = false;
            }
        }
        if (updateStatus || CommonUtil.qosDebug) {
            //先更新sbw中pipeId
            sbwEntity.setUpdatedTime(CommonUtil.getGmtDate());
            sbwRepository.saveAndFlush(sbwEntity);

            eipEntity.setPipId(sbwEntity.getPipeId());
            eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
            eipEntity.setSbwId(sbwId);
            eipEntity.setOldBandWidth(eipEntity.getBandWidth());
            eipEntity.setChargeMode(HsConstants.SHAREDBANDWIDTH);
            eipEntity.setBandWidth(eipUpdateParam.getBandwidth());
            eipRepository.saveAndFlush(eipEntity);

            log.info("Eip add to sbw success.:{}", eipEntity);
            return ActionResponse.actionSuccess();
        }
        log.error("Failed to add ip in sbw,eip:{},sbwId:{}" + eipEntity + sbwId);
        return ActionResponse.actionFailed(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }

    /**
     * 从共享带宽中移除eip
     *
     * @param eipid
     * @param eipUpdateParam
     * @param token
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionResponse removeEipFromSbw(String eipid, EipUpdateParam eipUpdateParam, String token) {
        Optional<Eip> optionalEip = eipRepository.findById(eipid);
        String msg;
        String sbwId = eipUpdateParam.getSbwId();
        /*check sbwId request param*/
        if (StringUtils.isBlank(sbwId)) {
            log.error("param have no sbwId,Eip add to sbw must transmit:{} ", sbwId);
            return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
        if (!optionalEip.isPresent()) {
            log.error("In remove Eip From Sbw process,failed to find the eip by id:{} ", eipid);
            return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);
        }
        Eip eipEntity = optionalEip.get();
        if (!CommonUtil.verifyToken(token, eipEntity.getProjectId())) {
            log.error("User have no write to delete eip:{}", eipid);
            return ActionResponse.actionFailed(ErrorStatus.SC_FORBIDDEN.getMessage(), HttpStatus.SC_FORBIDDEN);
        }
        Optional<Sbw> optionalSbw = sbwRepository.findById(sbwId);
        if (!optionalSbw.isPresent()) {
            log.error("In remove Eip From Sbw process,failed to find sbw by id:{} ", sbwId);
            return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);
        }
        Sbw sbw = optionalSbw.get();

        boolean removeStatus = true;
        String newPipId = null;
        if (eipEntity.getStatus().equalsIgnoreCase(HsConstants.ACTIVE)) {
            if (eipUpdateParam.getBandwidth() != eipEntity.getOldBandWidth()) {
                log.error(ErrorStatus.SC_PARAM_ERROR.getMessage() + "bandwidth:{}", eipUpdateParam.getBandwidth());
                return ActionResponse.actionFailed(ErrorStatus.SC_PARAM_ERROR.getMessage(), HttpStatus.SC_BAD_REQUEST);
            }
            newPipId = HsConstants.UUID_LENGTH;
//            firewallService.addQos(eipEntity.getFloatingIp(), eipEntity.getEipAddress(), String.valueOf(eipUpdateParam.getBandwidth()),
//                    eipEntity.getFirewallId());
            if (null != newPipId) {
                removeStatus = firewallService.removeFipFromSbwQos(eipEntity.getFirewallId(), eipEntity.getFloatingIp(), sbw.getId());
            } else {
                removeStatus = false;
            }
        }
        //如果EIP未绑定虚机，只更新数据库，待绑定时候有fip再加入共享带宽
        if (removeStatus || CommonUtil.qosDebug) {
            //update the eip table
            eipEntity.setUpdatedTime(CommonUtil.getGmtDate());
            eipEntity.setPipId(newPipId);
            eipEntity.setSbwId(null);
            eipEntity.setBandWidth(eipUpdateParam.getBandwidth());
            eipEntity.setChargeMode(HsConstants.BANDWIDTH);
            eipRepository.saveAndFlush(eipEntity);

            sbw.setUpdatedTime(CommonUtil.getGmtDate());
            sbwRepository.saveAndFlush(sbw);
            log.info("Eip remove to sbw success.:{}", eipEntity);
            return ActionResponse.actionSuccess();
        }
        msg = "Failed to remove ip in sbw,eip:{},sbwId:{}" + eipEntity + sbwId;
        log.error(msg);
        return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    public Page<Sbw> findByIdAndIsDelete(String sbwId, String projectId, int isDelete, Pageable pageable) {
        return sbwRepository.findByIdAndProjectIdAndIsDelete(sbwId, projectId, isDelete, pageable);
    }

    public Page<Sbw> findByIsDeleteAndSbwName(String projectId, int isDelete, String name, Pageable pageable) {
        return sbwRepository.findByProjectIdAndIsDeleteAndSbwNameContaining(projectId, isDelete, name, pageable);
    }

    public Page<Sbw> findByIsDelete(String projectId, int isDelte, Pageable pageable) {
        return sbwRepository.findByProjectIdAndIsDelete(projectId, isDelte, pageable);
    }

    public Sbw findByIdAndIsDelete(String id,int isDelete){
        return sbwRepository.findByIdAndIsDelete(id,isDelete);
    }


}
