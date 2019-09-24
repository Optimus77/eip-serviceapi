package com.inspur.eip.service;


import com.inspur.eip.entity.openapi.OpenCreateEip;
import org.springframework.http.ResponseEntity;

public interface OpenApiSbwService {
    /**
     * Openapi create sbw
     * @return the json result
     */
    ResponseEntity OpenapiCreateSbw(OpenCreateEip openCreateEip, String token);

    /**
     * Openapi delete sbw
     * @return the json result
     */
    ResponseEntity OpenapiDeleteSbw(OpenCreateEip openCreateEip, String token);

    /**
     * Openapi eip add to sbw
     * @return the json result
     */
    ResponseEntity OpenapiEipAddSbw(OpenCreateEip openCreateEip, String token);

    /**
     * Openapi eip remove from sbw
     * @return the json result
     */
    ResponseEntity OpenapiEipRemoveSbw(OpenCreateEip openCreateEip, String token);

    /**
     * Openapi renew sbw
     * @return the json result
     */
    ResponseEntity OpenapiRenewSbw(OpenCreateEip openCreateEip, String token);

    /**
     * Openapi update bandwidth of sbw
     * @return the json result
     */
    ResponseEntity OpenapiSbwUpdateBandwidth(OpenCreateEip openCreateEip, String token);



}
