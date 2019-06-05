package com.inspur.eip.controller;

import com.inspur.eip.entity.sbw.SbwUpdateParamWrapper;
import com.inspur.eip.service.impl.SbwServiceImpl;
import com.inspur.eip.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(value= "/v1", produces={"application/json;charset=UTF-8"})
@Api(value = "/v1", description = "sbw API")
@Validated
public class SbwController {

    @Autowired
    private SbwServiceImpl sbwService;


    @GetMapping(value = "/sbws")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "listsbw", notes = "list")
    public ResponseEntity listSbw(@RequestParam(required = false) String pageIndex,
                                  @RequestParam(required = false) String pageSize,
                                  @RequestParam(required = false) String searchValue) {
        log.debug("————SbwService api listSbw, pageIndex:{}, pageSize:{}", pageIndex, pageSize);
        log.debug("Atom listSbw method param currentPageIndex:{}, currentPageSize:{}, searchValue:{}", pageIndex, pageSize, searchValue);
        if (pageIndex == null || pageSize == null) {
            pageIndex = "0";
            pageSize = "0";
        } else {
            try {
                int currentPageNum = Integer.parseInt(pageIndex);
                int limitNum = Integer.parseInt(pageSize);
                if (currentPageNum < 0 || limitNum < 0) {
                    pageIndex = "0";
                }
            } catch (Exception e) {
                log.error("number is not correct ");
                pageIndex = "0";
                pageSize = "0";
            }
        }
        return sbwService.listShareBandWidth(Integer.parseInt(pageIndex), Integer.parseInt(pageSize), searchValue);
    }


    @GetMapping(value = "/sbws/search")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "getSbwByProjectId", notes = "get")
    public ResponseEntity getSbwByProjectId(@RequestParam(required = false) String projectId) {
        log.info("Atom param get Sbw by project Id project:{}",projectId);
        if (null == projectId) {
            return new ResponseEntity<>("not found.", HttpStatus.NOT_FOUND);
        }

        return sbwService.getSbwByProjectId(projectId);

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
        log.info("Atom get the sbw detail , sbwId:{} ", sbwId);
        return sbwService.getSbwDetail(sbwId);

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

        log.info("Atom get Sbw Count loading……");
        return sbwService.getSbwCount();
    }



    @GetMapping(value = "/sbws/{sbw_id}/othereips")
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "get othereips without the sbw", notes = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity getOtherEips(@PathVariable("sbw_id") String sbwId){
        log.info("Atom get the other Eip have not bind in sbw,Sbwid:{}",sbwId);
        return sbwService.getOtherEips(sbwId);
    }

    @GetMapping(value = "/sbws/{sbw_id}/eips")
    @CrossOrigin(origins = "*", maxAge = 3000)
    @ApiOperation(value = "sbwListEip", notes = "listEip")
    public ResponseEntity sbwListEip(@PathVariable( name = "sbw_id") String sbwId,
                                     @RequestParam(required = false, name = "currentPageIndex", defaultValue = "1") String pageIndex,
                                     @RequestParam(required = false, name = "currentPageSize", defaultValue = "10") String pageSize) {
        log.info("Atom get EIP list in this Sbw sbwId:{},currentPageIndex:{}, currentPageSize:{}",sbwId, pageIndex, pageSize);
        if (pageIndex == null || pageSize == null) {
            pageIndex = "0";
            pageSize = "0";
        } else {
            try {
                int currentPageNum = Integer.parseInt(pageIndex);
                int limitNum = Integer.parseInt(pageSize);
                if (currentPageNum < 0 || limitNum < 0) {
                    pageIndex = "0";
                }
            } catch (Exception e) {
                log.error("number is not correct ");
                pageIndex = "0";
                pageSize = "0";
            }
        }
        return sbwService.sbwListEip(sbwId ,Integer.parseInt(pageIndex), Integer.parseInt(pageSize));
    }



    @PutMapping(value = "/sbws/{sbw_id}/rename", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*",maxAge = 3000)
    @ApiOperation(value = "rename sbw name", notes = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "sbw_id", value = "the id of sbw", required = true, dataType = "String"),
    })
    public ResponseEntity renameSbw(@PathVariable("sbw_id") String sbwId, @Valid @RequestBody SbwUpdateParamWrapper param, BindingResult result){
        log.info("Atom rename sbw param:{}",param.getSbw().toString());
        if (result.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                builder.append(fieldError.getField() + ":" + fieldError.getDefaultMessage());
            }
            log.info("{}",builder);
            return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, builder.toString()), HttpStatus.BAD_REQUEST);
        }
        String msg;
        if (StringUtils.isNotBlank(param.getSbw().getSbwName())){
            return sbwService.renameSbw(sbwId, param.getSbw());
        }else {
            msg="The new sbw name must not be blank";
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR, msg), HttpStatus.BAD_REQUEST);
    }

}
