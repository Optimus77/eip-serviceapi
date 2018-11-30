package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipAllocateParam;
import com.inspur.eip.entity.EipAllocateParamWrapper;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HttpUtil;
import com.inspur.eip.util.ReturnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EipAtomService {

    public final static Logger log = LoggerFactory.getLogger(EipAtomService.class);
    //1.2.8 订单接口POST
    @Value("${eipAtom}")
    private String eipAtomUrl;

    /**
     * create
     * @param eipConfig config
     * @return json
     */
    public JSONObject atomCreateEip(EipAllocateParamWrapper eipConfig) {
        String url = eipAtomUrl;
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
    public JSONObject atomDeleteEip(String  eipId)  {
        String url=eipAtomUrl +eipId;
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
    public JSONObject atomUpdateEip(String eipId, EipAllocateParam eipConfig)  {
        String url=eipAtomUrl+eipId +"/renew";
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
    public JSONObject getEipEntityById(String eipId){

        String  uri =eipAtomUrl+eipId;
        ReturnResult response = null;
        try {
            log.info(uri);
            response = HttpUtil.get(uri, null);
        }catch (Exception e){
            log.error("Get eip by id exception", e);
        }
        return  CommonUtil.handlerResopnse(response);
    }
}
