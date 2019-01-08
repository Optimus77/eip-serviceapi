package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.sbw.SbwAllocateParamWrapper;
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

    JSONObject atomCreateSbw(SbwAllocateParamWrapper wrapper) {
        String url = sbwAtomUrl + "/eip/v1/sbws/";
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
            log.info("Send order to url:{}, sbwId:{}", url, sbwId);
            response = HttpUtil.delete(url, null);
        }catch (Exception e){
            log.error("Atom delete sbw exception", e);
        }
        return CommonUtil.handlerResopnse(response);
    }

//
//    public JSONObject listSbw(String pageIndex, String pageSize, String searchValue)  {
//        String url=sbwAtomUrl + "/eip/v1/sbws?"+"pageIndex="+pageIndex+"&pageSize="+pageSize+"&searchValue="+searchValue;
//        ReturnResult response = null;
//        try {
//            log.info("Send order to url:{}", url);
//            response = HttpUtil.get(url, null);
//        }catch (Exception e){
//            log.error("list sbw exception", e);
//        }
//        return CommonUtil.handlerResopnse(response);
//    }
//
//
//    public JSONObject getSbwByProjectId(String projectId)  {
//        String url=sbwAtomUrl + "/eip/v1/sbws?"+"projectId="+projectId;
//        ReturnResult response = null;
//        try {
//            log.info("Send order to url:{}", url);
//            response = HttpUtil.get(url, null);
//        }catch (Exception e){
//            log.error("list sbw exception", e);
//        }
//        return CommonUtil.handlerResopnse(response);
//    }
//
//
//
//    public JSONObject getSbwDetail(String id)  {
//        String url=sbwAtomUrl + "/eip/v1/sbws/"+id;
//        ReturnResult response = null;
//        try {
//            log.info("Send order to url:{}", url);
//            response = HttpUtil.get(url, null);
//        }catch (Exception e){
//            log.error("list sbw exception", e);
//        }
//        return CommonUtil.handlerResopnse(response);
//    }
//
//
//    public JSONObject getSbwCount()  {
//        String url=sbwAtomUrl + "/eip/v1/sbwnumbers";
//        ReturnResult response = null;
//        try {
//            log.info("Send order to url:{}", url);
//            response = HttpUtil.get(url, null);
//        }catch (Exception e){
//            log.error("list sbw exception", e);
//        }
//        return CommonUtil.handlerResopnse(response);
//    }
}
