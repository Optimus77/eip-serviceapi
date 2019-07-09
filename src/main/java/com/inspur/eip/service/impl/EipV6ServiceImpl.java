package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.ipv6.*;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.EipV6Repository;
import com.inspur.eip.service.EipV6DaoService;
import com.inspur.eip.service.IEipV6Service;
import com.inspur.eip.service.NatPtService;
import com.inspur.eip.util.*;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class EipV6ServiceImpl implements IEipV6Service {


    @Autowired
    private EipV6DaoService eipV6DaoService;

    @Autowired
    private EipV6Repository eipV6Repository;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private NatPtService natPtService;



    /**
     * create a eipV6
     * @param eipV4Id          eipV4Id
     * @return                   json info of eip
     */
    public ResponseEntity atomCreateEipV6(String eipV4Id, String token) {

        String code;
        String msg;
        try {
            EipPoolV6 eipV6 = eipV6DaoService.getOneEipFromPoolV6();
            if(null == eipV6) {
                msg = "Failed, no eipv6 in eip pool v6.";
                log.error(msg);
                return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_RESOURCE_NOTENOUGH, msg),
                        HttpStatus.FAILED_DEPENDENCY);
            }

            EipV6 eipMo = eipV6DaoService.allocateEipV6(eipV4Id, eipV6, token);
            if (null != eipMo) {
                EipV6ReturnBase eipInfo = new EipV6ReturnBase();
                BeanUtils.copyProperties(eipMo, eipInfo);
                log.info("Atom create a eipv6 success:{}", eipMo);
                return new ResponseEntity<>(eipInfo, HttpStatus.OK);
            } else {
                code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
                msg = "Failed to create eipv6 " ;
                log.error(msg);
            }

        }catch (Exception e){
            log.error("Exception in atomCreateEipV6", e.getMessage());
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     *   the eipV6
     * @param pageNo  the current page
     * @param pageSize  element of per page
     * @return       result
     */
    @Override
    public ResponseEntity listEipV6s(int pageNo,int pageSize, String status){

        try {
            String userId= CommonUtil.getUserId();
            log.debug("listEipV6s  of user, userId:{}", userId);
            if(userId==null){
                return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                        "get projcetid error please check the Authorization param"), HttpStatus.BAD_REQUEST);
            }
            JSONObject data=new JSONObject();
            JSONArray eipv6s=new JSONArray();
            if(pageNo!=0){
                Sort sort = new Sort(Sort.Direction.DESC, "createdTime");
                Pageable pageable =PageRequest.of(pageNo-1,pageSize,sort);
                Page<EipV6> page=eipV6Repository.findByUserIdAndIsDelete(userId, 0, pageable);
                for(EipV6 eipV6:page.getContent()){
                    if (eipV6.getIpv4() == null || eipV6.getIpv4().equals("")) {
                        log.error("Failed to obtain eipv4 in eipv6",eipV6.getIpv4());
                        return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),"Failed to obtain eipv4 in eipv6"), HttpStatus.BAD_REQUEST);
                    }else{
                        Eip eip = eipRepository.findByEipAddressAndUserIdAndIsDelete(eipV6.getIpv4(), eipV6.getUserId(),0);
                        if(eip == null){
                            return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                                    "Failed to fetch eip based on ipv4"), HttpStatus.BAD_REQUEST);
                        }
                        if((StringUtils.isNotBlank(status)) && (!eipV6.getStatus().trim().equalsIgnoreCase(status))){
                            continue;
                        }
                        EipV6ReturnDetail eipV6ReturnDetail = new EipV6ReturnDetail();
                        BeanUtils.copyProperties(eipV6, eipV6ReturnDetail);
                        if (eip.getBandWidth() > 10) {
                            eipV6ReturnDetail.setEipv6Bandwidth(10);

                        } else {
                            eipV6ReturnDetail.setEipv6Bandwidth(eip.getBandWidth());
                        }
                        eipV6ReturnDetail.setEipBandwidth(eip.getBandWidth());
                        eipV6ReturnDetail.setEipChargeType(eip.getBillType());
                        eipV6ReturnDetail.setEipId(eip.getId());
                        eipV6ReturnDetail.setEipCreatedTime(eip.getCreatedTime());
                        eipv6s.add(eipV6ReturnDetail);
                    }

                }
                data.put("totalCount",page.getTotalElements());
                data.put("pageNo",pageNo);
                data.put("pageSize",pageSize);
                data.put("data", eipv6s);
            }else{
                List<EipV6> eipV6List=eipV6DaoService.findEipV6ByUserId(userId);
                for(EipV6 eipV6:eipV6List){
                    if (eipV6.getIpv4() == null || eipV6.getIpv4().equals("")) {
                        log.error("Failed to obtain eipv4 in eipv6",eipV6.getIpv4());
                        return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),"Failed to obtain eipv4 in eipv6"), HttpStatus.BAD_REQUEST);
                    }else{
                        Eip eip = eipRepository.findByEipAddressAndUserIdAndIsDelete(eipV6.getIpv4(), eipV6.getUserId(),0);
                        if(eip == null){
                            return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                                    "Failed to fetch eip based on ipv4"), HttpStatus.BAD_REQUEST);
                        }
                        if((StringUtils.isNotBlank(status)) && (!eipV6.getStatus().trim().equalsIgnoreCase(status))){
                            continue;
                        }
                        EipV6ReturnDetail eipV6ReturnDetail = new EipV6ReturnDetail();
                        BeanUtils.copyProperties(eipV6, eipV6ReturnDetail);
                        if (eip.getBandWidth() > 10) {
                            eipV6ReturnDetail.setEipv6Bandwidth(10);
                        } else {
                            eipV6ReturnDetail.setEipv6Bandwidth(eip.getBandWidth());
                        }
                        eipV6ReturnDetail.setEipBandwidth(eip.getBandWidth());
                        eipV6ReturnDetail.setEipChargeType(eip.getBillType());
                        eipV6ReturnDetail.setEipId(eip.getId());
                        eipv6s.add(eipV6ReturnDetail);
                    }

                }
                data.put("data", eipv6s);
                data.put("totalCount",eipv6s.size());
                data.put("pageNo",1);
                data.put("pageSize",eipv6s.size());
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch(KeycloakTokenException e){
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_FORBIDDEN,e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            log.error("Exception in listEipv6s", e);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * delete eipV6
     * @param eipV6Id eipV6id
     * @return return
     */
    public ResponseEntity atomDeleteEipV6(String eipV6Id) {
        String msg;
        String code;

        try {
            ActionResponse actionResponse =  eipV6DaoService.deleteEipV6(eipV6Id,CommonUtil.getKeycloackToken());
            if (actionResponse.isSuccess()){
                log.info("Atom delete eipV6 successfully, eipV6Id:{}", eipV6Id);
                return new ResponseEntity<>(ReturnMsgUtil.success(), HttpStatus.OK);
            }else {
                msg = actionResponse.getFault();
                code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
                log.info("Atom delete eipV6 failed,{}", msg);
            }
        }catch (Exception e){
            log.error("Exception in atomDeleteEipV6", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * get detail of the eipv6
     * @param eipV6Id  the id of the eipv6 instance
     * @return the json result
     */
    @Override
    public ResponseEntity getEipV6Detail(String eipV6Id) {

        try {
            EipV6 eipV6Entity = eipV6DaoService.getEipV6ById(eipV6Id);
            if (null != eipV6Entity) {
                if (eipV6Entity.getIpv4() == null || eipV6Entity.getIpv4().equals("")) {
                    log.error("Failed to obtain eipv4 in eipv6",eipV6Entity.getIpv4());
                    return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),"Failed to obtain eipv4 in eipv6"), HttpStatus.BAD_REQUEST);
                }else {
                    Eip eip = eipRepository.findByEipAddressAndUserIdAndIsDelete(eipV6Entity.getIpv4(), eipV6Entity.getUserId(), 0);
                    if(eip == null){
                        log.error("Failed to fetch eip based on ipv4",eipV6Entity.getIpv4());
                        return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),"Failed to fetch eip based on ipv4"), HttpStatus.BAD_REQUEST);
                    }
                    EipV6ReturnDetail eipV6ReturnDetail = new EipV6ReturnDetail();
                    BeanUtils.copyProperties(eipV6Entity, eipV6ReturnDetail);
                    eipV6ReturnDetail.setEipBandwidth(eip.getBandWidth());
                    eipV6ReturnDetail.setEipChargeType(eip.getBillType());
                    eipV6ReturnDetail.setEipId(eip.getId());
                    eipV6ReturnDetail.setEipCreatedTime(eip.getCreatedTime());

                    return new ResponseEntity<>(eipV6ReturnDetail, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_NOT_FOUND,
                        "Can not find eipV6 by id:" + eipV6Id+"."),
                        HttpStatus.NOT_FOUND);

            }
        } catch (Exception e) {
            log.error("Exception in getEipV6Detail", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    /**
     * eipV6 bind with port
     * @param eipV6Id      eipV6Id
     * @return        result
     */
    @Override
    public ResponseEntity eipV6bindPort(String eipV6Id,String ipv4){
        String code=null;
        String msg=null;
        Optional<EipV6> optional = eipV6Repository.findById(eipV6Id);
        if (!optional.isPresent()) {
            code=ReturnStatus.SC_NOT_FOUND;
            msg="Failed to get eipv6 based on eipV6Id, id";
            log.error("Failed to get eipv6 based on eipV6Id, id:{}.", eipV6Id);
            return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        EipV6 eipV6 = optional.get();
        try {
            Eip eipEntity = eipRepository.findByEipAddressAndUserIdAndIsDelete(eipV6.getIpv4(), eipV6.getUserId(), 0);
            if (eipEntity == null) {
                code = ReturnStatus.SC_NOT_FOUND;
                msg = "Query eip failed";
                return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Eip eip = eipRepository.findByEipAddressAndUserIdAndIsDelete(ipv4, eipV6.getUserId(), 0);
            if (eip == null) {
                code = ReturnStatus.SC_NOT_FOUND;
                msg = "Query eip failed";
                return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (eipV6.getDnatptId() == null && eipV6.getSnatptId() == null) {
                if (eip.getFloatingIp() == null) {
                    EipV6 newEipV6 = eipV6DaoService.updateIp(ipv4, eipV6);
                    if (newEipV6 != null) {
                        eip.setEipV6Id(eipV6Id);
                        eipRepository.saveAndFlush(eip);
                        eipEntity.setEipV6Id(null);
                        eipRepository.saveAndFlush(eipEntity);
                        code = "200";
                        msg = "update success";
                        log.info("update success ，eipv6id:{},newIpv4:{}",eipV6.getId(),eip.getEipAddress());
                        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.OK);
                    }

                } else {
                    NatPtV6 natPtV6 = natPtService.addNatPt(eipV6.getIpv6(),eip.getEipAddress(), eip.getFloatingIp(), eipV6.getFirewallId());
                    if (natPtV6 != null) {
                        eipV6.setSnatptId(natPtV6.getNewSnatPtId());
                        eipV6.setDnatptId(natPtV6.getNewDnatPtId());
                        eipV6.setFloatingIp(eip.getFloatingIp());
                        eipV6.setIpv4(ipv4);
                        eipV6.setUpdatedTime(CommonUtil.getGmtDate());
                        eipV6Repository.saveAndFlush(eipV6);
                        eip.setEipV6Id(eipV6Id);
                        eipRepository.saveAndFlush(eip);
                        eipEntity.setEipV6Id(null);
                        eipRepository.saveAndFlush(eipEntity);
                        log.info("add nat successfully. snat:{}, dnat:{},eipv6id:{},newIpv4:{},",
                                natPtV6.getNewSnatPtId(), natPtV6.getNewDnatPtId(),eipV6.getId(),eip.getEipAddress());
                        code = ReturnStatus.SC_OK;
                        msg = "Ipv4 was replaced successfully";
                        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.OK);
                    } else {
                        code = ReturnStatus.SC_FIREWALL_NAT_UNAVAILABLE;
                        msg = "Failed to add natPtId";
                        log.error("Failed to add natPtId" );
                    }
                }
            }else{
                if (eip.getFloatingIp() == null) {
                    Boolean flag = natPtService.delNatPt(eipV6.getSnatptId(), eipV6.getDnatptId(), eipV6.getFirewallId());
                    if (flag) {
                        eipV6.setSnatptId(null);
                        eipV6.setDnatptId(null);
                        eipV6.setFloatingIp(null);
                        eipV6.setIpv4(ipv4);
                        eipV6.setUpdatedTime(CommonUtil.getGmtDate());
                        eipV6Repository.saveAndFlush(eipV6);
                        eip.setEipV6Id(eipV6Id);
                        eipRepository.saveAndFlush(eip);
                        eipEntity.setEipV6Id(null);
                        eipRepository.saveAndFlush(eipEntity);
                        log.info("del nat successfully. snat:{}, dnat:{},eipv6id:{},newIpv4:{},",
                                eipV6.getSnatptId(), eipV6.getDnatptId(),eipV6.getId(),eip.getEipAddress());
                        code = ReturnStatus.SC_OK;
                        msg = "Ipv4 was replaced successfully";
                        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.OK);
                    } else {
                        eip.setStatus(HsConstants.ERROR);
                        log.error("Failed to del natPtId" + eipV6.getSnatptId(), eipV6.getDnatptId());
                        code = ReturnStatus.SC_FIREWALL_NAT_UNAVAILABLE;
                        msg = "Failed to del natPtId";
                    }
                } else {
                    Boolean flag = natPtService.delNatPt(eipV6.getSnatptId(), eipV6.getDnatptId(), eipV6.getFirewallId());
                    log.info("del nat successfully. snat:{}, dnat:{},",
                            eipV6.getSnatptId(), eipV6.getDnatptId());
                    if (flag) {
                        NatPtV6 natPtV6 = natPtService.addNatPt(eipV6.getIpv6(),eip.getEipAddress(), eip.getFloatingIp(), eipV6.getFirewallId());
                        if (natPtV6 != null) {
                            eipV6.setSnatptId(natPtV6.getNewSnatPtId());
                            eipV6.setDnatptId(natPtV6.getNewDnatPtId());
                            eipV6.setFloatingIp(eip.getFloatingIp());
                            eipV6.setIpv4(ipv4);
                            eipV6.setUpdatedTime(CommonUtil.getGmtDate());
                            eipV6Repository.saveAndFlush(eipV6);
                            eip.setEipV6Id(eipV6Id);
                            eipRepository.saveAndFlush(eip);
                            eipEntity.setEipV6Id(null);
                            eipRepository.saveAndFlush(eipEntity);
                            log.info("add nat successfully. snat:{}, dnat:{},,eipv6id:{},newIpv4:{},",
                                    natPtV6.getNewSnatPtId(), natPtV6.getNewDnatPtId(),eipV6.getId(),eip.getEipAddress());
                            code = ReturnStatus.SC_OK;
                            msg = "Ipv4 was replaced successfully";
                            return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.OK);
                        } else {
                            log.error("Failed to add natPtId");
                            code = ReturnStatus.SC_FIREWALL_NAT_UNAVAILABLE;
                            msg = "Failed to add natPtId";
                        }
                    } else {
                        log.error("Failed to del natPtId" + eipV6.getSnatptId(), eipV6.getDnatptId());
                        code = ReturnStatus.SC_FIREWALL_NAT_UNAVAILABLE;
                        msg = "Failed to del natPtId";
                    }
                }
            }

        } catch (Exception e) {
            log.error("eipbindPort exception", e);

            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    EipV6 findEipV6ByEipV6Id(String eipV6Id){
        return eipV6DaoService.findByEipV6IdAndIsDelete(eipV6Id,0);
    }

}
