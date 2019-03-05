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


@Service
@Slf4j
class WebControllerService {

    @Autowired
    private ClientTokenUtil clientTokenUtil;

    @Value("${mq.webSocket}")
    private String pushMq;

    @Value("${mq.returnNotify}")
    private   String returnNotify;

    //1.2.8 订单接口POST
    @Value("${bssurl.submitPay}")
    private   String ordercreate;

    @Value("${mq.returnMq}")
    private   String returnMq;

    /**
     * send order to controller
     * @param order order
     * @return code and message
     */
    ReturnResult postOrder(EipReciveOrder order)  {
        String url=ordercreate;
        ReturnResult response;
        try {
            String orderStr = JSONObject.toJSONString(order);
            Map<String, String> header = new HashMap<>();
            header.put(HsConstants.AUTHORIZATION, "bearer "+ clientTokenUtil.getAdminToken().trim());
            header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");

            log.info("SubmitPay url:{}, body:{}", url, orderStr);
            if ((url.trim().startsWith("https://")) || (url.trim().startsWith("HTTPS://"))) {
                response = HttpsClientUtil.doPostJson(url, header, orderStr);
            } else {
                response = HttpUtil.post(url, header, orderStr);
            }
            return  response;
        }catch (Exception e){
            log.error("In submitpay order, get token exception:{}", e);
        }
        return ReturnResult.actionFailed("Post order failed ", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }


    /**
     * 订单返回给控制台的消息
     * @param orderResult  result
     * @return return
     */
    ReturnResult resultReturnMq(EipOrderResult orderResult)   {
        String url=returnMq;
        String orderStr=JSONObject.toJSONString(orderResult);
        try {
            Map<String, String> header = new HashMap<>();
            header.put(HsConstants.AUTHORIZATION, "bearer "+ clientTokenUtil.getAdminToken().trim());
            header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");

            log.info("ReturnMq Url:{} body:{}", url, orderStr);
            return  HttpsClientUtil.doPostJson(url, header, orderStr);
        }catch (Exception e){
            log.error("In return mq, get token exception:{}", e);
        }
        return ReturnResult.actionFailed("Return mq failed ", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     *  notify
     * @param orderResult result
     * @return code and message
     */
    ReturnResult resultReturnNotify(EipSoftDownOrder orderResult)  {
        String url=returnNotify;
        try {
            Map<String, String> header = new HashMap<>();
            header.put(HsConstants.AUTHORIZATION, clientTokenUtil.getAdminToken());
            header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");

            String orderStr = JSONObject.toJSONString(orderResult);
            log.info("ReturnNotify Url:{} body:{}", url, orderStr);
            return   HttpsClientUtil.doPostJson(url, null, orderStr);
        }catch (Exception e){
            log.error("In return from notify mq, get token exception:{}", e);
        }
        return ReturnResult.actionFailed("Notify failed ", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }


    /**
     *  websocket return
     * @param eipId  id
     * @param eipOrder  order
     * @param type type
     */
    void returnsWebsocket(String eipId, EipReciveOrder eipOrder, String type){
            try {
                SendMQEIP sendMQEIP = new SendMQEIP();
                sendMQEIP.setUserName(CommonUtil.getUsername());
                sendMQEIP.setHandlerName("operateEipHandler");
                sendMQEIP.setInstanceId(eipId);
                sendMQEIP.setInstanceStatus("active");
                sendMQEIP.setOperateType(type);
                sendMQEIP.setMessageType("success");
                sendMQEIP.setMessage("Flexible public network IP updated successfully");
                String url=pushMq;
                String orderStr=JSONObject.toJSONString(sendMQEIP);
                log.info("websocket send return: {} {}", url, orderStr);
                ReturnResult response = HttpsClientUtil.doPostJson(url,null,orderStr);
                log.debug("websocket respons:{}", response.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    /**
     * sbw webSocket
     * @param sbwId id
     * @param eipReciveOrder order
     * @param type tyep
     */
    void returnSbwWebsocket(String sbwId, EipReciveOrder eipReciveOrder, String type){

            try {
                SendMQEIP sendMQEIP = new SendMQEIP();
                sendMQEIP.setUserName(CommonUtil.getUsername());
                sendMQEIP.setHandlerName("operateEipHandler");
                sendMQEIP.setInstanceId(sbwId);
                sendMQEIP.setInstanceStatus("active");
                sendMQEIP.setOperateType(type);
                sendMQEIP.setMessageType("success");
                sendMQEIP.setMessage("sbw create successfully");
                String url=pushMq;
                String socketStr=JSONObject.toJSONString(sendMQEIP);
                log.info("websocket send return: {} {}", url, socketStr);
                ReturnResult response = HttpsClientUtil.doPostJson(url,null,socketStr);
                log.debug("websocket respons:{}", response.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    /**
     * 订单返回给控制台的消息
     * @param eipOrderResult  result
     * @return return
     */
    ReturnResult resultSbwReturnMq(EipOrderResult eipOrderResult)   {
        String url=returnMq;
        String mqStr=JSONObject.toJSONString(eipOrderResult);
        try {
            Map<String, String> header = new HashMap<>();
            header.put(HsConstants.AUTHORIZATION, "bearer "+ clientTokenUtil.getAdminToken().trim());
            header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");

            log.info("ReturnMq Url:{} body:{}", url, mqStr);
            return  HttpsClientUtil.doPostJson(url, header, mqStr);
        }catch (Exception e){
            log.error("In return mq, get token exception:{}", e);
        }
        return ReturnResult.actionFailed("Return mq failed ", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
