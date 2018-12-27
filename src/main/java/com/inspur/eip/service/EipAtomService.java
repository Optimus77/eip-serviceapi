package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipAllocateParam;
import com.inspur.eip.entity.EipAllocateParamWrapper;
import com.inspur.eip.entity.sbw.SbwAllocateParamWrapper;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HttpUtil;
import com.inspur.eip.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EipAtomService {

    @Value("${proxy.target_url}")
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
     * delete
     * @param sbwId id
     * @return json
     */
    JSONObject atomDeleteSbw(String  sbwId)  {
        String url=eipAtomUrl + "/v1/sbws/"+sbwId;
        ReturnResult response = null;
        try {
            log.info("Send order to url:{}, sbwId:{}", url, sbwId);
            response = HttpUtil.delete(url, null);
        }catch (Exception e){
            log.error("Atom delete sbw exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

    /**
     * update
     * @param eipId id
     * @param eipConfig config
     * @return json
     */
    JSONObject atomRenewEip(String eipId, EipAllocateParam eipConfig)  {
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
     * update
     * @param eipId id
     * @param eipConfig config
     * @return json
     */
    JSONObject atomUpdateEip(String eipId, EipAllocateParam eipConfig)  {
        String url=eipAtomUrl + "/v1/eips/" +eipId;
        ReturnResult response = null;
        try {
            EipAllocateParamWrapper eipConfigWrapper =  new EipAllocateParamWrapper();
            eipConfigWrapper.setEip(eipConfig);
            String orderStr = JSONObject.toJSONString(eipConfigWrapper);
            log.info("Send order to url:{}, body:{}", url, orderStr);

            response = HttpUtil.put(url, null, orderStr);
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

    JSONObject atomCreateSbw(SbwAllocateParamWrapper wrapper) {
        String url = eipAtomUrl + "/v1/sbws/";
        ReturnResult response = null;
        try {
            String orderStr = JSONObject.toJSONString(wrapper);
            log.info("Send order to url:{}, body:{}", url, orderStr);
            response = HttpUtil.post(url, null, orderStr);
        }catch (Exception e){
            log.error("Create sbw exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

}
