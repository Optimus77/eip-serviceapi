package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.ipv6.EipV6;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.eip.EipReturnDetail;
import com.inspur.eip.entity.eip.Resourceset;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.entity.sbw.SbwReturnBase;
import com.inspur.eip.entity.sbw.SbwReturnDetail;
import com.inspur.eip.exception.EipBadRequestException;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.EipV6Repository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.service.ISbwService;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.util.*;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import com.inspur.icp.common.util.annotation.ICPServiceLog;
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

@Slf4j
@Service
public class SbwServiceImpl implements ISbwService {
    @Autowired
    private SbwRepository sbwRepository;

    @Autowired
    private SbwDaoService sbwDaoService;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private EipV6Repository eipV6Repository;

    @ICPServiceLog
    @Override
    public ResponseEntity atomCreateSbw(SbwUpdateParam sbwConfig, String token) {

        try {
            Sbw sbwMo = sbwDaoService.allocateSbw(sbwConfig, token);
            if (null != sbwMo) {
                SbwReturnBase sbwInfo = new SbwReturnBase();
                BeanUtils.copyProperties(sbwMo, sbwInfo);
                log.info("Create a sbw success:{}", sbwMo);
                return new ResponseEntity<>(sbwInfo, HttpStatus.OK);
            } else {
                log.error("Failed to create sbw :{}" + sbwConfig);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    @ICPServiceLog
    public ResponseEntity listShareBandWidth(Integer pageIndex, Integer pageSize, String searchValue) {
        try {
            String matche = "(\\w{8}(-\\w{4}){3}-\\w{12}?)";
            String projectid = CommonUtil.getUserId();
            log.debug("listShareBandWidth  of user, userId:{}", projectid);
            if (projectid == null) {
                return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                        "get project id error please check the Authorization param"), HttpStatus.BAD_REQUEST);
            }
            JSONObject data = new JSONObject();
            JSONArray sbws = new JSONArray();
            Page<Sbw> page;
            if (pageIndex != 0) {
                Sort sort = new Sort(Sort.Direction.DESC, "createTime");
                Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, sort);
                if (StringUtils.isNotBlank(searchValue)) {
                    if (searchValue.matches(matche)) {
                        page = sbwDaoService.findByIdAndIsDelete(searchValue, projectid, 0, pageable);
                    } else {
                        page = sbwDaoService.findByIsDeleteAndSbwName(projectid, 0, searchValue, pageable);
                    }
                } else {
                    page = sbwDaoService.findByIsDelete(projectid, 0, pageable);
                }
                for (Sbw sbw : page.getContent()) {
                    SbwReturnDetail sbwReturnDetail = new SbwReturnDetail();
                    BeanUtils.copyProperties(sbw, sbwReturnDetail);
                    long ipCount = eipRepository.countBySbwIdAndIsDelete(sbw.getSbwId(), 0);
                    sbwReturnDetail.setIpCount((int) ipCount);
                    sbws.add(sbwReturnDetail);
                }
                data.put("data", sbws);
                data.put(HsConstants.TOTAL_PAGES, page.getTotalPages());
                data.put(HsConstants.TOTAL_COUNT, page.getTotalElements());
                data.put(HsConstants.PAGE_NO, pageIndex);
                data.put(HsConstants.PAGE_SIZE, pageSize);
            } else {
                List<Sbw> sbwList = sbwDaoService.findByProjectId(projectid);
                for (Sbw sbw : sbwList) {
                    if (StringUtils.isNotBlank(searchValue)) {
                        continue;
                    }
                    SbwReturnDetail sbwReturnDetail = new SbwReturnDetail();
                    BeanUtils.copyProperties(sbw, sbwReturnDetail);
                    long ipCount = eipRepository.countBySbwIdAndIsDelete(sbw.getSbwId(), 0);
                    sbwReturnDetail.setIpCount((int) ipCount);
                    sbws.add(sbwReturnDetail);
                }
                data.put("data", sbws);
                data.put(HsConstants.TOTAL_PAGES, 1);
                data.put(HsConstants.TOTAL_COUNT, sbws.size());
                data.put(HsConstants.PAGE_NO, 1);
                data.put(HsConstants.PAGE_SIZE, sbws.size());
            }
            log.debug("data :{}", data.toString());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception in listShareBandWidth", e);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @ICPServiceLog
    public ActionResponse deleteSbwInfo(String sbwId, String token) {
        try {
            if (StringUtils.isBlank(sbwId)){
                return ActionResponse.actionFailed(ErrorStatus.PARAM_CAN_NOT_BE_NULL.getMessage()+ sbwId,HttpStatus.BAD_REQUEST.value());
            }else {
                return sbwDaoService.deleteSbw(sbwId, token);
            }
        } catch (Exception e) {
            log.error("Exception in atom Delete SBW", e);
        }
        return ActionResponse.actionFailed(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public ActionResponse bssSoftDeleteSbw(String sbwId) {
        try {
            if (StringUtils.isBlank(sbwId)){
                return ActionResponse.actionFailed(ErrorStatus.PARAM_CAN_NOT_BE_NULL.getMessage()+ sbwId,HttpStatus.BAD_REQUEST.value());
            }else {
                return sbwDaoService.adminDeleteSbw(sbwId);
            }
        } catch (Exception e) {
            log.error("Exception in atom delete Sbw", e);
        }
        return ActionResponse.actionFailed(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public ResponseEntity getSbwDetail(String sbwId) {
        try {
            Sbw sbwEntity = sbwDaoService.getSbwById(sbwId);
            if (null != sbwEntity) {
                SbwReturnDetail sbwReturnDetail = new SbwReturnDetail();
                BeanUtils.copyProperties(sbwEntity, sbwReturnDetail);
                sbwReturnDetail.setIpCount((int) eipRepository.countBySbwIdAndIsDelete(sbwId, 0));
                log.debug("sbw Detail:{}", sbwReturnDetail.toString());
                return new ResponseEntity<>(sbwReturnDetail, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_NOT_FOND_IN_DB.getCode(), ErrorStatus.ENTITY_NOT_FOND_IN_DB.getMessage()), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Exception in get Sbw Detail", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(), ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @ICPServiceLog
    public ActionResponse updateSbwConfig(String sbwId, SbwUpdateParam param, String token) {
        try {
            if (StringUtils.isNotBlank(sbwId) && HsConstants.UUID_LENGTH.length() == sbwId.length()){
                return sbwDaoService.updateSbwEntity(sbwId, param, token);
            }else {
                ActionResponse.actionFailed(ErrorStatus.PARAM_CAN_NOT_BE_NULL.getMessage()+" sbwId:"+ sbwId+ " bandWidth:"+param.getBandwidth(),HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception e) {
            log.error("Exception in update Sbw Config", e);
        }
        return ActionResponse.actionFailed(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    @Override
    @ICPServiceLog
    public ResponseEntity getSbwCount() {
        try {
            String projectid = CommonUtil.getUserId();
            long num = sbwRepository.countByProjectIdAndIsDelete(projectid, 0);

            return new ResponseEntity<>(ReturnMsgUtil.msg(ReturnStatus.SC_OK, HsConstants.SUCCESS, num), HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            return new ResponseEntity<>(ReturnMsgUtil.msg(ErrorStatus.SC_FORBIDDEN.getCode(), ErrorStatus.SC_FORBIDDEN.getMessage(), null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception in listShareBandWidth", e.getMessage());
            return new ResponseEntity<>(ReturnMsgUtil.msg(ErrorStatus.SC_INTERNAL_SERVER_ERROR.getCode(),ErrorStatus.SC_INTERNAL_SERVER_ERROR.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 包年包月续费
     *
     * @param sbwId
     * @param updateParam
     * @return
     */
    @ICPServiceLog
    public ActionResponse restartSbwService(String sbwId, SbwUpdateParam updateParam, String token) {
        try {
            String renewTime = updateParam.getDuration();
            if (StringUtils.isBlank(sbwId)|| StringUtils.isBlank(renewTime)) {
                return ActionResponse.actionFailed(ErrorStatus.PARAM_CAN_NOT_BE_NULL.getMessage()+" sbwId:"+ sbwId+ " duration:"+ updateParam.getDuration(),HttpStatus.BAD_REQUEST.value());
            } else if (Integer.parseInt(renewTime) > 0) {
                return sbwDaoService.renewSbwInfo(sbwId, token);
            }
        } catch (Exception e) {
            log.error("Exception in restart sbw service:{}", e.getMessage());
        }
        return ActionResponse.actionFailed(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * 包年包月停服
     *
     * @param sbwId
     * @param updateParam
     * @return
     */
    @ICPServiceLog
    public ActionResponse stopSbwService(String sbwId, SbwUpdateParam updateParam) {
        try {
            String renewTime = updateParam.getDuration();
            if (StringUtils.isBlank(renewTime) || StringUtils.isBlank(sbwId)) {
                return ActionResponse.actionFailed(HttpStatus.BAD_REQUEST.getReasonPhrase()+"sbwId:"+sbwId + "duration:"+renewTime, HttpStatus.BAD_REQUEST.value());
            } else if (renewTime.trim().equals("0")) {
                return sbwDaoService.stopSbwService(sbwId);
            }
        } catch (Exception e) {
            log.error("Exception in stop sbw Service Sbw:{}", e.getMessage());
        }
        return ActionResponse.actionFailed(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    /**
     * get eipList in sbw :获取某共享带宽的EIP列表
     *
     * @param sbwId       sbw id
     * @param currentPage page
     * @param limit       limit
     * @return ret
     */
    public ResponseEntity sbwListEip(String sbwId, Integer currentPage, Integer limit) {
        String projcectid;
        try {
            projcectid = CommonUtil.getUserId();
            log.debug("list Eips  in one sbw of user, userId:{}", projcectid);
            if (projcectid == null) {
                return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                        "get projcetid error please check the Authorization param"), HttpStatus.BAD_REQUEST);
            }
            JSONObject data = new JSONObject();
            JSONArray eips = new JSONArray();
            if (currentPage != 0) {
                Sort sort = new Sort(Sort.Direction.DESC, "createTime");
                Pageable pageable = PageRequest.of(currentPage - 1, limit, sort);
                Page<Eip> page = eipRepository.findByUserIdAndIsDeleteAndSbwId(projcectid, 0, sbwId, pageable);
                for (Eip eip : page.getContent()) {
                    EipReturnDetail eipReturnDetail = new EipReturnDetail();
                    BeanUtils.copyProperties(eip, eipReturnDetail);
                    eipReturnDetail.setResourceset(Resourceset.builder()
                            .resourceId(eip.getInstanceId())
                            .resourceType(eip.getInstanceType()).build());
                    eips.add(eipReturnDetail);
                }
                data.put("data", eips);
                data.put(HsConstants.TOTAL_PAGES, page.getTotalPages());
                data.put(HsConstants.TOTAL_COUNT, page.getTotalElements());
                data.put(HsConstants.PAGE_NO, currentPage);
                data.put(HsConstants.PAGE_SIZE, limit);
            } else {
                List<Eip> eipList = eipRepository.findByUserIdAndIsDeleteAndSbwId(projcectid, 0, sbwId);
                for (Eip eip : eipList) {

                    EipReturnDetail eipReturnDetail = new EipReturnDetail();
                    BeanUtils.copyProperties(eip, eipReturnDetail);
                    eipReturnDetail.setResourceset(Resourceset.builder()
                            .resourceId(eip.getInstanceId())
                            .resourceType(eip.getInstanceType()).build());
                    eips.add(eipReturnDetail);
                }
                data.put("data", eips);
                data.put(HsConstants.TOTAL_PAGES, 1);
                data.put(HsConstants.TOTAL_COUNT, eips.size());
                data.put(HsConstants.PAGE_NO, 1);
                data.put(HsConstants.PAGE_SIZE, eips.size());
            }
            log.debug("data:{}", data.toString());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_FORBIDDEN, e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception in listEips", e.getMessage());
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * rename sbw
     *
     * @return ret
     */
    public ResponseEntity renameSbw(String sbwId, SbwUpdateParam param) {
        try {
            if (StringUtils.isBlank(sbwId) || StringUtils.isBlank(param.getSbwName())){
               return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.SC_PARAM_ERROR.getCode(), ErrorStatus.SC_PARAM_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }else {
                SbwReturnDetail sbwReturnDetail = new SbwReturnDetail();
                Sbw sbwBean = sbwDaoService.renameSbw(sbwId, param);
                BeanUtils.copyProperties(sbwBean, sbwReturnDetail);
                sbwReturnDetail.setIpCount((int) eipRepository.countBySbwIdAndIsDelete(sbwId, 0));
                return new ResponseEntity<>(sbwReturnDetail,HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Exception in rename sbw:{}", e.getMessage());
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(),ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * get unbinding ip
     *
     * @param sbwId sbw id
     * @return ret
     */
    public ResponseEntity getOtherEips(String sbwId) {
        try {
            String userId = CommonUtil.getUserId();
            if (userId == null) {
                return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                        "get projcetid error please check the Authorization param"), HttpStatus.BAD_REQUEST);
            }
            List<Eip> eipList = eipRepository.getEipListNotBinding(userId, 0, HsConstants.HOURLYSETTLEMENT, "");
            JSONArray eips = new JSONArray();
            JSONObject data = new JSONObject();

            for (Eip eip : eipList) {
                if (null != eip.getSbwId() && eip.getSbwId().equals(sbwId)) {
                    continue;
                }
                EipV6 eipV6 = eipV6Repository.findByIpv4AndUserIdAndIsDelete(eip.getEipAddress(), eip.getUserId(), 0);
                if (eipV6 == null) {
                    EipReturnDetail eipReturn = new EipReturnDetail();
                    BeanUtils.copyProperties(eip, eipReturn);
                    eips.add(eipReturn);
                }
            }
            data.put("data", eips);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_FORBIDDEN, e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception in getOtherEips", e.getMessage());
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
