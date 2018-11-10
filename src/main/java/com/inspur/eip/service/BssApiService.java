package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;

import com.inspur.eip.entity.EipOrder;
import com.inspur.eip.entity.EipQuota;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class BssApiService {

    private final static Logger log = LoggerFactory.getLogger(BssApiService.class);

    @Value("${bssURL.host}")
    private   String host;
    @Value("${bssURL.port}")
    private   String port;
    @Value("${bssURL.ignoSSL}")
    private   boolean ignoSSL;

    private String getURL(){

        if(ignoSSL){
            return "http://"+host+":"+port;
        }else{
            return "https://"+host+":"+port;
        }
    }


    private  Map<String,String> getHeader(){
        Map<String,String> header=new HashMap<String,String>();
        header.put("requestId",UUID.randomUUID().toString());
        header.put(HsConstants.AUTHORIZATION, CommonUtil.getKeycloackToken());
        header.put(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
        header.put(HsConstants.HILLTONE_LANGUAGE, HsConstants.LANG);
        return header;
    }
 

    //1.2.1 查询当前用户余额
    @Value("${bssURL.userBalanceURL}")
    private   String userBalanceURL;
    public JSONObject getUserBalance(String userid){
        String  uri=userBalanceURL+"/crm/quota";
        log.info(uri);
        Map<String,String> header= getHeader();
        HttpResponse response= HttpUtil.get(uri,header);
        return handlerResopnse(response);
    }


    //1.2.8 订单接口POST
    @Value("${bssURL.ordercreate}")
    private   String ordercreate;
    public JSONObject createOrder(EipOrder order)  {
        String url=ordercreate;

        Map<String,String> header= getHeader();
        String orderStr=JSONObject.toJSONString(order);
        log.info("Send order to url:{}, body:{}",url, orderStr);

        HttpResponse response=HttpUtil.post(url,header,orderStr);
        return handlerResopnse(response);
    }


    //1.2.11	查询用户配额的接口 URL: http://117.73.2.105:8083/crm/quota
    @Value("${bssURL.quotaUrl}")
    private   String quotaUrl;
    public JSONObject getQuota(EipQuota quota){

        String  uri =quotaUrl+"?userId="+quota.getUserId()+"&region="+quota.getRegion()+"&productLineCode="
                +quota.getProductLineCode()+"&productTypeCode="+quota.getProductTypeCode()+"&quotaType=amount";
        log.info(uri);
        Map<String,String> header= getHeader();
        HttpResponse response= HttpUtil.get(uri,header);
        return handlerResopnse(response);
    }


    private JSONObject handlerResopnse(HttpResponse response){
        JSONObject result=new JSONObject();
        StringBuffer sb= new StringBuffer("");
        if(response!=null){
            BufferedReader in=null;
            try{
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                JSONObject returnInfo=JSONObject.parseObject(sb.toString());
                log.info("BSS RETURN ==>{}",returnInfo);
                if(null != returnInfo && returnInfo.containsKey("code")){
                    if(returnInfo.getInteger("code")==0){
                        result.put(HsConstants.SUCCESS,true);
                        result.put("data",returnInfo.get("result"));
                    }else{
                        result.put(HsConstants.SUCCESS,false);
                        result.put("data",returnInfo);
                    }
                }else{
                    result.put(HsConstants.SUCCESS,false);
                    result.put("data",returnInfo);
                }
            }catch(Exception e){
                log.error("Recieve resopnse exeception", e);
                result.put(HsConstants.SUCCESS,false);
                result.put("data",e.getMessage());
            }
        }else{
            result.put(HsConstants.SUCCESS,false);
            result.put("data",sb.toString());
        }
        return result;
    }
    



}
