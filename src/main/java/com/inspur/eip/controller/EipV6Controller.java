package com.inspur.eip.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.v2.eipv6.EipV6AllocateParamWrapper;
import com.inspur.eip.entity.ipv6.EipV6UpdateParamWrapper;
import com.inspur.eip.util.CustomException;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.ThrowErrorHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
@Api(value = "/v1", description = "eipv6 API")
@Validated
public class EipV6Controller {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${proxy.target_url}")
    private String eipV6AtomUrl;

    @PostMapping(value = "/eipv6")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity allocateEipV6(@Valid @RequestBody EipV6AllocateParamWrapper eipV6Config) {
        log.info("————create Eipv6 service api ————");
        String url = eipV6AtomUrl + "/eip/v1/eipv6";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String orderStr = JSONObject.toJSONString(eipV6Config);
            log.info("create eipv6  service api   url:{}, body:{}", url, orderStr);
            return restTemplate.postForEntity(url, eipV6Config, JSONObject.class);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/eipv6")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listeipv6",notes="list")
    public ResponseEntity listEipV6(@RequestParam(required = false) String currentPage ,
                                    @RequestParam(required = false )String limit,
                                    @RequestParam(required = false )String status) {
        log.debug("————get listEipv6 service api ————");

        String  uri =eipV6AtomUrl + "/eip/v1/eipv6?currentPage={currentPage}&limit={limit}&status={status}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String,Object> map = new HashMap();
            map.put("currentPage",currentPage);
            map.put("limit",limit);
            map.put("status",status);
            return restTemplate.getForEntity(uri, JSONObject.class,map );

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping(value = "/eipv6/{eipv6_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    public ResponseEntity deleteEip(@Size(min=36, max=36, message = "Must be uuid.")
                                    @PathVariable("eipv6_id") String eipV6Id) {
        log.info("————service delete the EipV6Id :{} ",eipV6Id);
        String url=eipV6AtomUrl + "/eip/v1/eipv6/"+eipV6Id;
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<>(eipV6Id, headers);
            return restTemplate.exchange(url, HttpMethod.DELETE, entity, JSONObject.class,eipV6Id);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    /**
     * get eipV6 instance detail
     *
     * @param eipV6Id the id of eipV6
     * @return retrun
     */
    @GetMapping(value = "/eipv6/{eipv6_id}")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "geteipv6Detail", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eipv6_id", value = "the id of eipv6", required = true, dataType = "String"),
    })
    public ResponseEntity geteipV6Detail(@PathVariable("eipv6_id") String eipV6Id) {

        log.info("————get EipV6Detail service api———— ");

        String  uri =eipV6AtomUrl + "/eip/v1/eipv6/{eipId}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class,eipV6Id );

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping(value = "/eipv6/{eipv6_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eipv6", notes = "put")
    public ResponseEntity updateEip(@PathVariable("eipv6_id") String eipV6Id, @Valid  @RequestBody (required = false) EipV6UpdateParamWrapper param) {
        log.info("————update eipv6 service api———— ");
        String  uri =eipV6AtomUrl + "/eip/v1/eipv6/{eipId}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String params = JSONObject.toJSONString(param);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(params, headers);
            return restTemplate.exchange(uri, HttpMethod.PUT, entity, Object.class, eipV6Id);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
