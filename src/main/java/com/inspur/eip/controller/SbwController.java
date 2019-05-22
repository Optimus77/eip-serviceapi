package com.inspur.eip.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.sbw.SbwUpdateParamWrapper;
import com.inspur.eip.util.*;
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
import java.util.HashMap;


@Slf4j
@RestController
@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
@Api(value = "/v1", description = "sbw API")
@Validated
public class SbwController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${proxy.target_url}")
    private String sbwAtomUrl;


    @GetMapping(value = "/sbws")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "listsbw", notes = "list")
    public ResponseEntity listSbw(@RequestParam(required = false) String pageIndex,
                                  @RequestParam(required = false) String pageSize,
                                  @RequestParam(required = false) String searchValue) {
        log.debug("————SbwService api listSbw, pageIndex:{}, pageSize:{}", pageIndex, pageSize);

        String uri = sbwAtomUrl + "/eip/v1/sbws?pageIndex={pageIndex}&pageSize={pageSize}&searchValue={searchValue}";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String, Object> map = new HashMap();
            map.put("pageIndex", pageIndex);
            map.put("pageSize", pageSize);
            map.put("searchValue", searchValue);
            return restTemplate.getForEntity(uri, JSONObject.class, map);

        } catch (CustomException e) {
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/sbws/search")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "getSbwByProjectId", notes = "get")
    public ResponseEntity getSbwByProjectId(@RequestParam(required = false) String projectId) {
        String uri = sbwAtomUrl + "/eip/v1/sbws/search?projectId={projectId}";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String, Object> map = new HashMap();
            map.put("projectId", projectId);
            return restTemplate.getForEntity(uri, JSONObject.class, map);

        } catch (CustomException e) {
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    /**
     * get sbw instance detail
     *
     * @param sbwId the id of sbw
     * @return retrun
     */
    @GetMapping(value = "/sbws/{sbw_id}")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "get detail of  sbw instance", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity getSbwDetail(@PathVariable("sbw_id") String sbwId) {
        log.info("————get sbw detail SbwService api called————");

        String uri = sbwAtomUrl + "/eip/v1/sbws/{sbwId}";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class, sbwId);

        } catch (CustomException e) {
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * get sbw number of user
     *
     * @return response
     */
    @GetMapping(value = "/sbwnumbers")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "get number", notes = "get number")
    public ResponseEntity getSbwCount() {

        log.info("————sbwnumbers SbwService api———— ");
        String uri = sbwAtomUrl + "/eip/v1/sbwnumbers";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class);

        } catch (CustomException e) {
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping(value = "/sbws/{sbw_id}/othereips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "get othereips without the sbw", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity getOtherEips(@PathVariable("sbw_id") String sbwId){
        log.debug("————SbwService listSbw, sbwId:{}", sbwId);

        String uri = sbwAtomUrl + "/eip/v1/sbws/{sbw_id}/othereips";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            return restTemplate.getForEntity(uri, JSONObject.class, sbwId);

        } catch (CustomException e) {
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/sbws/{sbw_id}/eips")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "sbwListEip", notes = "listEip")
    public ResponseEntity sbwListEip(@PathVariable( name = "sbw_id") String sbwId,
                                     @RequestParam(required = false, name = "currentPageIndex", defaultValue = "1") String pageIndex,
                                     @RequestParam(required = false, name = "currentPageSize", defaultValue = "10") String pageSize) {
        log.debug("————SbwService listSbw, pageIndex:{}, pageSize:{}", pageIndex, pageSize);

        String uri = sbwAtomUrl + "/eip/v1/sbws/"+sbwId+"/eips?pageIndex={pageIndex}&pageSize={pageSize}";
        try {
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            HashMap<String, Object> map = new HashMap();
            map.put("pageIndex", pageIndex);
            map.put("pageSize", pageSize);
            return restTemplate.getForEntity(uri, JSONObject.class, map, sbwId);

        } catch (CustomException e) {
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PutMapping(value = "/sbws/{sbw_id}/rename", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "rename sbw name", notes = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity renameSbw(@PathVariable("sbw_id") String sbwId, @Valid @RequestBody SbwUpdateParamWrapper param){
        log.info("————Atom rename sbw param:{}",param.getSbw().toString());
        String  uri =sbwAtomUrl + "/eip/v1//sbws/{sbw_id}/rename";
        try{
            restTemplate.setErrorHandler(new ThrowErrorHandler());
            String params = JSONObject.toJSONString(param);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(params, headers);
            return restTemplate.exchange(uri, HttpMethod.PUT, entity, Object.class, sbwId);
        }catch (CustomException e){
            JSONObject resultJson = JSON.parseObject(e.getBody());
            log.error(resultJson.getString("message"));
            return new ResponseEntity<>(ReturnMsgUtil.error(resultJson.getString("code"), resultJson.getString("message")),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
