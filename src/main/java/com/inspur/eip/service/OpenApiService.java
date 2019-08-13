package com.inspur.eip.service;


import com.inspur.eip.entity.openapi.OpenCreateEip;
import org.springframework.http.ResponseEntity;

public interface OpenApiService {



    /**
     * Openapi create eip
     * @return the json result
     */
    ResponseEntity OpenapiCreateEip(OpenCreateEip openCreateEip, String token);


    /**
     * Openapi create eipAddSbw
     * @return the json result
     */
    ResponseEntity OpenapiCreateEipAddSbw(OpenCreateEip openCreateEip, String token);


    /**
     * Openapi delete eip
     * @return the json result
     */
    ResponseEntity OpenapiDeleteEip(OpenCreateEip openCreateEip, String token);



}
