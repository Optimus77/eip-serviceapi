package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipAllocateParam;
import com.inspur.eip.entity.EipAllocateParamWrapper;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HttpUtil;
import com.inspur.eip.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EipAtomService {

    @Value("${proxy.servlet_url}")
    private String eipAtomUrl;
    /**
     * create
     * @param eipConfig config
     * @return json
     */
    JSONObject atomCreateEip(EipAllocateParamWrapper eipConfig) {
        String url = eipAtomUrl + "/v1/eips/";
        ReturnResult response = null;
        try {
            String orderStr = JSONObject.toJSONString(eipConfig);
            log.info("Send order to url:{}, body:{}", url, orderStr);
            response = HttpUtil.post(url, null, orderStr);
        }catch (Exception e){
            log.error("Create eip exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

    /**
     * delete
     * @param eipId id
     * @return json
     */
    JSONObject atomDeleteEip(String  eipId)  {
        String url=eipAtomUrl + "/v1/eips/"+eipId;
        ReturnResult response = null;
        try {
            log.info("Send order to url:{}, eipId:{}", url, eipId);
            response = HttpUtil.delete(url, null);
        }catch (Exception e){
            log.error("Atom delete eip exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

    /**
     * update
     * @param eipId id
     * @param eipConfig config
     * @return json
     */
    JSONObject atomUpdateEip(String eipId, EipAllocateParam eipConfig)  {
        String url=eipAtomUrl + "/v1/eips/" +eipId +"/renew";
        ReturnResult response = null;
        try {

            String orderStr = JSONObject.toJSONString(eipConfig);
            log.info("Send order to url:{}, body:{}", url, orderStr);

            response = HttpUtil.post(url, null, orderStr);
        }catch (Exception e){
            log.error("Update eip exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

    /**
     * get by id
     * @param eipId  id
     * @return json
     */
    JSONObject getEipEntityById(String eipId){

        String  uri =eipAtomUrl+ "/v1/eips/"+eipId;
        ReturnResult response = null;
        try {
            log.info(uri);
            response = HttpUtil.get(uri, null);
        }catch (Exception e){
            log.error("Get eip by id exception", e);
        }
        return  CommonUtil.handlerResopnse(response);
    }

    /**
     *  set log
     * @param requestBody request
     * @param pack pack
     * @return return
     */
    JSONObject setLogLevel(String requestBody, String pack) {
        String url = eipAtomUrl;
        ReturnResult response = null;
        try {
            url = url+"/v1/loggers/"+pack;
            log.info("Send order to url:{}, body:{}", url, requestBody);
            response = HttpUtil.post(url, null, requestBody);
        }catch (Exception e){
            log.error("Create eip exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }
}
