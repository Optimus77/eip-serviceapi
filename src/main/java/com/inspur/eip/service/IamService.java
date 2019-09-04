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
                return ActionResponse.actionSuccess();
            }

            String action = getAction(reciveOrder.getOrderType(), reciveOrder.getOrderRoute());
            if (StringUtils.isBlank(action)) {
                ActionResponse.actionFailed("action can not be blank", HttpStatus.SC_BAD_REQUEST);
            }
            iamParam.setRegion(region);
            iamParam.setAction(action);
            if (action.equals(HsConstants.CREATE_EIP) || action.equals(HsConstants.CREATE_SBW)) {
                if(action.equals(HsConstants.CREATE_EIP)){
                    iamParam.setService(HsConstants.LOWERCASE_EIP);
                } else {
                    iamParam.setService(HsConstants.LOWERCASE_SBW);
                }
                iamParam.setInstanceId(null);
                iamParam.setResourceAccountId(CommonUtil.getProjectId(token));
                iamParam.setResourceCreator(CommonUtil.getProjectId(token));
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
            } else if (orderType.equals(HsConstants.CHANGECONFIGURE_ORDERTYPE)) {
                action = HsConstants.DELETE_EIP;
            } else {
                action = HsConstants.UPDATE_EIP;
            }
        } else {
            if (orderType.equals(HsConstants.NEW_ORDERTYPE)) {
                action = HsConstants.CREATE_SBW;
            } else if (orderType.equals(HsConstants.CHANGECONFIGURE_ORDERTYPE)) {
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
            if (eip.getUserId() != null && (CommonUtil.getUserId().equals(eip.getUserId()))){
                return HsConstants.CHILD_ENTITY;
            }
            if(eip.getUserId() != null && (!CommonUtil.getUserId().equals(eip.getUserId()))){
                iamParam.setResourceCreator(eip.getUserId());
                iamParam.setResourceAccountId(eip.getProjectId());
            }
            iamParam.setService(HsConstants.LOWERCASE_EIP);
            return HsConstants.OTHER_ENTITY;
        } else {
            Sbw sbw = sbwRepository.findByIdAndIsDelete(id, 0);
            if (sbw == null) {
                return HsConstants.NOTFOUND;
            }
            if (sbw.getUserId() != null && (CommonUtil.getUserId().equals(sbw.getUserId()))){
                return HsConstants.CHILD_ENTITY;
            }
            if(sbw.getUserId() != null && (!CommonUtil.getUserId().equals(sbw.getUserId()))){
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
        String url = "http://service.inspurtest.com/auth/v1/has-permissions";
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
        ReturnResult returnResult = HttpUtil.post(url, header, orderStr);
        String message= returnResult.getMessage().replace("[","");
        message = message.replace("]","");
        return message;
    }


    public static void main(String[] args) throws Exception {
       /* String token ="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiJkNDBkMDMwNi04ZmZmLTQyMDMtYTFjOC1iOWUxNDNkNTQ4OGYiLCJleHAiOjE1Njc1MDg1ODIsIm5iZiI6MCwiaWF0IjoxNTY3NDcyNTkzLCJpc3MiOiJodHRwczovL2F1dGguaW5zcHVydGVzdC5jb20vYXV0aC9yZWFsbXMvcGljcCIsImF1ZCI6ImNsaWVudC14dWV3ZWk4MSIsInN1YiI6IjYzMDhhNTg4LWVmYTYtNGY4Ni04YmYyLWI3YjY4ZGZkOGVjNiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjlmY2Y0NzEwLTc3ZDgtNDYxZi04YmE2LWQxYTdjYTRmNGY2NCIsImF1dGhfdGltZSI6MTU2NzQ3MjU4Miwic2Vzc2lvbl9zdGF0ZSI6IjQ4YTNhNjAxLTU2NWMtNDlmMy1iMDJmLWQ5OTI3NTlmMmZhNCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIkFDQ09VTlRfVVNFUiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC14dWV3ZWk4MSI6eyJyb2xlcyI6WyJ1c2VyIl19fSwic2NvcGUiOiJvcGVuaWQiLCJwaG9uZSI6IiIsInByb2plY3RfaWQiOiJjNGRjODFjNi1kYTg2LTQyN2UtYjlmOS02MGU0OTUwNjJhNzAiLCJjYW5NYW5hZ2VQd2QiOiIwIiwicHJvamVjdCI6Inh1ZXdlaTgxIiwiZ3JvdXBzIjpbIi9ncm91cC14dWV3ZWk4MSJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4dWV3ZWk4MS5saXV5cyJ9.kv49fj5BLbpTJ28P9A06gFJz8BrYZei-bsObnnQqYcUcdOEzh13s31NrFMH-gZqRKqIGCKRfvriTKiKi6ztet4xHKLVivNpYGlAmMmzsymHCM8G5DzyaD8oGVXphj1XSR_oDXkysFI1vLDec7BiJmKdUgGIieQSmaETWE8kNTwDyHz1DpjGMBR4rYiU6NqWC05kSrVpvSrPaVRqXvL1knn0NBBFzYeUyMrytFl-aDiQybKhuNFe_feBlFGjjhIfiajYJ5AvpZ4d6xh1sMbmK_CFoIM_nNSeIuBze7ex5uhTdbnJYZdPcw5ZB9dP_2Zj5IMtFPxA9iZCZwgTww4yh9Q";
        String url = "http://service.inspurtest.com/auth/v1/has-permissions";

        ContextIam contextIam = new ContextIam();
        contextIam.setAccountId(CommonUtil.getProjectId(token));

        IamParam iamParam = new IamParam();
        iamParam.setRegion("cn-north-3");
        iamParam.setService("eip");
        iamParam.setResourceType("instance");
        iamParam.setAction("UpdateEip");
        iamParam.setInstanceId("3e10e8c0-edaf-4602-8579-6bc0277bb769");
        iamParam.setContext(contextIam);
        iamParam.setResourceCreator(CommonUtil.getProjectId(token));
        iamParam.setResourceAccountId(CommonUtil.getProjectId(token));

        Map<String, String> header = new HashMap<String, String>();
        header.put("requestId", UUID.randomUUID().toString());
        header.put(HsConstants.AUTHORIZATION, "bearer " + token);
        header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");

        List<IamParam> list = new ArrayList();
        list.add(iamParam);
        String orderStr = JSONObject.toJSONString(list);
        ReturnResult returnResult = HttpUtil.post(url, header, orderStr);
        String message= returnResult.getMessage().replace("[","");
        message = message.replace("]","");
        System.out.println(message);*/
/*
        ReciveOrder reciveOrder=new ReciveOrder();
        OrderProduct orderProduct=new OrderProduct();
        List<OrderProduct> ProductList= new ArrayList<>();
        reciveOrder.setToken("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsY2hRX2ZrNFdHN0hCZFpmdkdRLUxxWTUwTWxVQVUwb1ZYUU1KcVF0UjNzIn0.eyJqdGkiOiI2Yjk5OGM2ZS0wZmVmLTRlODEtOGM3ZS01MTk1Y2I1OWNmMzUiLCJleHAiOjE1Njc1OTUyOTIsIm5iZiI6MCwiaWF0IjoxNTY3NTU5MzAwLCJpc3MiOiJodHRwczovL2F1dGguaW5zcHVydGVzdC5jb20vYXV0aC9yZWFsbXMvcGljcCIsImF1ZCI6ImNsaWVudC14dWV3ZWk4MSIsInN1YiI6IjYzMDhhNTg4LWVmYTYtNGY4Ni04YmYyLWI3YjY4ZGZkOGVjNiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNvbnNvbGUiLCJub25jZSI6IjlmY2Y0NzEwLTc3ZDgtNDYxZi04YmE2LWQxYTdjYTRmNGY2NCIsImF1dGhfdGltZSI6MTU2NzU1OTI5Miwic2Vzc2lvbl9zdGF0ZSI6IjIzODczMjYzLWZlM2QtNDFiYS1iMjBhLWE3MzQ2NGRlNWMwMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIkFDQ09VTlRfVVNFUiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNsaWVudC14dWV3ZWk4MSI6eyJyb2xlcyI6WyJ1c2VyIl19fSwic2NvcGUiOiJvcGVuaWQiLCJwaG9uZSI6IiIsInByb2plY3RfaWQiOiJjNGRjODFjNi1kYTg2LTQyN2UtYjlmOS02MGU0OTUwNjJhNzAiLCJjYW5NYW5hZ2VQd2QiOiIwIiwicHJvamVjdCI6Inh1ZXdlaTgxIiwiZ3JvdXBzIjpbIi9ncm91cC14dWV3ZWk4MSJdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ4dWV3ZWk4MS5saXV5cyJ9.HXN22-H9Gq6qgAtQmtgswhIE8bpMs5z1bbiEP9j8bAvLAIts_jA26ZBWlpmfFJKfkoWw1-mxOK-uh9mn9hDd2pMx9z32n2reUlIJIcHM5yxE0DhveF1SIUv3haZKZPPyjm-k_gSS0mXwFza8rxP3K_uJw2lXQmGYikuSDkBiugGyXIctK6d6cdZv1-_--NMdxz9rcQ-FnbclHZEpCW9w52adHxq2b-pTC-a3wAPEqpd_VtlBMjwDiZR4kqPi4PlHzmhZtV_RhOZkHcqMeQ_CRWHP4ATOXo8KrbIy8kbLHvZlaQ3AkyrDDn3NBuC8w_9GeRz70eIgqB40G0op8Vf0uQ");
        reciveOrder.setOrderType("changeConfigure");
        reciveOrder.setOrderRoute("EIP");

        orderProduct.setInstanceId("00825ebd-021c-478e-b942-0158f2785ebb");
        orderProduct.setRegion("cn-north-3");
        ProductList.add(orderProduct);
        reciveOrder.setProductList(ProductList);
        ActionResponse iamAuthority = isIamAuthority(reciveOrder);
        System.out.println(iamAuthority.toString());
*/


    }

}

