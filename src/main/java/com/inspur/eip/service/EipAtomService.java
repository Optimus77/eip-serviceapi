package com.inspur.eip.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipAllocateParamWrapper;
import com.inspur.eip.entity.EipUpdateParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
    ResponseEntity atomCreateEip(EipAllocateParamWrapper eipConfig) {
        String url = "http://localhost:8080/eip/v1/eips";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String orderStr = JSONObject.toJSONString(eipConfig);
            log.info("Send order to url:{}, body:{}", url, orderStr);
            return restTemplate.postForEntity(url, eipConfig, JSONObject.class);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete
     * @param eipId id
     * @return json
     */
    ResponseEntity atomDeleteEip(String  eipId)  {
        String url="http://localhost:8080/eip/v1/eips/"+eipId;
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            log.info("Send order to url:{}, eipId:{}", url, eipId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<>(eipId, headers);
            return restTemplate.exchange(url, HttpMethod.DELETE, entity, JSONObject.class,eipId);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * update
     * @param eipId id
     * @param eipConfig config
     * @return json
     */
    ResponseEntity atomRenewEip(String eipId, EipUpdateParam eipConfig)  {
        String url="http://localhost:8080/eip/v1/eips/"+eipId+"/renew";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String orderStr = JSONObject.toJSONString(eipConfig);
            log.info("Renew eip, url:{}, body:{}", url, orderStr);
            return restTemplate.postForEntity(url, orderStr, JSONObject.class,eipId);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * update
     * @param eipId id
     * @param eipConfig config
     * @return json
     */
    ResponseEntity atomUpdateEip(String eipId, EipUpdateParam eipConfig)  {
        String url="http://localhost:8080/eip/v1/eips/"+eipId;
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            EipUpdateParamWrapper eipConfigWrapper =  new EipUpdateParamWrapper();
            eipConfigWrapper.setEip(eipConfig);
            String orderStr = JSONObject.toJSONString(eipConfigWrapper);
            log.info("Update eip, url:{}, body:{}", url, orderStr);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(orderStr, headers);
            return restTemplate.exchange(url, HttpMethod.PUT, entity, JSONObject.class,eipId);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * get by id
     * @param eipId  id
     * @return json
     */
    ResponseEntity getEipEntityById(String eipId){

        String  uri =eipAtomUrl+ "/eip/v1/eips/"+eipId;

        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class ,eipId);

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
