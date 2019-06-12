package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.v2.eip.Eip;
import com.inspur.eip.entity.v2.eip.EipReturnDetail;
import com.inspur.eip.entity.v2.eip.Resourceset;
import com.inspur.eip.entity.ipv6.EipV6;
import com.inspur.eip.entity.v2.sbw.Sbw;
import com.inspur.eip.entity.v2.sbw.SbwReturnBase;
import com.inspur.eip.entity.v2.sbw.SbwReturnDetail;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.EipV6Repository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.ISbwService;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.util.*;
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
    private EipDaoService eipDaoService;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private EipV6Repository eipV6Repository;

    @ICPServiceLog
    @Override
    public ResponseEntity atomCreateSbw(SbwUpdateParam sbwConfig, String token) {

        String code;
        String msg;
        try {
            Sbw sbwMo = sbwDaoService.allocateSbw(sbwConfig, token);
            if (null != sbwMo) {
                SbwReturnBase sbwInfo = new SbwReturnBase();
                BeanUtils.copyProperties(sbwMo, sbwInfo);
                log.info("Atom create a sbw success:{}", sbwMo);
                return new ResponseEntity<>(ReturnMsgUtil.success(sbwInfo), HttpStatus.OK);
            } else {
                code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
                msg = "Failed to create sbw :" + sbwConfig;
                log.error(msg);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg), HttpStatus.INTERNAL_SERVER_ERROR);
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
                data.put("sbws", sbws);
                data.put(HsConstants.TOTAL_PAGES, page.getTotalPages());
                data.put(HsConstants.TOTAL_ELEMENTS, page.getTotalElements());
                data.put(HsConstants.CURRENT_PAGE, pageIndex);
                data.put(HsConstants.CURRENT_PAGEPER, pageSize);
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
                data.put("sbws", sbws);
                data.put(HsConstants.TOTAL_PAGES, 1);
                data.put(HsConstants.TOTAL_ELEMENTS, sbws.size());
                data.put(HsConstants.CURRENT_PAGE, 1);
                data.put(HsConstants.CURRENT_PAGEPER, sbws.size());
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
                return new ResponseEntity<>(ReturnMsgUtil.success(sbwReturnDetail), HttpStatus.OK);
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
            if (StringUtils.isBlank(sbwId)){
                ActionResponse.actionFailed(ErrorStatus.PARAM_CAN_NOT_BE_NULL.getMessage()+" sbwId:"+ sbwId+ " bandWidth:"+param.getBandwidth(),HttpStatus.BAD_REQUEST.value());
            }else {
               return sbwDaoService.updateSbwEntity(sbwId, param, token);
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

            return new ResponseEntity<>(ReturnMsgUtil.msg(ReturnStatus.SC_OK, "get instance num success", num), HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            return new ResponseEntity<>(ReturnMsgUtil.msg(ReturnStatus.SC_FORBIDDEN, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(ReturnMsgUtil.msg(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by current user
     *
     * @param projectId project id
     * @return ret
     */
    @Override
    @ICPServiceLog
    public ResponseEntity getSbwByProjectId(String projectId) {
        try {
            if (projectId == null) {
                return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                        "get project id error please check the Authorization param"), HttpStatus.BAD_REQUEST);
            }
            JSONObject data = new JSONObject();
            JSONArray sbws = new JSONArray();
            List<Sbw> sbwList = sbwDaoService.findByProjectId(projectId);
            for (Sbw sbw : sbwList) {
                SbwReturnDetail sbwReturnDetail = new SbwReturnDetail();
                long count = eipRepository.countBySbwIdAndIsDelete(sbw.getSbwId(), 0);
                sbwReturnDetail.setIpCount((int) count);
                BeanUtils.copyProperties(sbw, sbwReturnDetail);
                sbws.add(sbwReturnDetail);
            }
            data.put("sbws", sbws);
            data.put(HsConstants.TOTAL_PAGES, 1);
            data.put(HsConstants.TOTAL_ELEMENTS, sbws.size());
            data.put(HsConstants.CURRENT_PAGE, 1);
            data.put(HsConstants.CURRENT_PAGEPER, sbws.size());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception in listShareBandWidth", e);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 包年包月续费/自动续费
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
                return sbwDaoService.renewSbwEntity(sbwId, token);
            }
        } catch (Exception e) {
            log.error("Exception in restart sbw service:{}", e);
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
            if (StringUtils.isBlank(renewTime)) {
                return ActionResponse.actionFailed(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value());
            } else if (renewTime.trim().equals("0")) {
                return sbwDaoService.stopSbwService(sbwId);
            }
        } catch (Exception e) {
            log.error("Exception in stop sbw Service Sbw:{}", e);
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
                            .resourceid(eip.getInstanceId())
                            .resourcetype(eip.getInstanceType()).build());
                    eips.add(eipReturnDetail);
                }
                data.put("eips", eips);
                data.put("totalPages", page.getTotalPages());
                data.put("totalElements", page.getTotalElements());
                data.put("currentPage", currentPage);
                data.put("currentPagePer", limit);
            } else {
                List<Eip> eipList = eipRepository.findByUserIdAndIsDeleteAndSbwId(projcectid, 0, sbwId);
                for (Eip eip : eipList) {

                    EipReturnDetail eipReturnDetail = new EipReturnDetail();
                    BeanUtils.copyProperties(eip, eipReturnDetail);
                    eipReturnDetail.setResourceset(Resourceset.builder()
                            .resourceid(eip.getInstanceId())
                            .resourcetype(eip.getInstanceType()).build());
                    eips.add(eipReturnDetail);
                }
                data.put("eips", eips);
                data.put("totalPages", 1);
                data.put("totalElements", eips.size());
                data.put("currentPage", 1);
                data.put("currentPagePer", eips.size());
            }
            log.debug("data:{}", data.toString());
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_FORBIDDEN, e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception in listEips", e);
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
                return new ResponseEntity<>(ReturnMsgUtil.success(sbwReturnDetail),HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Exception in rename sbw", e);
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
            data.put("eips", eips);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_FORBIDDEN, e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception in getOtherEips", e);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
