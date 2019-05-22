package com.inspur.eip.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipDelParam;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.entity.LogLevel;
import com.inspur.eip.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;


@Slf4j
@RestController
@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
@Api(value = "/v1", description = "eip API")
@Validated
public class EipController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${proxy.target_url}")
    private String eipAtomUrl;


    @GetMapping(value = "/health-status")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "health check")
    public ResponseEntity EipHealthCheck() {

        return new ResponseEntity<>("Eip is running", HttpStatus.OK);
    }


    @CrossOrigin(origins = "*",maxAge = 3000)
    @PostMapping(value = "/loggers/{package}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setDebugLevel(@PathVariable("package") String packageName, @RequestBody LogLevel requestBody) {
        log.info("————Set debug level to:{}", requestBody);


        String debugLevel = requestBody.getLevel();

        try{
            Level level = Level.toLevel(debugLevel);
            Logger logger = LogManager.getLogger(packageName);
            logger.setLevel(level);
        }catch (Exception e){
            log.error("Set log level error", e);
        }
        return new ResponseEntity<>(ReturnMsgUtil.success(), HttpStatus.OK);
    }


    /**
     * get number of user
     * @return response
     */
    @GetMapping(value = "/eipnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getEipCount(@RequestParam(required = false )String DimensionName,
                                      @RequestParam(required = false )String status) {
        try{
            log.info("———— get eipnumbers api called————");
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String,Object> map = new HashMap();
            map.put("DimensionName",DimensionName);
            map.put("status",status);
            String  uri =eipAtomUrl + "/eip/v1/eipnumbers?DimensionName={DimensionName}&status={status}";
            ResponseEntity responseEntity = restTemplate.getForEntity(uri, JSONObject.class,map );
            return responseEntity;
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
       
    }

    @PostMapping(value = "/deleiplist", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "deleiplist")
    public ResponseEntity deleteEipList(@RequestBody EipDelParam param) {

        try{
            log.info(" ————delete EipList api called————");
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String params = JSONObject.toJSONString(param);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(params, headers);
            String  uri =eipAtomUrl + "/eip/v1/deleiplist";
            return restTemplate.exchange(uri, HttpMethod.POST,entity, Object.class);

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping(value = "/servers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "show all servers", notes = "get")
    public ResponseEntity getServerList(@RequestParam String region, @RequestParam String tag) {
        log.info("————get serverList api called————");

        String  uri =eipAtomUrl + "/eip/v1/servers?region={region}&tag={tag}";

        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String,Object> map = new HashMap();
            map.put("region",region);
            map.put("tag",tag);
            ResponseEntity responseEntity = restTemplate.getForEntity(uri, JSONObject.class,map );
            return responseEntity;
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    @GetMapping(value = "/eips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="listeip",notes="list")
    public ResponseEntity listEip(@RequestParam(required = false) String currentPage ,
                                  @RequestParam(required = false )String limit,
                                  @RequestParam(required = false )String status,
                                  @RequestParam(required = false )String bandWidth) {

        log.info("————get listEip service api ————");

        String  uri =eipAtomUrl + "/eip/v1/eips?currentPage={currentPage}&limit={limit}&status={status}&bandWidth={bandWidth}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String,Object> map = new HashMap();
            map.put("currentPage",currentPage);
            map.put("limit",limit);
            map.put("status",status);
            map.put("bandWidth",bandWidth);
            return restTemplate.getForEntity(uri, JSONObject.class,map );

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/eips/{eip_id}")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "get detail of  eip instance", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "eip_id", value = "the id of eip", required = true, dataType = "String"),
    })
    public ResponseEntity getEipDetail(@PathVariable("eip_id") String eipId){

        log.info("————get EipDetail service api ————");

        String  uri =eipAtomUrl + "/eip/v1/eips/{eipId}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class,eipId );

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/eips/search")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="getEipByInstanceId",notes="get")
    public ResponseEntity getEipByInstanceId(@RequestParam(required = false) String resourceid,
                                             @RequestParam(required = false) String eipaddress) {
        log.info("————getEipByInstanceId service api ————");
        String  uri =eipAtomUrl + "/eip/v1/eips/search?resourceid={resourceid}&eipaddress={eipaddress}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String,Object> map = new HashMap();
            map.put("resourceid",resourceid);
            map.put("eipaddress",eipaddress);
            return restTemplate.getForEntity(uri, JSONObject.class,map );

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    /**
     * get number of user
     * @return response
     */
    @GetMapping(value = "/freeeipnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getFreeEipCount() {
        log.info("————freeeipnumbers service api ————");
        String  uri =eipAtomUrl + "/eip/v1/freeeipnumbers";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class );

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/usingeipnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getUsingEipCount(@RequestParam(required = false )String status) {
        log.info("————usingeipnumbers service api———— ");
        String  uri =eipAtomUrl + "/eip/v1/usingeipnumbers?status={status}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String,Object> map = new HashMap();
            map.put("status",status);
            return restTemplate.getForEntity(uri, JSONObject.class ,map);

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping(value = "/totaleipnumbers")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value="get number",notes="get number")
    public ResponseEntity getTotalEipCount() {

        log.info("————totaleipnumbers service api———— ");
        String  uri =eipAtomUrl + "/eip/v1/totaleipnumbers";

        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class );

        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping(value = "/eips/{eip_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "update eip", notes = "put")

    public ResponseEntity updateEip(@PathVariable("eip_id") String eipId,  @Valid @RequestBody (required = false)  EipUpdateParamWrapper param ) {


        log.info("————update service api ————");
        String  uri =eipAtomUrl + "/eip/v1/eips/{eipId}";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String params = JSONObject.toJSONString(param);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(params, headers);
            return restTemplate.exchange(uri, HttpMethod.PUT, entity, Object.class, eipId);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
