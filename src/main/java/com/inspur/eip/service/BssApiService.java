package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;

import com.inspur.eip.entity.EipOrder;
import com.inspur.eip.entity.EipOrderResult;
import com.inspur.eip.entity.EipQuota;
import com.inspur.eip.entity.EipSoftDownOrder;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HttpUtil;
import com.inspur.eip.util.HttpsClientUtil;
import com.inspur.eip.util.ReturnResult;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BssApiService {

    private final static Logger log = LoggerFactory.getLogger(BssApiService.class);


    //1.2.8 订单接口POST
    @Value("${bssurl.submitPay}")
    private   String ordercreate;
    public JSONObject postOrder(EipOrder order)  {
        String url=ordercreate;

        String orderStr=JSONObject.toJSONString(order);
        log.info("SubmitPay url:{}, body:{}",url, orderStr);

        HttpResponse response=HttpUtil.post(url,null,orderStr);
        return CommonUtil.handlerResopnse(response);
    }


    //1.2.11	查询用户配额的接口 URL: http://117.73.2.105:8083/crm/quota
    @Value("${bssurl.quotaUrl}")
    private   String quotaUrl;
    public JSONObject getQuota(EipQuota quota){

        String  uri =quotaUrl+"?userId="+quota.getUserId()+"&region="+quota.getRegion()+"&productLineCode="
                +quota.getProductLineCode()+"&productTypeCode="+quota.getProductTypeCode()+"&quotaType=amount";
        log.info("Get quota: {}",uri);

        HttpResponse response= HttpUtil.get(uri,null);
        return CommonUtil.handlerResopnse(response);
    }

    //1.2.8 订单返回给控制台的消息
    @Value("${mq.returnMq}")
    private   String returnMq;
    public ReturnResult resultReturnMq(EipOrderResult orderResult)  {
        String url=returnMq;
        String orderStr=JSONObject.toJSONString(orderResult);
        log.info("ReturnMq Url:{} body:{}",url,orderStr);
        ReturnResult response= HttpsClientUtil.doPostJson(url,null,orderStr);
        log.info("Mq return:{}", response.getMessage());
        return response;
    }

    @Value("${mq.returnNotify}")
    private   String returnNotify;
    public ReturnResult resultReturnNotify(EipSoftDownOrder orderResult)  {
        String url=returnNotify;
        String orderStr=JSONObject.toJSONString(orderResult);
        log.info("ReturnNotify Url:{} body:{}",url,orderStr);
        ReturnResult response=HttpsClientUtil.doPostJson(url,null,orderStr);
        log.info("Notify return:{}", response.getMessage());
        return response;
    }



}
