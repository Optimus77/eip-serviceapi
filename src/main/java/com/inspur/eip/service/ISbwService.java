package com.inspur.eip.service;


import com.inspur.eip.entity.sbw.SbwUpdateParam;
import org.springframework.http.ResponseEntity;


public interface ISbwService {

    ResponseEntity atomCreateSbw(SbwUpdateParam sbwConfig, String token);

    ResponseEntity deleteSbwInfo(String sbwId ,String token);

    ResponseEntity listShareBandWidth(Integer pageIndex, Integer pageSize, String searchValue);

    ResponseEntity getSbwDetail(String sbwId);

    ResponseEntity updateSbwConfig(String id, SbwUpdateParam param, String token);

    ResponseEntity getSbwCount();

    ResponseEntity getSbwByProjectId(String projectId);
}
