package com.inspur.eip.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.sbw.SbwAtomParamWrapper;
import com.inspur.eip.entity.sbw.SbwUpdateParamWrapper;
import com.inspur.eip.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SbwAtomService {
    @Value("${proxy.target_url}")
    private String sbwAtomUrl;
    @Autowired
    private RestTemplate restTemplate;

    ResponseEntity atomCreateSbw(SbwAtomParamWrapper wrapper) {

        String url = sbwAtomUrl + "/eip/v1/sbws";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String orderStr = JSONObject.toJSONString(wrapper);
            log.info("Send order to url:{}, body:{}", url, orderStr);
            return restTemplate.postForEntity(url, wrapper, JSONObject.class);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * delete
     * @param sbwId id
     * @return json
     */
    ResponseEntity atomDeleteSbw(String  sbwId)  {


        String url=sbwAtomUrl + "/eip/v1/sbws/"+sbwId;
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            log.info("Send order to url:{}, eipId:{}", url, sbwId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<>(sbwId, headers);
            return restTemplate.exchange(url, HttpMethod.DELETE, entity, JSONObject.class,sbwId);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    /**
     * update
     * @param sbwId id
     * @return json
     */
    ResponseEntity atomUpdateSbw(String sbwId, SbwUpdateParamWrapper wrapper)  {
        String url=sbwAtomUrl + "/eip/v1/sbws/"+sbwId+"/update";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String orderStr = JSONObject.toJSONString(wrapper);
            log.info("Update eip, url:{}, body:{}", url, orderStr);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(orderStr, headers);
            return restTemplate.exchange(url, HttpMethod.PUT, entity, JSONObject.class,sbwId);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param sbwId id
     * @return json
     */
    ResponseEntity atomRenewSbw(String sbwId, SbwUpdateParamWrapper wrapper)  {
        String url=sbwAtomUrl + "/eip/v1/sbws/" +sbwId +"/renew";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String orderStr = JSONObject.toJSONString(wrapper);
            log.info("Send order to url:{}, body:{}", url, orderStr);
            return restTemplate.postForEntity(url, wrapper, JSONObject.class);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
