package com.inspur.eip.service;


import com.inspur.eip.entity.sbw.SbwUpdateParam;
import org.springframework.http.ResponseEntity;


public interface ISbwService {

    ResponseEntity atomDeleteSbw(String sbwId);

    ResponseEntity listShareBandWidth(Integer pageIndex, Integer pageSize, String searchValue);

    ResponseEntity getSbwDetail(String sbwId);

    ResponseEntity updateSbwBandWidth(String id, SbwUpdateParam param);

    ResponseEntity getSbwCount();

    ResponseEntity getSbwByProjectId(String projectId);
}
