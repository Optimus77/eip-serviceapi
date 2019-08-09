package com.inspur.eip.service;


import com.inspur.eip.entity.eip.EipAllocateParam;
import org.springframework.http.ResponseEntity;

public interface OpenApiService {



    /**
     * Openapi create eip
     * @return the json result
     */
    ResponseEntity OpenapiCreateEip(EipAllocateParam eipAllocateParam, String token);


}
