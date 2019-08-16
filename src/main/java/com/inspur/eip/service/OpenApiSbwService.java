package com.inspur.eip.service;


import com.inspur.eip.entity.openapi.OpenCreateEip;
import org.springframework.http.ResponseEntity;

public interface OpenApiSbwService {
    /**
     * Openapi create sbw
     * @return the json result
     */
    ResponseEntity OpenapiCreateSbw(OpenCreateEip openCreateEip, String token);




}
