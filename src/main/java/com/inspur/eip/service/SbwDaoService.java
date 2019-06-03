package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.v2.MethodReturn;
import com.inspur.eip.entity.v2.MethodSbwReturn;
import com.inspur.eip.entity.v2.eip.Eip;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.v2.fw.Firewall;
import com.inspur.eip.entity.v2.sbw.Sbw;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.FirewallRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.ErrorStatus;
import com.inspur.eip.util.v2.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class SbwDaoService {
    @Autowired
    private SbwRepository sbwRepository;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private FirewallService firewallService;

    @Autowired
    private FirewallRepository firewallRepository;

    @Autowired
    private QosService qosService;

    public List<Sbw> findByProjectId(String projectId) {
        return sbwRepository.findByProjectIdAndIsDelete(projectId, 0);
    }

    @Transactional
    public Sbw allocateSbw(SbwUpdateParam sbwConfig) {
        Sbw sbwMo = null;
        try {
            String userId = CommonUtil.getUserId();
            sbwMo = new Sbw();
            sbwMo.setRegion(sbwConfig.getRegion());
            sbwMo.setSbwName(sbwConfig.getSbwName());
            sbwMo.setBandWidth(sbwConfig.getBandwidth());
            sbwMo.setBillType(sbwConfig.getBillType());
            sbwMo.setDuration(sbwConfig.getDuration());
            sbwMo.setProjectId(userId);
            sbwMo.setProjectName(CommonUtil.getProjectName());
            sbwMo.setIsDelete(0);
            sbwMo.setCreateTime(CommonUtil.getGmtDate());
            Sbw sbw = sbwRepository.saveAndFlush(sbwMo);

            Firewall firewall = firewallRepository.findFirewallByRegion(sbwConfig.getRegion());

            String pipeId = firewallService.addQos(null, sbw.getSbwId(), String.valueOf(sbw.getBandWidth()), firewall.getId());
            if (StringUtils.isNotBlank(pipeId)) {
                sbwMo.setPipeId(pipeId);
                sbwRepository.saveAndFlush(sbwMo);
                log.info("Success create a sbw qos sbwId:{} ,sbw:{}", sbw.getSbwId(), sbw.toString());
            } else {
                sbwRepository.deleteById(sbw.getSbwId());
                log.warn("Failed to create sbw qos in FireWall,pipe create failure");
            }
        } catch (KeycloakTokenException e) {
            log.error("KeycloakTokenException", e);
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return sbwMo;
    }

    public Sbw getSbwById(String id) {
        Sbw sbwEntity = null;
        Optional<Sbw> sbw = sbwRepository.findById(id);
        if (sbw.isPresent()) {
            sbwEntity = sbw.get();
        }
        return sbwEntity;
    }

    /**
     * delete
     *
     * @param sbwId id
     * @return ret
     */
    @Transactional
    public ActionResponse deleteSbw(String sbwId) {
        String msg;
        long ipCount;
        Sbw sbwBean = sbwRepository.findBySbwId(sbwId);
        if (null == sbwBean ||sbwBean.getIsDelete() ==1 ) {
            msg = "Faild to find sbw by id:" + sbwId;
            log.error(msg);
            return ActionResponse.actionFailed(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage(), HttpStatus.SC_NOT_FOUND);
        }
        if (!CommonUtil.isAuthoried(sbwBean.getProjectId())) {
            log.error(CodeInfo.getCodeMessage(CodeInfo.SBW_FORBIDDEN), sbwId);
            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
        }
        ipCount = eipRepository.countBySbwIdAndIsDelete(sbwBean.getSbwId(), 0);
        if (ipCount != 0) {
            msg = "EIP in sbw so that sbw cannot be removed ，please remove first !,ipCount:{}" + ipCount;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_FORBIDDEN);
        }
        Firewall firewall = firewallRepository.findFirewallByRegion(sbwBean.getRegion());
        if (StringUtils.isBlank(sbwBean.getPipeId())) {
            sbwBean.setIsDelete(1);
            sbwBean.setStatus(HsConstants.DELETE);
            sbwBean.setUpdateTime(CommonUtil.getGmtDate());
            sbwRepository.saveAndFlush(sbwBean);
            return ActionResponse.actionSuccess();
        }
        boolean delQos = firewallService.delQos(sbwBean.getPipeId(), null,null,firewall.getId());
        if (delQos) {
            sbwBean.setIsDelete(1);
            sbwBean.setStatus(HsConstants.DELETE);
            sbwBean.setUpdateTime(CommonUtil.getGmtDate());
            sbwBean.setPipeId(null);
            sbwRepository.saveAndFlush(sbwBean);
            return ActionResponse.actionSuccess();
        }
        return ActionResponse.actionFailed(CodeInfo.SBW_DELETE_ERROR, HttpStatus.SC_FORBIDDEN);
    }


    @Transactional
    public ActionResponse softDownSbw(String sbwId) {
        String msg;
        Sbw sbw = sbwRepository.findBySbwId(sbwId);
        if (null == sbw) {
            msg = "Faild to find in soft Down by sbwId:{}:" + sbwId ;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        if (!CommonUtil.isAuthoried(sbw.getProjectId())) {
            log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), sbwId);
            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
        }
        Firewall firewall = firewallRepository.findFirewallByRegion(sbw.getRegion());
        if (firewall == null) {
            msg = "Can't find firewall by sbw region:{}"+ sbw.getRegion();
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
        }
        if (StringUtils.isNotEmpty(sbw.getStatus()) && HsConstants.ACTIVE.equalsIgnoreCase(sbw.getStatus()) && StringUtils.isNotEmpty(sbw.getPipeId())){
            MethodReturn methodReturn = qosService.controlPipe(firewall.getId(), sbwId, true);
            if (methodReturn.getHttpCode() == HttpStatus.SC_OK) {
                sbw.setUpdateTime(CommonUtil.getGmtDate());
                sbw.setStatus("STOP");
                sbwRepository.saveAndFlush(sbw);
                return ActionResponse.actionSuccess();
            } else {
                return ActionResponse.actionFailed(methodReturn.getMessage(), methodReturn.getHttpCode());
            }
        }else {
            sbw.setUpdateTime(CommonUtil.getGmtDate());
            sbw.setStatus("STOP");
            sbwRepository.saveAndFlush(sbw);
            return ActionResponse.actionSuccess();
        }
    }

    @Transactional
    public ActionResponse renewSbwEntity(String sbwId) {
        String msg;
        Sbw sbw = sbwRepository.findBySbwId(sbwId);
        if (null == sbw) {
            msg = "Faild to find in Renew by sbwId:{}" + sbwId;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        if (!CommonUtil.isAuthoried(sbw.getProjectId())) {
            log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), sbwId);
            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
        }
        if (!sbw.getBillType().equals(HsConstants.MONTHLY)) {
            msg = "BillType is not monthly SBW cannot be renewed:{}" + sbwId;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
        }
        Firewall firewall = firewallRepository.findFirewallByRegion(sbw.getRegion());
        if (firewall == null) {
            msg = "Can't find firewall by sbw region:{}"+ sbw.getRegion();
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
        }
        if (StringUtils.isNotEmpty(sbw.getStatus()) && HsConstants.STOP.equalsIgnoreCase(sbw.getStatus()) && StringUtils.isNotEmpty(sbw.getPipeId())) {
            MethodReturn methodReturn = qosService.controlPipe(firewall.getId(), sbwId, false);
            if (methodReturn.getHttpCode() == HttpStatus.SC_OK) {
                sbw.setUpdateTime(CommonUtil.getGmtDate());
                sbw.setStatus("ACTIVE");
                sbwRepository.saveAndFlush(sbw);
                return ActionResponse.actionSuccess();
            } else {
                return ActionResponse.actionFailed(methodReturn.getMessage(), methodReturn.getHttpCode());
            }
        } else {
            sbw.setUpdateTime(CommonUtil.getGmtDate());
            sbw.setStatus("ACTIVE");
            sbwRepository.saveAndFlush(sbw);
            return ActionResponse.actionSuccess();
        }
    }

    @Transactional
    public JSONObject renameSbw(String sbwId, SbwUpdateParam param) {
        JSONObject data = new JSONObject();
        String newSbwName = param.getSbwName();
        Sbw sbw = sbwRepository.findBySbwId(sbwId);
        if (null == sbw) {
            log.error("In rename sbw process,failed to find the sbw by id:{} ", sbwId);
            data.put(HsConstants.REASON, CodeInfo.getCodeMessage(CodeInfo.SBW_NOT_FOND_BY_ID));
            data.put(HsConstants.HTTP_CODE, HttpStatus.SC_NOT_FOUND);
            data.put(HsConstants.INTER_CODE, ReturnStatus.SC_NOT_FOUND);
            return data;
        }
        if (!CommonUtil.isAuthoried(sbw.getProjectId())) {
            log.error("User have no write to operate sbw:{}", sbwId);
            data.put(HsConstants.REASON, CodeInfo.getCodeMessage(CodeInfo.SBW_FORBIDDEN));
            data.put(HsConstants.HTTP_CODE, HttpStatus.SC_FORBIDDEN);
            data.put(HsConstants.INTER_CODE, ReturnStatus.SC_FORBIDDEN);
            return data;
        }
        sbw.setSbwName(newSbwName);
        sbw.setUpdateTime(CommonUtil.getGmtDate());
        sbwRepository.saveAndFlush(sbw);
        data.put(HsConstants.REASON, "");
        data.put(HsConstants.HTTP_CODE, HttpStatus.SC_OK);
        data.put(HsConstants.INTER_CODE, ReturnStatus.SC_OK);
        data.put("data", sbw);
        return data;
    }

    @Transactional
    public MethodSbwReturn updateSbwEntity(String sbwId, SbwUpdateParam param) {

        Sbw sbwEntity = sbwRepository.findBySbwId(sbwId);
        if (null == sbwEntity) {
            log.error("In update sbw bandWidth  process,failed to find the sbw by id:{} ", sbwId);
            return MethodReturnUtil.errorSbw(HttpStatus.SC_NOT_FOUND, ReturnStatus.SC_NOT_FOUND,
                    CodeInfo.getCodeMessage(CodeInfo.SBW_NOT_FOND_BY_ID));
        }
        if (!CommonUtil.isAuthoried(sbwEntity.getProjectId())) {
            log.error("User  not have permission to update sbw bandWidth sbwId:{}", sbwId);
            return MethodReturnUtil.errorSbw(HttpStatus.SC_FORBIDDEN, ReturnStatus.SC_FORBIDDEN,
                    CodeInfo.getCodeMessage(CodeInfo.SBW_FORBIDDEN));
        }
        if (param.getBillType().equals(HsConstants.MONTHLY) && param.getBandwidth() < sbwEntity.getBandWidth()) {
            //can’t  modify
            return MethodReturnUtil.errorSbw(HttpStatus.SC_BAD_REQUEST, ReturnStatus.SC_PARAM_ERROR,
                    CodeInfo.getCodeMessage(CodeInfo.SBW_THE_NEW_BANDWIDTH_VALUE_ERROR));
        }
        if (sbwEntity.getPipeId() == null) {
            sbwEntity.setBandWidth(param.getBandwidth());
            sbwEntity.setBillType(param.getBillType());
            sbwEntity.setUpdateTime(CommonUtil.getGmtDate());
            sbwRepository.saveAndFlush(sbwEntity);
            return MethodReturnUtil.successSbw(sbwEntity);
        }
        Firewall firewall = firewallRepository.findFirewallByRegion(sbwEntity.getRegion());
        boolean updateStatus = firewallService.updateQosBandWidth(firewall.getId(), sbwEntity.getPipeId(), sbwEntity.getSbwId(), String.valueOf(param.getBandwidth()), null, null);
        if (updateStatus || CommonUtil.qosDebug) {
            sbwEntity.setBandWidth(param.getBandwidth());
            sbwEntity.setBillType(param.getBillType());
            sbwEntity.setUpdateTime(CommonUtil.getGmtDate());
            sbwRepository.saveAndFlush(sbwEntity);

            Stream<Eip> stream = eipRepository.findByUserIdAndIsDeleteAndSbwId(sbwEntity.getProjectId(), 0, sbwId).stream();
            stream.forEach(eip -> {
                eip.setBandWidth(param.getBandwidth());
                eip.setUpdateTime(CommonUtil.getGmtDate());
                eipRepository.saveAndFlush(eip);
            });
            return MethodReturnUtil.successSbw(sbwEntity);
        }
        return MethodReturnUtil.errorSbw(HttpStatus.SC_INTERNAL_SERVER_ERROR, ReturnStatus.SC_FIREWALL_SERVER_ERROR,
                CodeInfo.getCodeMessage(CodeInfo.SBW_CHANGE_BANDWIDTH_ERROR));

    }

    @Transactional
    public MethodReturn addEipIntoSbw(String eipid, EipUpdateParam eipUpdateParam) {


        String sbwId = eipUpdateParam.getSbwId();
        Eip eipEntity = eipRepository.findByEipId(eipid);
        String pipeId;
        if (null == eipEntity) {
            log.error("In addEipIntoSbw process,failed to find the eip by id:{} ", eipid);
            return MethodReturnUtil.error(HttpStatus.SC_NOT_FOUND, ReturnStatus.SC_NOT_FOUND,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_NOT_FOND));
        }
        if (StringUtils.isNotBlank(eipEntity.getEipV6Id())) {
            log.error("EIP is already bound to eipv6");
            return MethodReturnUtil.error(HttpStatus.SC_NOT_FOUND, ReturnStatus.SC_NOT_FOUND,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_EIPV6_ERROR));
        }
        if (!CommonUtil.isAuthoried(eipEntity.getUserId())) {
            log.error("User have no write to operate eip:{}", eipid);
            return MethodReturnUtil.error(HttpStatus.SC_FORBIDDEN, ReturnStatus.SC_FORBIDDEN,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDDEN));
        }
        //1.ensure eip is billed on hourlySettlement
        if (eipEntity.getBillType().equals(HsConstants.MONTHLY)) {
            log.error("The eip billType isn't hourlySettment!", eipEntity.getBillType());
            return MethodReturnUtil.error(HttpStatus.SC_BAD_REQUEST, ReturnStatus.SC_PARAM_ERROR,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_BILLTYPE_NOT_HOURLYSETTLEMENT));
        }
        //3.check eip had not adding any Shared bandWidth
        if (StringUtils.isNotBlank(eipEntity.getSbwId())) {
            log.error("The shared band id not null, this mean the eip had already added other SBW !", eipEntity.getSbwId());
            return MethodReturnUtil.error(HttpStatus.SC_BAD_REQUEST, ReturnStatus.SC_PARAM_ERROR,
                    CodeInfo.getCodeMessage(CodeInfo.EIP_SHARED_BAND_WIDTH_ID_NOT_NULL));
        }
        Sbw sbwEntiy = sbwRepository.findBySbwId(sbwId);
        if (null == sbwEntiy) {
            log.error("Failed to find sbw by id:{} ", sbwId);
            return MethodReturnUtil.error(HttpStatus.SC_NOT_FOUND, ReturnStatus.SC_NOT_FOUND,
                    CodeInfo.getCodeMessage(CodeInfo.SBW_NOT_FOND_BY_ID));
        }
        boolean updateStatus = true;
        if (eipEntity.getStatus().equalsIgnoreCase(HsConstants.ACTIVE)) {
            log.info("FirewallId: " + eipEntity.getFirewallId() + " FloatingIp: " + eipEntity.getFloatingIp() + " sbwId: " + sbwId);
            if (eipUpdateParam.getBandwidth() != sbwEntiy.getBandWidth()){
                return MethodReturnUtil.error(HttpStatus.SC_NOT_FOUND, ReturnStatus.SC_NOT_FOUND,
                        CodeInfo.getCodeMessage(CodeInfo.SBW_THE_NEW_BANDWIDTH_VALUE_ERROR));
            }
            pipeId = firewallService.addFloatingIPtoQos(eipEntity.getFirewallId(), eipEntity.getFloatingIp(), sbwEntiy.getPipeId());
            if (null != pipeId) {
                updateStatus = firewallService.delQos(eipEntity.getPipId(), eipEntity.getEipAddress(),eipEntity.getFloatingIp(), eipEntity.getFirewallId());
                if (StringUtils.isBlank(sbwEntiy.getPipeId())) {
                    sbwEntiy.setPipeId(pipeId);
                }
            } else {
                updateStatus = false;
            }
        }

        if (updateStatus || CommonUtil.qosDebug) {
            eipEntity.setPipId(sbwEntiy.getPipeId());
            eipEntity.setUpdateTime(new Date());
            eipEntity.setSbwId(sbwId);
            eipEntity.setOldBandWidth(eipEntity.getBandWidth());
            eipEntity.setChargeMode(HsConstants.SHAREDBANDWIDTH);
            eipEntity.setBandWidth(eipUpdateParam.getBandwidth());
            eipRepository.saveAndFlush(eipEntity);

            sbwEntiy.setUpdateTime(new Date());
            sbwRepository.saveAndFlush(sbwEntiy);

            return MethodReturnUtil.success(eipEntity);
        }

        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ReturnStatus.SC_FIREWALL_SERVER_ERROR,
                CodeInfo.getCodeMessage(CodeInfo.EIP_CHANGE_BANDWIDTH_ERROR));

    }

    @Transactional
    public ActionResponse removeEipFromSbw(String eipid, EipUpdateParam eipUpdateParam) {
        Eip eipEntity = eipRepository.findByEipId(eipid);
        String msg;
        String sbwId = eipUpdateParam.getSbwId();
        Sbw sbw = sbwRepository.findBySbwId(sbwId);
        if (null == sbw) {
            log.error("In removeEipFromSbw process,failed to find sbw by id:{} ", sbwId);
            return ActionResponse.actionFailed("Eip Not found.", HttpStatus.SC_NOT_FOUND);
        }
        if (null == eipEntity) {
            log.error("In removeEipFromSbw process,failed to find the eip by id:{} ", eipid);
            return ActionResponse.actionFailed("Eip Not found.", HttpStatus.SC_NOT_FOUND);
        }

        if (!CommonUtil.isAuthoried(eipEntity.getUserId())) {
            log.error("User have no write to delete eip:{}", eipid);
            return ActionResponse.actionFailed("Forbiden.", HttpStatus.SC_FORBIDDEN);
        }
        boolean removeStatus = true;
        String newPipId = null;
        if (eipEntity.getStatus().equalsIgnoreCase(HsConstants.ACTIVE)) {
            log.info("FirewallId: " + eipEntity.getFirewallId() + " FloatingIp: " + eipEntity.getFloatingIp() + " sbwId: " + sbwId);
            if (eipUpdateParam.getBandwidth() != eipEntity.getOldBandWidth()){
                return ActionResponse.actionFailed("Update param bandwidth error.", HttpStatus.SC_NOT_FOUND);
            }
            newPipId = firewallService.addQos(eipEntity.getFloatingIp(), eipEntity.getEipAddress(), String.valueOf(eipUpdateParam.getBandwidth()),
                    eipEntity.getFirewallId());
            if (null != newPipId) {
                removeStatus = firewallService.removeFloatingIpFromQos(eipEntity.getFirewallId(), eipEntity.getFloatingIp(), eipEntity.getPipId());
            } else {
                removeStatus = false;
            }
        }

        if (removeStatus || CommonUtil.qosDebug) {
            eipEntity.setUpdateTime(new Date());
            //update the eip table
            eipEntity.setPipId(newPipId);
            eipEntity.setSbwId(null);
            eipEntity.setBandWidth(eipUpdateParam.getBandwidth());
            eipEntity.setChargeMode(HsConstants.BANDWIDTH);
            eipRepository.saveAndFlush(eipEntity);

            sbw.setUpdateTime(new Date());
            sbwRepository.saveAndFlush(sbw);
            return ActionResponse.actionSuccess();
        }

        msg = "Failed to remove ip in sharedBand,eipId:" + eipEntity.getEipId() + " sharedBandWidthId:" + sbwId + "";
        log.error(msg);
        return ActionResponse.actionFailed(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }

    @Transactional
    public Page<Sbw> findByIdAndIsDelete(String sbwId, String userId, int isDelete, Pageable pageable) {
        return sbwRepository.findBySbwIdAndProjectIdAndIsDelete(sbwId, userId, isDelete, pageable);
    }

    @Transactional
    public Page<Sbw> findByIsDeleteAndSbwName(String userId, int isDelete, String name, Pageable pageable) {
        return sbwRepository.findByProjectIdAndIsDeleteAndSbwNameContaining(userId, isDelete, name, pageable);
    }

    @Transactional
    public Page<Sbw> findByIsDelete(String userId, int isDelte, Pageable pageable) {
        return sbwRepository.findByProjectIdAndIsDelete(userId, isDelte, pageable);
    }


}
