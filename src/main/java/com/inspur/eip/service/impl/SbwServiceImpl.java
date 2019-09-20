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
import com.inspur.iam.adapter.util.ListFilterUtil;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Slf4j
@Service
public class SbwServiceImpl implements ISbwService {

    public static final String SELECT_FROM_SBW_BY_ISDELETE_PROJECTID = "select * from sbw where is_delete='0' and project_id= '";

    @Autowired
    private SbwRepository sbwRepository;

    @Autowired
    private SbwDaoService sbwDaoService;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private EipV6Repository eipV6Repository;

    @PersistenceContext
    private EntityManager entityManager;

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
    public ResponseEntity listShareBandWidth(Integer pageIndex, Integer pageSize, String searchValue) {
        try {
            String matche = "(\\w{8}(-\\w{4}){3}-\\w{12}?)";
            String projectId = CommonUtil.getProjectId();
            log.debug("listShareBandWidth  of user, userId:{}", projectId);
            if (projectId == null) {
                return new ResponseEntity<>(ReturnMsgUtil.error(String.valueOf(HttpStatus.BAD_REQUEST),
                        "get project id error please check the Authorization param"), HttpStatus.BAD_REQUEST);
            }
            JSONObject data = new JSONObject();
            JSONArray sbws = new JSONArray();
            Page<Sbw> page;
            String querySql;
            if (pageIndex != 0) {
                //Sort sort = new Sort(Sort.Direction.DESC, "createdTime");
                Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
                if (StringUtils.isNotBlank(searchValue)) {
                    if (searchValue.matches(matche)) {      //ID精确查询
                        querySql=SELECT_FROM_SBW_BY_ISDELETE_PROJECTID+projectId+"'"+" and id='"+searchValue+"'"+ HsConstants.ORDER_BY_CREATED_TIME_DESC;
                        //page = sbwDaoService.findByIdAndIsDelete(searchValue, projectId, 0, pageable);
                    } else {        //根据名称模糊查询
                        querySql=SELECT_FROM_SBW_BY_ISDELETE_PROJECTID+projectId+"'"+" and sbw_name like'%"+searchValue+"%'"+ HsConstants.ORDER_BY_CREATED_TIME_DESC;
                        //page = sbwDaoService.findByIsDeleteAndSbwName(projectId, 0, searchValue, pageable);
                    }
                } else {
                    querySql=SELECT_FROM_SBW_BY_ISDELETE_PROJECTID+projectId+"'" + HsConstants.ORDER_BY_CREATED_TIME_DESC;
                    //page = sbwDaoService.findByIsDelete(projectid, 0, pageable);
                }
                page = ListFilterUtil.filterPageDataBySql(entityManager, querySql, pageable, Sbw.class);
                for (Sbw sbw : page.getContent()) {
                    SbwReturnDetail sbwReturnDetail = new SbwReturnDetail();
                    BeanUtils.copyProperties(sbw, sbwReturnDetail);
                    long ipCount = eipRepository.countBySbwIdAndIsDelete(sbw.getId(), 0);
                    sbwReturnDetail.setIpCount((int) ipCount);
                    sbws.add(sbwReturnDetail);
                }
                data.put("data", sbws);
                data.put(HsConstants.TOTAL_PAGES, page.getTotalPages());
                data.put(HsConstants.TOTAL_COUNT, page.getTotalElements());
                data.put(HsConstants.PAGE_NO, pageIndex);
                data.put(HsConstants.PAGE_SIZE, pageSize);
            } else {
                List<Sbw> sbwList = sbwDaoService.findByProjectId(projectId);
                List<Sbw> dataList = ListFilterUtil.filterListData(sbwList, Sbw.class);
                for (Sbw sbw : dataList) {
                    if (StringUtils.isNotBlank(searchValue)) {
                        continue;
                    }
                    SbwReturnDetail sbwReturnDetail = new SbwReturnDetail();
                    BeanUtils.copyProperties(sbw, sbwReturnDetail);
                    long ipCount = eipRepository.countBySbwIdAndIsDelete(sbw.getId(), 0);
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
    @Override
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
    public ActionResponse updateSbwConfig(String sbwId, SbwUpdateParam param, String token) {
        try {
            if (StringUtils.isNotBlank(sbwId) && HsConstants.UUID_LENGTH.length() == sbwId.length()){
                return sbwDaoService.updateSbwEntity(sbwId, param, token);
            }else {
                ActionResponse.actionFailed(ErrorStatus.PARAM_CAN_NOT_BE_NULL.getMessage()+" id:"+ sbwId+ " bandWidth:"+param.getBandwidth(),HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception e) {
            log.error("Exception in update Sbw Config", e);
        }
        return ActionResponse.actionFailed(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    @Override
    public ResponseEntity countSbwNumsByProjectId() {
        try {
            String projectId = CommonUtil.getProjectId();
            List<Sbw> sbwList = sbwDaoService.findByProjectId(projectId);
            List<Sbw> dataList = ListFilterUtil.filterListData(sbwList, Sbw.class);
            int size = dataList.size();
            return new ResponseEntity<>(ReturnMsgUtil.msg(ReturnStatus.SC_OK, HsConstants.SUCCESS, size), HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            log.error("KeycloakTokenException in count sbw nums:{}", e.getMessage());
        }
        return new ResponseEntity<>(ReturnMsgUtil.msg(ErrorStatus.SC_FORBIDDEN.getCode(), ErrorStatus.SC_FORBIDDEN.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    /**
     * 可通过状态统计用户下的sbw实例数量
     * @param status ：ACTIVE | STOP | DELETE
     * @return
     */
    @Override
    public ResponseEntity countSbwNumsByStatus(String status){
        try {
            String projectId = CommonUtil.getProjectId();
            if (status.equals(HsConstants.ACTIVE) || status.equals(HsConstants.STOP)|| status.equals(HsConstants.DELETE)){
                long num = sbwRepository.countByStatusAndProjectIdAndIsDelete(status, projectId, 0);
                log.info("Atom get Sbw Count loading……:{}",num);
                return new ResponseEntity<>(ReturnMsgUtil.msg(ReturnStatus.SC_OK, HsConstants.SUCCESS, num), HttpStatus.OK);
            }else {
                log.warn(ErrorStatus.SC_PARAM_NOTFOUND +":{}",status);
                throw new EipBadRequestException(ErrorStatus.SC_PARAM_NOTFOUND.getCode(),ErrorStatus.SC_PARAM_NOTFOUND.getMessage());
            }
        } catch (KeycloakTokenException e) {
            log.error("KeycloakTokenException in count sbw nums by status:{}", e.getMessage());
        }
        return new ResponseEntity<>(ReturnMsgUtil.msg(ErrorStatus.SC_FORBIDDEN.getCode(), ErrorStatus.SC_FORBIDDEN.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Sbw getSbwById(String id) {
        return sbwDaoService.findByIdAndIsDelete(id,0);
    }

    /**
     * 包年包月续费
     *
     * @param sbwId
     * @param updateParam
     * @return
     */
    public ActionResponse restartSbwService(String sbwId, SbwUpdateParam updateParam, String token) {
        try {
            String renewTime = updateParam.getDuration();
            if (StringUtils.isBlank(sbwId)|| StringUtils.isBlank(renewTime)) {
                return ActionResponse.actionFailed(ErrorStatus.PARAM_CAN_NOT_BE_NULL.getMessage()+" id:"+ sbwId+ " duration:"+ updateParam.getDuration(),HttpStatus.BAD_REQUEST.value());
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
    public ActionResponse stopSbwService(String sbwId, SbwUpdateParam updateParam) {
        try {
            String renewTime = updateParam.getDuration();
            if (StringUtils.isBlank(renewTime) || StringUtils.isBlank(sbwId)) {
                return ActionResponse.actionFailed(HttpStatus.BAD_REQUEST.getReasonPhrase()+"id:"+sbwId + "duration:"+renewTime, HttpStatus.BAD_REQUEST.value());
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
        String projectId;
        try {
            projectId = CommonUtil.getProjectId();
            log.debug("list Eips  in one sbw of user, userId:{}", projectId);
            if (projectId == null) {
                return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_UNAUTHORIZED.getCode(),
                        ErrorStatus.ENTITY_UNAUTHORIZED.getMessage()), HttpStatus.BAD_REQUEST);
            }
            Sbw sbw = sbwDaoService.getSbwById(sbwId);
            if (null == sbw){
                return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(),
                        ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage()), HttpStatus.BAD_REQUEST);
            }
            JSONObject data = new JSONObject();
            JSONArray eips = new JSONArray();
            if (currentPage != 0) {
                Sort sort = new Sort(Sort.Direction.DESC, "createdTime");
                Pageable pageable = PageRequest.of(currentPage - 1, limit, sort);
                Page<Eip> page = eipRepository.findByProjectIdAndIsDeleteAndSbwId(projectId, 0, sbwId, pageable);
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
                List<Eip> eipList = eipRepository.findByProjectIdAndIsDeleteAndSbwId(projectId, 0, sbwId);
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
            return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.SC_FORBIDDEN.getCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception in listEips", e.getMessage());
            return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * rename sbw
     *
     * @return ret
     */
    public ResponseEntity renameSbw(String sbwId, SbwUpdateParam param) {
        try {
            if (StringUtils.isBlank(sbwId)){
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
            String projectId = CommonUtil.getProjectId();
            if (projectId == null) {
                return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_UNAUTHORIZED.getCode(),
                        ErrorStatus.ENTITY_UNAUTHORIZED.getMessage()), HttpStatus.BAD_REQUEST);
            }
            Sbw sbw = sbwDaoService.getSbwById(sbwId);
            if (null ==sbw){
                return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(),
                        ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage()), HttpStatus.BAD_REQUEST);
            }
            List<Eip> eipList = eipRepository.findByProjectIdAndIsDeleteAndBillType(projectId, 0, HsConstants.HOURLYSETTLEMENT);
            List<Eip> dataList = ListFilterUtil.filterListData(eipList, Sbw.class);
            JSONArray eips = new JSONArray();
            JSONObject data = new JSONObject();

            for (Eip eip : dataList) {
                //只要该eip加入了任何共享带宽，就不予以显示
                if (StringUtils.isNotBlank(eip.getSbwId())) {
                    continue;
                }
                EipV6 eipV6 = eipV6Repository.findByIpv4AndProjectIdAndIsDelete(eip.getEipAddress(), eip.getProjectId(), 0);
                if (eipV6 == null) {
                    EipReturnDetail eipReturn = new EipReturnDetail();
                    BeanUtils.copyProperties(eip, eipReturn);
                    eips.add(eipReturn);
                }
            }
            data.put("data", eips);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (KeycloakTokenException e) {
            return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.SC_FORBIDDEN.getCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception in get Other Eips", e.getMessage());
            return new ResponseEntity<>(ReturnMsgUtil.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
