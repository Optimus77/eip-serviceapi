package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.bss.OrderProduct;
import com.inspur.eip.entity.bss.ReciveOrder;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.iam.ContextIam;
import com.inspur.eip.entity.iam.IamParam;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.ReturnResult;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
@Slf4j
@Service
public class IamService {
    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private SbwRepository sbwRepository;

    @Value("${iamUrl}")
    private String iamUrl;

    public ActionResponse isIamAuthority(ReciveOrder reciveOrder) {
        String id = null;
        String region = null;
        try {
            IamParam iamParam = new IamParam();
            List<OrderProduct> orderProducts = reciveOrder.getProductList();
            String token =reciveOrder.getToken();
            for (OrderProduct orderProduct : orderProducts) {
                id = orderProduct.getInstanceId();
                region = orderProduct.getRegion();
            }
            //判断是子账号还是主账号 主账号直接通过
            if (CommonUtil.isParentOrChildAccount(token)) {
                log.info("The parent account");
                return ActionResponse.actionSuccess();
            }

            String action = getAction(reciveOrder.getOrderType(), reciveOrder.getOrderRoute());
            iamParam.setRegion(region);
            iamParam.setAction(action);
            iamParam.setInstanceId(id);
            if (action.equals(HsConstants.CREATE_EIP) || action.equals(HsConstants.CREATE_SBW)) {
                if(action.equals(HsConstants.CREATE_EIP)){
                    iamParam.setService(HsConstants.LOWERCASE_EIP);
                } else {
                    iamParam.setService(HsConstants.LOWERCASE_SBW);
                }
                String rts = rts(token, iamParam);
                if(rts.equals(HsConstants.FALSE)){
                    return ActionResponse.actionFailed("unauthorized", HttpStatus.SC_FORBIDDEN);
                }
                return ActionResponse.actionSuccess();
            } else {
                String parentOrChildEntity = isParentOrChildEntity(reciveOrder, id,iamParam);
                //判断实例是主账号还是子账号  子账号的实例直接通过
                if(parentOrChildEntity.equals(HsConstants.CHILD_ENTITY)) {
                    return ActionResponse.actionSuccess();
                } else if(parentOrChildEntity.equals(HsConstants.NOTFOUND)){
                    return ActionResponse.actionFailed("not find entity by id", HttpStatus.SC_NOT_FOUND);
                } else {
                    String rts = rts(token, iamParam);
                    if(rts.equals(HsConstants.FALSE)){
                        return ActionResponse.actionFailed("unauthorized", HttpStatus.SC_FORBIDDEN);
                    }
                }
            }
            return ActionResponse.actionSuccess();
        } catch (Exception e) {
            log.error("Iam authentication failed", e);
            return ActionResponse.actionFailed("unauthorized exception", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public String getAction(String orderType, String orderRoute) {

        String action = null;
        if (orderRoute.equals(HsConstants.EIP)) {
            if (orderType.equals(HsConstants.NEW_ORDERTYPE)) {
                action = HsConstants.CREATE_EIP;
            } else if (orderType.equals(HsConstants.UNSUBSCRIBE)) {
                action = HsConstants.DELETE_EIP;
            } else {
                action = HsConstants.UPDATE_EIP;
            }
        } else {
            if (orderType.equals(HsConstants.NEW_ORDERTYPE)) {
                action = HsConstants.CREATE_SBW;
            } else if (orderType.equals(HsConstants.UNSUBSCRIBE)) {
                action = HsConstants.DELETE_SBE;
            } else {
                action = HsConstants.UPDATE_SBW;
            }
        }
        return action;
    }



    public String isParentOrChildEntity(ReciveOrder reciveOrder,String id,IamParam iamParam) throws KeycloakTokenException {
        if (reciveOrder.getOrderRoute().equals(HsConstants.EIP)) {
            Eip eip = eipRepository.findByIdAndIsDelete(id, 0);
            if (eip == null) {
                return HsConstants.NOTFOUND;
            }
            if (eip.getUserId() != null && (CommonUtil.getUserId(reciveOrder.getToken()).equals(eip.getUserId()))){
                log.info(HsConstants.CHILD_ENTITY);
                return HsConstants.CHILD_ENTITY;
            }
            if(eip.getUserId() != null && (!CommonUtil.getUserId(reciveOrder.getToken()).equals(eip.getUserId()))){
                iamParam.setResourceCreator(eip.getUserId());
                iamParam.setResourceAccountId(eip.getProjectId());
            } else {
                iamParam.setResourceCreator(eip.getProjectId());
                iamParam.setResourceAccountId(eip.getProjectId());
            }
            iamParam.setService(HsConstants.LOWERCASE_EIP);
            return HsConstants.OTHER_ENTITY;
        } else {
            Sbw sbw = sbwRepository.findByIdAndIsDelete(id, 0);
            if (sbw == null) {
                return HsConstants.NOTFOUND;
            }
            if (sbw.getUserId() != null && (CommonUtil.getUserId(reciveOrder.getToken()).equals(sbw.getUserId()))){
                log.info(HsConstants.CHILD_ENTITY);
                return HsConstants.CHILD_ENTITY;
            }
            if(sbw.getUserId() != null && (!CommonUtil.getUserId(reciveOrder.getToken()).equals(sbw.getUserId()))){
                iamParam.setResourceCreator(sbw.getUserId());
                iamParam.setResourceAccountId(sbw.getProjectId());
            } else {
                iamParam.setResourceCreator(sbw.getProjectId());
                iamParam.setResourceAccountId(sbw.getProjectId());
            }
            iamParam.setService(HsConstants.LOWERCASE_SBW);
            return HsConstants.OTHER_ENTITY;
        }
    }

    public String rts(String token,IamParam iamParam) throws Exception {
        iamParam.setResourceType("instance");
        ContextIam contextIam = new ContextIam();
        contextIam.setAccountId(CommonUtil.getProjectId(token));
        iamParam.setContext(contextIam);

        Map<String, String> header = new HashMap<>();
        header.put("requestId", UUID.randomUUID().toString());
        header.put(HsConstants.AUTHORIZATION, "bearer " + token);
        header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");

        List<IamParam> list = new ArrayList();
        list.add(iamParam);
        String orderStr = JSONObject.toJSONString(list);
        log.info("--------------Iam authentication parameters :{}",orderStr);
        ReturnResult returnResult = HttpUtil.post(iamUrl, header, orderStr);
        String message= returnResult.getMessage().replace("[","");
        message = message.replace("]","");
        return message;
    }

}

