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

    @Value("${proxy.target_url}")
    private String eipAtomUrl;
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
    JSONObject atomRenewEip(String eipId, EipAllocateParam eipConfig)  {
        String url=eipAtomUrl + "/eip/v1/eips/" +eipId +"/renew";
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
        String url=eipAtomUrl + "/eip/v1/eips/" +eipId;
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
//
//    public ReturnResult getServerList(String region, String tag){
//
//        String  uri =eipAtomUrl+ "/eip/v1/servers?region="+region+"&tag="+tag;
//        ReturnResult response = null;
//        try {
//            log.info(uri);
//            response = HttpUtil.get(uri, null);
//        }catch (Exception e){
//            log.error("Get eip by id exception", e);
//        }
//        return  response;
//    }
//
//
//    public ReturnResult getEipNumbers(){
//
//        String  uri =eipAtomUrl+ "/eip/v1/numbers";
//        ReturnResult response = null;
//        try {
//            log.info(uri);
//            response = HttpUtil.get(uri, null);
//        }catch (Exception e){
//            log.error("Get eip by id exception", e);
//        }
//        return  response;
//    }
//
//    public ReturnResult delEipList(EipDelParam param){
//
//        String  uri =eipAtomUrl+ "/eip/v1/deleiplist";
//        ReturnResult response = null;
//        try {
//            response = HttpUtil.post(uri, null, param.toString());
//        }catch (Exception e){
//            log.error("Get eip by id exception", e);
//        }
//        return response;
//    }


}
