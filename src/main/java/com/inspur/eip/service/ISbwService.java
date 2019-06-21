package com.inspur.eip.service;


import com.inspur.eip.entity.sbw.SbwUpdateParam;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.http.ResponseEntity;


public interface ISbwService {

    ResponseEntity atomCreateSbw(SbwUpdateParam sbwConfig, String token);

    ActionResponse deleteSbwInfo(String sbwId , String token);

    ResponseEntity listShareBandWidth(Integer pageIndex, Integer pageSize, String searchValue);

    ResponseEntity getSbwDetail(String sbwId);

    ActionResponse updateSbwConfig(String id, SbwUpdateParam param, String token);

    ResponseEntity getSbwCount();
}
