package com.inspur.eip.util;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.bss.OrderProduct;
import com.inspur.eip.entity.bss.ReciveOrder;
import com.inspur.eip.entity.iam.ContextIam;
import com.inspur.eip.entity.iam.IamParam;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class AuthenticationUtil {


    public Boolean isIamAuthority (ReciveOrder reciveOrder, String token){
        try{
            String eipId = null;
            String region = null;
            String url="http://service.inspurtest.com/auth/v1/has-permissions";
            ContextIam contextIam = new ContextIam();
            contextIam.setAccountId(CommonUtil.getProjectId(token));
            contextIam.setCreator(CommonUtil.getUserId(token));
            IamParam iamParam = new IamParam();
            List<OrderProduct> orderProducts = reciveOrder.getProductList();
            for (OrderProduct orderProduct : orderProducts) {
                eipId = orderProduct.getInstanceId();
                region = orderProduct.getRegion();
            }
            iamParam.setRegion(region);
            iamParam.setService("iam");
            iamParam.setResourceAccountId(CommonUtil.getProjectId(token));
            iamParam.setResourceType("instance");
            String action =getAction(reciveOrder.getOrderType(),reciveOrder.getOrderRoute());
            if(StringUtils.isBlank(action)){
                return false;
            }
            iamParam.setAction(action);
            iamParam.setInstanceId(eipId);
            iamParam.setContext(contextIam);

            Map<String,String> header=new HashMap<String,String>();
            header.put("requestId", UUID.randomUUID().toString());
            header.put(HsConstants.AUTHORIZATION, "bearer "+token);
            header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");

            List<IamParam> list = new ArrayList();
            list.add(iamParam);
            String orderStr = JSONObject.toJSONString(list);
            ReturnResult returnResult = HttpUtil.post(url, header,orderStr);
            String message = returnResult.getMessage();
        }catch (Exception e){
                log.error("Iam authentication failed",e.getMessage());
                return false;
            }
        return true;
    }

    public String getAction (String orderType,String orderRoute){

        String action = null;
        if( orderRoute.equals("EIP")){
            if(orderType.equals("new")){
                action = "CreateEip";
            } else if (orderType.equals("changeConfigure")) {
                action = "DeleteEip";
            }else if(orderType.equals("unsubscribe")){
                action = "UpdateEip";
            }
        } else {
            if (orderType.equals("new")) {
                action = "CreateSbw";
            } else if (orderType.equals("changeConfigure")) {
                action = "DeleteSbw";
            } else {
                action = "UpdateSbw";
            }
        }
        return action;
    }
}
