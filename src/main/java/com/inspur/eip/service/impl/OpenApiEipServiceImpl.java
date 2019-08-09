package com.inspur.eip.service.impl;


import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.openapi.EipConstant;
import com.inspur.eip.entity.openapi.Order;
import com.inspur.eip.entity.openapi.Product;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.service.OpenApiService;
import com.inspur.eip.util.common.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.*;

public abstract class OpenApiEipServiceImpl implements OpenApiService {

    @Value("${region.code}")
    private String regionCode;

    @Value("${bss.quota}")
    private String bssQuotaUrl;

    @Value("${bss.submit}")
    private String bssSubmitUrl;



    @Override
    public ResponseEntity OpenapiCreateEip(EipAllocateParam eipAllocateParam, String token) {

        //查询用户配额
        Map<String,String> paramMap = new HashMap<>();
        try {
            paramMap.put("userId",CommonUtil.getUserId(token));
            paramMap.put("region",regionCode);
            paramMap.put("productLineCode",EipConstant.PRODUCT_LINE_CODE);
//            ResponseEntity responseEntity = HttpClientUtil.doGet()

        } catch (KeycloakTokenException e) {
            e.printStackTrace();
        }

//      创建EIP订单报文
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                .instanceCount("1")
//                .itemList(items)
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);
        try {
        Order order = Order.builder()
                .userId(CommonUtil.getUserId(token))
                .token(token)
                .orderRoute(EipConstant.ORDER_ROUTE)
                .setCount("1")
                .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                .billType(eipAllocateParam.getBillType())
                .duration(eipAllocateParam.getDuration())
                .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                .orderType(EipConstant.ORDER_TYPE_NEW)
                .productList(products)
                .build();
        } catch (KeycloakTokenException e) {
            e.printStackTrace();
        }

        return null;
    }












}
