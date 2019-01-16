package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipAllocateParam;
import com.inspur.eip.entity.EipAllocateParamWrapper;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HttpUtil;
import com.inspur.eip.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class EipAtomService {

    @Value("${proxy.target_url}")
    private String eipAtomUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * create
     * @param eipConfig config
     * @return json
     */
    JSONObject atomCreateEip(EipAllocateParamWrapper eipConfig) {
        String url = eipAtomUrl + "/eip/v1/eips/";
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
        String url=eipAtomUrl + "/eip/v1/eips/"+eipId;
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
    JSONObject atomRenewEip(String eipId, EipUpdateParam eipConfig)  {
        String url=eipAtomUrl + "/eip/v1/eips/" +eipId +"/renew";
        ReturnResult response = null;
        try {

            String orderStr = JSONObject.toJSONString(eipConfig);
            log.info("Renew eip, url:{}, body:{}", url, orderStr);

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
    JSONObject atomUpdateEip(String eipId, EipUpdateParam eipConfig)  {
        String url=eipAtomUrl + "/eip/v1/eips/" +eipId;
        ReturnResult response = null;
        try {
            EipUpdateParamWrapper eipConfigWrapper =  new EipUpdateParamWrapper();
            eipConfigWrapper.setEip(eipConfig);
            String orderStr = JSONObject.toJSONString(eipConfigWrapper);
            log.info("Update eip, url:{}, body:{}", url, orderStr);

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

        String  uri =eipAtomUrl+ "/eip/v1/eips/"+eipId;
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
