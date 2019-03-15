package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.sbw.SbwAtomParam;
import com.inspur.eip.entity.sbw.SbwAtomParamWrapper;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.sbw.SbwUpdateParamWrapper;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HttpUtil;
import com.inspur.eip.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SbwAtomService {
    @Value("${proxy.target_url}")
    private String sbwAtomUrl;

    JSONObject atomCreateSbw(SbwAtomParamWrapper wrapper) {
        String url = sbwAtomUrl + "/eip/v1/sbws";
        ReturnResult response = null;
        try {
            String orderStr = JSONObject.toJSONString(wrapper);
            log.info("Send order to url:{}, body:{}", url, orderStr);
            response = HttpUtil.post(url, null, orderStr);
        } catch (Exception e) {
            log.error("Create sbw exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

    /**
     * delete
     * @param sbwId id
     * @return json
     */
    JSONObject atomDeleteSbw(String  sbwId)  {
        String url=sbwAtomUrl + "/eip/v1/sbws/"+sbwId;
        ReturnResult response = null;
        try {
            log.info("Send config to url:{}, sbwId:{}", url, sbwId);
            response = HttpUtil.delete(url, null);
        }catch (Exception e){
            log.error("Atom delete sbw exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }
    /**
     * update
     * @param sbwId id
     * @return json
     */
    JSONObject atomUpdateSbw(String sbwId, SbwUpdateParamWrapper wrapper)  {
        String url=sbwAtomUrl + "/eip/v1/sbws/" +sbwId+"/update";
        ReturnResult response = null;
        try {
            String orderStr = JSONObject.toJSONString(wrapper);
            log.info("Send config to url:{}, body:{}", url, orderStr);

            response = HttpUtil.put(url, null, orderStr);
        }catch (Exception e){
            log.error("Update sbw exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

    /**
     * update
     * @param sbwId id
     * @return json
     */
    JSONObject atomRenewSbw(String sbwId, SbwUpdateParamWrapper wrapper)  {
        String url=sbwAtomUrl + "/eip/v1/sbws/" +sbwId +"/renew";
        ReturnResult response = null;
        try {
            String orderStr = JSONObject.toJSONString(wrapper);
            log.info("Send config to url:{}, body:{}", url, orderStr);

            response = HttpUtil.post(url, null, orderStr);
        }catch (Exception e){
            log.error("Update sbw exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }
}
