package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.*;
import com.inspur.eip.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
class WebControllerService {

    @Autowired
    private ClientTokenUtil clientTokenUtil;

    @Value("${mq.webSocket}")
    private String pushMq;

    @Value("${mq.returnNotify}")
    private   String returnNotify;

    @Value("${mq.returnMq}")
    private   String returnMq;

    /**
     *  websocket return
     * @param eipId  id
     * @param eipOrder  order
     * @param type type
     */
    void returnsWebsocket(String eipId, ReciveOrder eipOrder, String type){
            try {
                WebSocketEntity wbEntity = new WebSocketEntity();
                wbEntity.setUserName(CommonUtil.getUsername(eipOrder.getToken()));
                wbEntity.setHandlerName("operateEipHandler");
                wbEntity.setInstanceId(eipId);
                wbEntity.setInstanceStatus("active");
                wbEntity.setOperateType(type);
                wbEntity.setMessageType("success");
                wbEntity.setMessage("Flexible public network IP updated successfully");
                String url=pushMq;
                String orderStr=JSONObject.toJSONString(wbEntity);
                log.info("websocket send return: {} {}", url, orderStr);
                Map<String, String> header = this.getHeader(eipOrder.getToken());
                ReturnResult response = HttpsClientUtil.doPostJson(url,header,orderStr);
                log.debug("websocket respons:{}", response.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
    void returnsIpv6Websocket( String result, String type, String token){
        try {
            WebSocketEntity wbEntity = new WebSocketEntity();
            wbEntity.setUserName(CommonUtil.getUsername(token));
            wbEntity.setHandlerName("operateNatHandler");
            wbEntity.setOperateType(type);
            String retMessage;
            if(type.equalsIgnoreCase("createNatWithEip")) {
                retMessage = "createNat" + result + "&" + "createEIP" + result;
            }else {
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
        header.put(HsConstants.AUTHORIZATION, token);
        header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
        return header;
    }
    /**
     * sbw webSocket
     * @param sbwId id
     * @param reciveOrder order
     * @param type tyep
     */
    void returnSbwWebsocket(String sbwId, ReciveOrder reciveOrder, String type){

            try {
                WebSocketEntity wbEntity = new WebSocketEntity();
                wbEntity.setUserName(CommonUtil.getUsername(reciveOrder.getToken()));
                wbEntity.setHandlerName("operateSbwHandler");
                wbEntity.setInstanceId(sbwId);
                wbEntity.setInstanceStatus("active");
                wbEntity.setOperateType(type);
                wbEntity.setMessageType("success");
                wbEntity.setMessage("Config update successfully");
                String url=pushMq;
                String socketStr=JSONObject.toJSONString(wbEntity);
                log.info("websocket send return: {} {}", url, socketStr);
                Map<String, String> header = this.getHeader(reciveOrder.getToken());
                ReturnResult response = HttpsClientUtil.doPostJson(url,header,socketStr);
                log.debug("websocket respons:{}", response.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
