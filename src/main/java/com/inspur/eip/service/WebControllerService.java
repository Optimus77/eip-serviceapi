package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.*;
import com.inspur.eip.entity.bss.OrderProductItem;
import com.inspur.eip.entity.bss.ReciveOrder;
import com.inspur.eip.entity.eip.EipReturnBase;
import com.inspur.eip.util.*;
import com.inspur.eip.util.common.ClientTokenUtil;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.http.HttpsClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
public class WebControllerService {

    @Autowired
    private ClientTokenUtil clientTokenUtil;

    @Value("${webSocket}")
    private String pushMq;

    public void retWebsocket(String isIpv6, ReciveOrder eipOrder, String ipv6Type,int retCode){
        String result;
        if(retCode != HttpStatus.SC_OK){
            result = "false";
        }else {
            result = "Success";
        }
        if (null != isIpv6 && isIpv6.equalsIgnoreCase("yes")) {
            if(retCode == 420){
                ipv6Type = "createEip";
            }
            returnsIpv6Websocket(result, ipv6Type, eipOrder.getToken());
        }
    }

    public void returnsIpv6Websocket(String result, String type, String token){
        try {
            WebSocketEntity wbEntity = new WebSocketEntity();
            wbEntity.setUserName(CommonUtil.getUsername(token));
            wbEntity.setHandlerName("operateNatHandler");
            wbEntity.setOperateType(type);
            String retMessage;
            if(type.equalsIgnoreCase("createNatWithEip")) {
                retMessage = "createNat" + result + "&" + "createEIP" + result;
            }else if (type.equalsIgnoreCase("createEip")){
                retMessage = "createNat" + result + "&" + "createEIP success";
            }else{
                retMessage = "deleteNat" + result + "&" + "deleteEIP" + result;
            }
            wbEntity.setMessage(retMessage);
            String url=pushMq;
            String orderStr=JSONObject.toJSONString(wbEntity);
            log.info("websocket send return: {} {}", url, orderStr);
            Map<String, String> header = this.getHeader(token);
            ReturnResult response = HttpsClientUtil.doPostJson(url,header,orderStr);
            log.debug("websocket respons:{}", response.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get token from mq
     * @param token
     * @return
     */
    private  Map<String,String> getHeader(String token){
        Map<String,String> header=new HashMap<String,String>();
        header.put("requestId", UUID.randomUUID().toString());
        header.put(HsConstants.AUTHORIZATION, "bearer "+token);
        header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
        return header;
    }
//    /**
//     * sbw webSocket
//     * @param sbwId id
//     * @param reciveOrder order
//     * @param type tyep
//     */
//    public void returnSbwWebsocket(String sbwId, ReciveOrder reciveOrder, String type){
//
//            try {
//                WebSocketEntity wbEntity = new WebSocketEntity();
//                wbEntity.setUserName(CommonUtil.getUsername(reciveOrder.getToken()));
//                wbEntity.setHandlerName("operateSbwHandler");
//                wbEntity.setInstanceId(sbwId);
//                wbEntity.setInstanceStatus("active");
//                wbEntity.setOperateType(type);
//                wbEntity.setMessageType("success");
//                wbEntity.setMessage("Config update successfully");
//                String url=pushMq;
//                String socketStr=JSONObject.toJSONString(wbEntity);
//                log.info("websocket send return: {} {}", url, socketStr);
//                Map<String, String> header = this.getHeader(reciveOrder.getToken());
//                ReturnResult response = HttpsClientUtil.doPostJson(url,header,socketStr);
//                log.debug("websocket respons:{}", response.getMessage());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    /**
//     *  websocket return
//     * @param eipId  id
//     * @param eipOrder  order
//     * @param type type
//     */
//    public void returnsWebsocket(String eipId, ReciveOrder eipOrder, String type){
//        try {
//            WebSocketEntity wbEntity = new WebSocketEntity();
//            wbEntity.setUserName(CommonUtil.getUsername(eipOrder.getToken()));
//            wbEntity.setHandlerName("operateEipHandler");
//            wbEntity.setInstanceId(eipId);
//            wbEntity.setInstanceStatus("active");
//            wbEntity.setOperateType(type);
//            wbEntity.setMessageType("success");
//            wbEntity.setMessage("Flexible public network IP updated successfully");
//            String url=pushMq;
//            String orderStr=JSONObject.toJSONString(wbEntity);
//            log.info("websocket send return: {} {}", url, orderStr);
//            Map<String, String> header = this.getHeader(eipOrder.getToken());
//            ReturnResult response = HttpsClientUtil.doPostJson(url,header,orderStr);
//            log.debug("websocket respons:{}", response.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    public void returnsWebsocketV2(ReciveOrder order, String result){
        try {
            String type = order.getOrderType();
            if(type.equalsIgnoreCase(HsConstants.NEW_ORDERTYPE)){
                type = "create";
            }else if(type.equalsIgnoreCase(HsConstants.CHANGECONFIGURE_ORDERTYPE)){
                type = "update";
            }else if(type.equalsIgnoreCase(HsConstants.UNSUBSCRIBE_ORDERTYPE)){
                type= "delete";
            }else{
                return;
            }
            String name = order.getOrderRoute();
            String msg ;
            if(name.equalsIgnoreCase(HsConstants.EIP)){
                name = "operateEipHandler";
                msg = "Flexible public network IP updated successfully";
            }else{
                name = "operateSbwHandler";
                msg = "Config update successfully";
            }
            String instanceId = "";
            if(result.equalsIgnoreCase(HsConstants.SUCCESS)) {
                if (order.getConsoleCustomization().containsKey("groupId")) {
                    List<OrderProductItem> productItems = order.getProductList().get(0).getItemList();
                    for (OrderProductItem item : productItems) {
                        if (item.getCode().equalsIgnoreCase("groupId")) {
                            instanceId = item.getValue();
                        }
                    }
                } else {
                    instanceId = order.getProductList().get(0).getInstanceId();
                }
            }
            WebSocketEntity wbEntity = new WebSocketEntity();
            wbEntity.setUserName(CommonUtil.getUsername(order.getToken()));
            wbEntity.setHandlerName(name);
            wbEntity.setInstanceId(instanceId);
            wbEntity.setInstanceStatus("active");
            wbEntity.setOperateType(type);
            wbEntity.setMessageType(result);
            wbEntity.setMessage(msg);
            String url=pushMq;
            String orderStr=JSONObject.toJSONString(wbEntity);
            log.info("websocket send return: {} {}", url, orderStr);
            Map<String, String> header = this.getHeader(order.getToken());
            ReturnResult response = HttpsClientUtil.doPostJson(url,header,orderStr);
            log.debug("websocket respons:{}", response.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
