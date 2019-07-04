package com.inspur.eip.service;

import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.ipv6.EipPoolV6;
import com.inspur.eip.entity.ipv6.EipV6;
import com.inspur.eip.entity.ipv6.NatPtV6;
import com.inspur.eip.repository.EipPoolV6Repository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.EipV6Repository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.exception.KeycloakTokenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EipV6DaoService {


    @Autowired
    private EipPoolV6Repository eipPoolV6Repository;

    @Autowired
    private EipV6Repository eipV6Repository;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private FireWallCommondService fireWallCommondService;

    @Autowired
    private NatPtService natPtService;



    /**
     * allocate eipv6
     *
     * @param  eipV4Id    eipconfig
     * @return result
     */
    @Transactional
    public EipV6 allocateEipV6(String  eipV4Id, EipPoolV6 eipPoolv6, String token) throws KeycloakTokenException {

        Optional<Eip> optional = eipRepository.findById(eipV4Id);
        if(!optional.isPresent()){
            log.error("Faild to find eip by id:{}",eipV4Id);
            eipPoolV6Repository.saveAndFlush(eipPoolv6);
            return null;
        }
        Eip eip = optional.get();
        if(StringUtils.isNotBlank(eip.getEipV6Id())){
            log.error("This eip has been associated with ipv6:{}",eipV4Id);
            eipPoolV6Repository.saveAndFlush(eipPoolv6);
            return null;
        }
        if(StringUtils.isNotBlank(eip.getSbwId())){
            log.error("This eip adds Shared bandwidth:{}",eipV4Id);
            eipPoolV6Repository.saveAndFlush(eipPoolv6);
            return null;
        }
        if (!eipPoolv6.getState().equals("0")) {
            log.error("Fatal Error! eipv6 state is not free, state:{}.", eipPoolv6.getState());
            eipPoolV6Repository.saveAndFlush(eipPoolv6);
            return null;
        }
        EipPoolV6 eipPoolV6Check  = eipPoolV6Repository.findByIp(eipPoolv6.getIp());
        if(eipPoolV6Check != null){
            log.error("==================================================================================");
            log.error("Fatal Error! get a duplicate eipv6 from eip pool v6, eip_v6_address:{}.", eipPoolv6.getIp());
            log.error("===================================================================================");
            eipPoolV6Repository.deleteById(eipPoolV6Check.getId());
            eipPoolV6Repository.flush();
        }
        EipV6 eipV6Entity = eipV6Repository.findByIpv6AndIsDelete(eipPoolv6.getIp(), 0);
        if(null != eipV6Entity){
            log.error("Fatal Error! get a duplicate eipv6 from eip pool v6, eip_v6_address:{} id:{}.",
                    eipV6Entity.getIpv6(), eipV6Entity.getId());
            return null;
        }
        EipV6 eipMo = new EipV6();
        NatPtV6 natPtV6;
        try {
            if (StringUtils.isNotEmpty(eip.getFloatingIp())) {
                natPtV6 = natPtService.addNatPt(eipPoolv6.getIp(),eip.getEipAddress(), eip.getFloatingIp(), eipPoolv6.getFireWallId());
                if (natPtV6 != null) {
                    eipMo.setSnatptId(natPtV6.getNewSnatPtId());
                    eipMo.setDnatptId(natPtV6.getNewDnatPtId());
                    eipMo.setFloatingIp(eip.getFloatingIp());
                } else {
                    log.error("Failed to add natPtId");
                    EipPoolV6 eipPoolV6Mo = new EipPoolV6();
                    eipPoolV6Mo.setFireWallId(eipPoolv6.getFireWallId());
                    eipPoolV6Mo.setIp(eipPoolv6.getIp());
                    eipPoolV6Mo.setState("0");
                    eipPoolV6Repository.saveAndFlush(eipPoolV6Mo);
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("add natPtId exception", e);
        }
        eipMo.setIpv6(eipPoolv6.getIp());
        eipMo.setFirewallId(eipPoolv6.getFireWallId());
        eipMo.setRegion(eip.getRegion());
        eipMo.setIpv4(eip.getEipAddress());
        String userId = CommonUtil.getUserId(token);
        log.debug("get tenantid:{} from clientv3", userId);
        eipMo.setUserId(userId);
        eipMo.setIsDelete(0);
        eipMo.setCreatedTime(CommonUtil.getGmtDate());
        eipV6Repository.saveAndFlush(eipMo);
        eip.setEipV6Id(eipMo.getId());
        eip.setUpdatedTime(CommonUtil.getGmtDate());
        eipRepository.saveAndFlush(eip);
        log.info("User:{} success allocate eipv6:{}",userId, eipMo.getId());
        return eipMo;
    }

    @Transactional(isolation= Isolation.SERIALIZABLE)
    public synchronized EipPoolV6 getOneEipFromPoolV6(){
        EipPoolV6 eipAddress =  eipPoolV6Repository.getEipV6ByRandom();
        if(null != eipAddress) {
            eipPoolV6Repository.deleteById(eipAddress.getId());
            eipPoolV6Repository.flush();
        }
        return eipAddress;
    }


    public List<EipV6> findEipV6ByUserId(String userId){
        return eipV6Repository.findByUserIdAndIsDelete(userId,0);
    }

    public EipV6 findByEipV6IdAndIsDelete(String eipV6Id, int isDelete){
        return eipV6Repository.findByIdAndIsDelete(eipV6Id,0);
    }


    @Transactional
    public ActionResponse deleteEipV6(String eipv6id,String token)  {
        String msg;
        EipV6 eipV6Entity = eipV6Repository.findByIdAndIsDelete(eipv6id,0);
        if (null == eipV6Entity) {
            msg= "Faild to find eipV6 by id:"+eipv6id;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        if(!CommonUtil.verifyToken(token,eipV6Entity.getUserId())){
            log.error(CodeInfo.getCodeMessage(CodeInfo.EIP_FORBIDEN_WITH_ID), eipv6id);
            return ActionResponse.actionFailed(HsConstants.FORBIDEN, HttpStatus.SC_FORBIDDEN);
        }
        try {
            if (eipV6Entity.getDnatptId() != null && eipV6Entity.getSnatptId() != null) {
                Boolean flag = natPtService.delNatPt(eipV6Entity.getSnatptId(),eipV6Entity.getDnatptId(),eipV6Entity.getFirewallId());
                if (flag) {
                    log.info("delete natPt success");
                } else {
                    msg = "Failed to delete natPtId";
                    log.error(msg);
                    return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            msg = "delete natPtId exception";
            log.error(msg, e);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        eipV6Entity.setFloatingIp(null);
        eipV6Entity.setDnatptId(null);
        eipV6Entity.setSnatptId(null);
        eipV6Entity.setIsDelete(1);
        eipV6Entity.setUpdatedTime(CommonUtil.getGmtDate());
        eipV6Repository.saveAndFlush(eipV6Entity);
        Eip eip = eipRepository.findByEipAddressAndUserIdAndIsDelete(eipV6Entity.getIpv4(), eipV6Entity.getUserId(), 0);
        if(eip == null){
            msg = "Failed to fetch eip based on ipv4";
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
        }
        eip.setEipV6Id(null);
        eip.setUpdatedTime(CommonUtil.getGmtDate());
        eipRepository.saveAndFlush(eip);
        EipPoolV6 eipV6Pool = eipPoolV6Repository.findByIp(eipV6Entity.getIpv6());
        if(null != eipV6Pool){
            log.error("******************************************************************************");
            log.error("Fatal error, eipV6 has already exist in eipV6 pool. can not add to eipV6 pool.{}",
                    eipV6Entity.getIpv6());
            log.error("******************************************************************************");
        }else {
            EipPoolV6 eipPoolV6Mo = new EipPoolV6();
            eipPoolV6Mo.setFireWallId(eipV6Entity.getFirewallId());
            eipPoolV6Mo.setIp(eipV6Entity.getIpv6());
            eipPoolV6Mo.setState("0");
            eipPoolV6Repository.saveAndFlush(eipPoolV6Mo);
            log.info("Success delete eipV6:{}",eipV6Entity.getIpv6());
        }
        return ActionResponse.actionSuccess();
    }

    @Transactional
    public ActionResponse adminDeleteEipV6(String eipv6id)  {
        String msg;
        EipV6 eipV6Entity = eipV6Repository.findByIdAndIsDelete(eipv6id,0);
        if (null == eipV6Entity) {
            msg= "Faild to find eipV6 by id:"+eipv6id;
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        try {
            if (eipV6Entity.getDnatptId() != null && eipV6Entity.getSnatptId() != null) {
                Boolean flag = natPtService.delNatPt(eipV6Entity.getSnatptId(),eipV6Entity.getDnatptId(),eipV6Entity.getFirewallId());
                if (flag) {
                    log.info("delete natPt success");
                } else {
                    msg = "Failed to delete natPtId";
                    log.error(msg);
                    return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            msg = "delete natPtId exception";
            log.error(msg, e);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_NOT_FOUND);
        }
        eipV6Entity.setFloatingIp(null);
        eipV6Entity.setDnatptId(null);
        eipV6Entity.setSnatptId(null);
        eipV6Entity.setIsDelete(1);
        eipV6Entity.setUpdatedTime(CommonUtil.getGmtDate());
        eipV6Repository.saveAndFlush(eipV6Entity);
        Eip eip = eipRepository.findByEipAddressAndUserIdAndIsDelete(eipV6Entity.getIpv4(), eipV6Entity.getUserId(), 0);
        if(eip == null){
            msg = "Failed to fetch eip based on ipv4";
            log.error(msg);
            return ActionResponse.actionFailed(msg, HttpStatus.SC_BAD_REQUEST);
        }
        eip.setEipV6Id(null);
        eip.setUpdatedTime(CommonUtil.getGmtDate());
        eipRepository.saveAndFlush(eip);
        EipPoolV6 eipV6Pool = eipPoolV6Repository.findByIp(eipV6Entity.getIpv6());
        if(null != eipV6Pool){
            log.error("******************************************************************************");
            log.error("Fatal error, eipV6 has already exist in eipV6 pool. can not add to eipV6 pool.{}",
                    eipV6Entity.getIpv6());
            log.error("******************************************************************************");
        }else {
            EipPoolV6 eipPoolV6Mo = new EipPoolV6();
            eipPoolV6Mo.setFireWallId(eipV6Entity.getFirewallId());
            eipPoolV6Mo.setIp(eipV6Entity.getIpv6());
            eipPoolV6Mo.setState("0");
            eipPoolV6Repository.saveAndFlush(eipPoolV6Mo);
            log.info("Success delete eipV6:{}",eipV6Entity.getIpv6());
        }
        return ActionResponse.actionSuccess();
    }




    boolean bindIpv6WithInstance(String eipAddress, String floatingIp, String userId) throws Exception{

        EipV6 eipV6 = eipV6Repository.findByIpv4AndUserIdAndIsDelete(eipAddress, userId, 0);
        if (eipV6 != null) {
            NatPtV6 natPtV6 = natPtService.addNatPt(eipV6.getIpv6(),eipAddress, floatingIp, eipV6.getFirewallId());
            if (natPtV6 == null) {
                log.error("Failed to add natpt wieth:{}---{}",eipAddress, eipV6.getIpv6() );
                return false;
            }
            eipV6.setFloatingIp(floatingIp);
            eipV6.setDnatptId(natPtV6.getNewDnatPtId());
            eipV6.setSnatptId(natPtV6.getNewSnatPtId());
            eipV6.setUpdatedTime(CommonUtil.getGmtDate());
            eipV6Repository.saveAndFlush(eipV6);
            log.info("Bind eipv6 with instance successfully. eip:{}", eipV6.toString());
        }
        return  true;
    }

    boolean unBindIpv6WithInstance(String eipAddress, String userId) throws Exception{

        EipV6 eipV6 = eipV6Repository.findByIpv4AndUserIdAndIsDelete(eipAddress, userId, 0);
        if (eipV6 != null) {
            Boolean flag = natPtService.delNatPt(eipV6.getSnatptId(), eipV6.getDnatptId(), eipV6.getFirewallId());
            if (!flag) {
                log.error("Failed to disassociate  with natPt:{}--{}", eipV6.getSnatptId() ,eipV6.getDnatptId());
                return false;
            }
            eipV6.setSnatptId(null);
            eipV6.setDnatptId(null);
            eipV6.setFloatingIp(null);
            eipV6.setUpdatedTime(CommonUtil.getGmtDate());
            eipV6Repository.saveAndFlush(eipV6);
            log.info("unbind ipv6 with instance successful, {}---{}", eipAddress, eipV6.getIpv6());
        }
        return  true;
    }
    @Transactional
    public EipV6 getEipV6ById(String id){

        EipV6 eipV6Entity = null;
        Optional<EipV6> eipV6 = eipV6Repository.findById(id);
        if (eipV6.isPresent()) {
            eipV6Entity = eipV6.get();
        }

        return eipV6Entity;
    }

    @Transactional(isolation= Isolation.SERIALIZABLE)
    public EipV6 updateIp(String newIpv4 ,EipV6 eipV6){

        eipV6.setIpv4(newIpv4);
        eipV6.setUpdatedTime(CommonUtil.getGmtDate());
        eipV6Repository.saveAndFlush(eipV6);
        return eipV6;
    }


}
