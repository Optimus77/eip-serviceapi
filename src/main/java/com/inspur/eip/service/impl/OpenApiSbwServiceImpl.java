package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.openapi.*;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.service.OpenApiSbwService;
import com.inspur.eip.service.OpenApiService;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.http.HttpClientUtil;
import com.inspur.eip.util.http.HttpsClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class OpenApiSbwServiceImpl implements OpenApiSbwService {

    @Value("${regionCode}")
    protected String regionCode;

    @Value("${bssUrl.quota}")
    protected String bssQuotaUrl;

    @Value("${bssUrl.submit}")
    protected String bssSubmitUrl;

    @Value("${bssUrl.product}")
    protected String bssProductUrl;


    @Autowired
    private SbwRepository sbwRepository;

    @Override
    public ResponseEntity OpenapiCreateSbw(OpenCreateEip openCreateEip, String token) {

        //检验请求参数
        if (EipConstant.BILLTYPE_MONTHLY.equals(openCreateEip.getBillType())) {
            if (StringUtils.isBlank(openCreateEip.getDuration())) {
                throw new EipInternalServerException(ErrorStatus.SBW_INVALID_BILL_TYPE.getCode(), ErrorStatus.SBW_INVALID_BILL_TYPE.getMessage());
            }
        }
        if(Integer.parseInt(openCreateEip.getBandwidth())<5||Integer.parseInt(openCreateEip.getBandwidth())>500){
            throw new EipInternalServerException(ErrorStatus.SBW_BANDWIDTH_ERROR.getCode(), ErrorStatus.SBW_BANDWIDTH_ERROR.getMessage());
        }

        //查询用户配额
        Map<String, String> paramMap = new HashMap<>();
        try {
            paramMap.put("userId", CommonUtil.getUserId(token));
            paramMap.put("region", regionCode);
            paramMap.put("productLineCode", EipConstant.PRODUCTLINE_CODE);
            paramMap.put("productTypeCode", EipConstant.PRODUCTTYPE_CODE);
            paramMap.put("billType", openCreateEip.getBillType());
            ResponseEntity responseEntity = HttpClientUtil.doGet(bssQuotaUrl, paramMap, HttpsClientUtil.getHeader());
            JSONObject responseBodyJson = JSONObject.parseObject(responseEntity.getBody().toString());
            if ("0".equals(responseBodyJson.getString("code"))) {
                if ((Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("data").getJSONObject(0).getJSONArray("typeList").getJSONObject(0).getString("totalAmount"))-Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("data").getJSONObject(0).getJSONArray("typeList").getJSONObject(0).getString("usedAmount"))) <= 0) {
                    throw new EipInternalServerException(ErrorStatus.SBW_EXCEED_QUOTA.getCode(), ErrorStatus.SBW_EXCEED_QUOTA.getMessage());
                }
            } else {
                throw new EipInternalServerException(ErrorStatus.BSS_CRM_QUOTA_ERROR.getCode(), ErrorStatus.BSS_CRM_QUOTA_ERROR.getMessage());
            }
        } catch (KeycloakTokenException e) {
            e.printStackTrace();
        }

        List<Item> items = new ArrayList<>();
        JSONArray itemArraryList = getUserProductItems(token);
        if (null != itemArraryList && !itemArraryList.isEmpty()) {
            for (int i = 0; i < itemArraryList.size(); i++) {
                buildItemList(items, itemArraryList, i, openCreateEip.getBandwidth(), openCreateEip.getSbwName(), openCreateEip.getSbwId());
            }
        }
//      创建SBW订单报文
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCTLINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCTTYPE_CODE)
                .instanceCount("1")
                .itemList(items)
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);

        JSONObject consoleJson = new JSONObject();
        consoleJson.put("region", regionCode);
        consoleJson.put("billType", openCreateEip.getBillType());
        consoleJson.put("chargemode", "SharedBandwidth");
        consoleJson.put("bandwidth", openCreateEip.getBandwidth());
        consoleJson.put("duration", openCreateEip.getDuration());
        consoleJson.put("durationUnit", "hourlySettlement".equalsIgnoreCase(openCreateEip.getBillType()) ? "H" : "M");
        consoleJson.put("sharedbandwidthname", openCreateEip.getSbwName());


        try {
            Order order = Order.builder()
                    .userId(CommonUtil.getUserId(token))
                    .token(token)
                    .orderRoute(EipConstant.ORDER_SBW_ROUTE)
                    .setCount("1")
                    .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .billType(openCreateEip.getBillType())
                    .duration(openCreateEip.getDuration())
                    .durationUnit("hourlySettlement".equalsIgnoreCase(openCreateEip.getBillType()) ? "H" : "M")
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_NEW)
                    .isAutoDeducted(EipConstant.STATUC_FALSE)
                    .isAutoRenew(EipConstant.STATUC_FALSE)
                    .consoleCustomization(consoleJson)
                    .productList(products)
                    .build();
            return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
        } catch (KeycloakTokenException e) {
            log.info("Openapi Create EIP Erroe");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ResponseEntity OpenapiDeleteSbw(OpenCreateEip openCreateEip, String token) {

        //检验请求参数
        if (StringUtils.isBlank(openCreateEip.getSbwId())) {
            throw new EipInternalServerException(ErrorStatus.SBW_ID_EMPTY.getCode(), ErrorStatus.SBW_ID_EMPTY.getMessage());
        }

        List<Item> items = new ArrayList<>();
        JSONArray itemArraryList = getUserProductItems(token);
        if (null != itemArraryList && !itemArraryList.isEmpty()) {
            String bandwidth = getSbwBandwidth(openCreateEip);
            for (int i = 0; i < itemArraryList.size(); i++) {
                buildItemList(items, itemArraryList, i, bandwidth, openCreateEip.getSbwName(), openCreateEip.getSbwId());
            }
        }
//      创建EIP订单报文
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCTLINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCTTYPE_CODE)
                .instanceCount("1")
                .instanceId(openCreateEip.getSbwId())
                .itemList(items)
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);
        try {
            Order order = Order.builder()
                    .userId(CommonUtil.getUserId(token))
                    .token(token)
                    .orderRoute(EipConstant.ORDER_SBW_ROUTE)
                    .setCount("1")
                    .duration("1")
                    .durationUnit("H")
                    .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .billType(EipConstant.BILLTYPE_HOURLYSETTLEMENT)
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_UNSUNSCRIBE)
                    .productList(products)
                    .build();
            return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
        } catch (KeycloakTokenException e) {
            log.info("Openapi Delete SBW Erroe");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ResponseEntity OpenapiEipAddSbw(OpenCreateEip openCreateEip, String token) {

        //检验请求参数
        if (StringUtils.isBlank(openCreateEip.getSbwId())) {
            throw new EipInternalServerException(ErrorStatus.SBW_ID_EMPTY.getCode(), ErrorStatus.SBW_ID_EMPTY.getMessage());
        }
        if (StringUtils.isBlank(openCreateEip.getEipId())) {
            throw new EipInternalServerException(ErrorStatus.EIP_ID_EMPTY.getCode(), ErrorStatus.EIP_ID_EMPTY.getMessage());
        }
        //获取sbw的带宽
        String bandwidth = getSbwBandwidth(openCreateEip);
        if (StringUtils.isBlank(bandwidth)) {
            throw new EipInternalServerException(ErrorStatus.SBW_NOT_FOUND.getCode(), ErrorStatus.SBW_NOT_FOUND.getMessage());
        }

        //获取sbw的名称
        String sbwName = getSbwName(openCreateEip);
        if (StringUtils.isBlank(sbwName)) {
            throw new EipInternalServerException(ErrorStatus.SBW_NOT_FOUND.getCode(), ErrorStatus.SBW_NOT_FOUND.getMessage());
        }

        //获取sbw的计费类型
        String billType = getBillType(openCreateEip);
        if (StringUtils.isBlank(billType)) {
            throw new EipInternalServerException(ErrorStatus.SBW_NOT_FOUND.getCode(), ErrorStatus.SBW_NOT_FOUND.getMessage());
        }


        List<Item> items = new ArrayList<>();
        JSONArray itemArraryList = getUserProductItemsEipAddSbw(token);
        if (null != itemArraryList && !itemArraryList.isEmpty()) {
            for (int i = 0; i < itemArraryList.size(); i++) {
                buildEipAddSbwItemList(items, itemArraryList, i, bandwidth, sbwName, openCreateEip.getSbwId());
            }
        }
//      创建EipAddSbw订单报文
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                .instanceCount("1")
                .instanceId(openCreateEip.getEipId())
                .itemList(items)
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
                    .billType(billType)
                    .duration("1")
                    .durationUnit("H")
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_CHANGE_CONFIG)
                    .consoleCustomization(null)
                    .productList(products)
                    .build();
            return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
        } catch (KeycloakTokenException e) {
            log.info("Openapi OpenapiCreateEipAddSbw Erroe");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ResponseEntity OpenapiEipRemoveSbw(OpenCreateEip openCreateEip, String token) {

        //检验请求参数
        if (StringUtils.isBlank(openCreateEip.getSbwId())) {
            throw new EipInternalServerException(ErrorStatus.SBW_ID_EMPTY.getCode(), ErrorStatus.SBW_ID_EMPTY.getMessage());
        }
        if (StringUtils.isBlank(openCreateEip.getEipId())) {
            throw new EipInternalServerException(ErrorStatus.EIP_ID_EMPTY.getCode(), ErrorStatus.EIP_ID_EMPTY.getMessage());
        }
        //获取sbw的带宽
        String bandwidth = getSbwBandwidth(openCreateEip);
        if (StringUtils.isBlank(bandwidth)) {
            throw new EipInternalServerException(ErrorStatus.SBW_NOT_FOUND.getCode(), ErrorStatus.SBW_NOT_FOUND.getMessage());
        }

        //获取sbw的名称
        String sbwName = getSbwName(openCreateEip);
        if (StringUtils.isBlank(sbwName)) {
            throw new EipInternalServerException(ErrorStatus.SBW_NOT_FOUND.getCode(), ErrorStatus.SBW_NOT_FOUND.getMessage());
        }

        //获取sbw的计费类型
        String billType = getBillType(openCreateEip);
        if (StringUtils.isBlank(billType)) {
            throw new EipInternalServerException(ErrorStatus.SBW_NOT_FOUND.getCode(), ErrorStatus.SBW_NOT_FOUND.getMessage());
        }

        List<Item> items = new ArrayList<>();
        JSONArray itemArraryList = getUserProductItemsEipAddSbw(token);
        if (null != itemArraryList && !itemArraryList.isEmpty()) {
            for (int i = 0; i < itemArraryList.size(); i++) {
                buildEipRemoveSbwItemList(items, itemArraryList, i, bandwidth, sbwName, openCreateEip.getSbwId());
            }
        }
//      创建EipRemoveSbw订单报文
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                .instanceCount("1")
                .instanceId(openCreateEip.getEipId())
                .itemList(items)
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
                    .billType(billType)
                    .duration("1")
                    .durationUnit("H")
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_CHANGE_CONFIG)
                    .consoleCustomization(null)
                    .productList(products)
                    .build();
            return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
        } catch (KeycloakTokenException e) {
            log.info("Openapi OpenapiCreateEipAddSbw Erroe");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ResponseEntity OpenapiRenewSbw(OpenCreateEip openCreateEip, String token) {

        if (StringUtils.isBlank(openCreateEip.getSbwId())) {
            throw new EipInternalServerException(ErrorStatus.SBW_ID_EMPTY.getCode(), ErrorStatus.SBW_ID_EMPTY.getMessage());
        }
        if (StringUtils.isBlank(openCreateEip.getDuration())) {
            throw new EipInternalServerException(ErrorStatus.SBW_BANDWIDTH_ERROR.getCode(), ErrorStatus.SBW_BANDWIDTH_ERROR.getMessage());
        }
        if (EipConstant.BILLTYPE_HOURLYSETTLEMENT.equals(openCreateEip.getBillType())) {
            throw new EipInternalServerException(ErrorStatus.SBW_INVALID_BILL_TYPE.getCode(), ErrorStatus.SBW_INVALID_BILL_TYPE.getMessage());

        }

        List<Item> items = new ArrayList<>();
        JSONArray itemArraryList = getUserProductItems(token);
        if (null != itemArraryList && !itemArraryList.isEmpty()) {
            String bandwidth = getSbwBandwidth(openCreateEip);
            for (int i = 0; i < itemArraryList.size(); i++) {
                buildItemListBandwidth(items, itemArraryList, i, bandwidth);
            }
        }
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCTLINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCTTYPE_CODE)
                .instanceCount("1")
                .instanceId(openCreateEip.getSbwId())
                .itemList(items)
                .build();
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Order order = null;
        try {
            order = Order.builder()
                    .userId(CommonUtil.getUserId(token))
                    .token(token)
                    .orderRoute(EipConstant.ORDER_SBW_ROUTE)
                    .setCount("1")
                    .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .billType(EipConstant.BILLTYPE_MONTHLY)
                    .duration(openCreateEip.getDuration())
                    .durationUnit(EipConstant.ORDER_DURATION_UNIT_MONTHLY)
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_RENEW)
                    .isAutoDeducted(EipConstant.STATUC_FALSE)
                    .isAutoRenew(EipConstant.STATUC_TRUE)
                    .productList(productList)
                    .build();
            return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
        } catch (KeycloakTokenException e) {
            log.info("Openapi Renew SBW Erroe");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ResponseEntity OpenapiSbwUpdateBandwidth(OpenCreateEip openCreateEip, String token) {

        if (StringUtils.isBlank(openCreateEip.getEipId())) {
            throw new EipInternalServerException(ErrorStatus.SBW_ID_EMPTY.getCode(),ErrorStatus.SBW_ID_EMPTY.getMessage());
        }
        if (StringUtils.isBlank(openCreateEip.getBandwidth())) {
            throw new EipInternalServerException(ErrorStatus.SBW_BANDWIDTH_EMPTY.getCode(),ErrorStatus.SBW_BANDWIDTH_EMPTY.getMessage());
        }
//      比较更新前后带宽大小；按需可以调大调小,包年包月只能调大
        Optional<Sbw> optionalSbw = sbwRepository.findById(openCreateEip.getSbwId());
        if (optionalSbw.isPresent()){
            Sbw sbwEntity = optionalSbw.get();
            if(EipConstant.BILLTYPE_MONTHLY.equals(sbwEntity.getBillType())){
                String str = openCreateEip.getBandwidth();
                if (str!=null){
                    Integer newBandwidth =Integer.valueOf(str);
                    if(newBandwidth <= sbwEntity.getBandWidth()){
                        throw new EipInternalServerException(ErrorStatus.SBW_BANDWIDTH_ERROR.getCode(),ErrorStatus.SBW_BANDWIDTH_ERROR.getMessage());
                    }
                }

            }

            List<Item> newItems = new ArrayList<>();
            JSONArray itemArraryList = getUserProductItems(token);
            if (null != itemArraryList && !itemArraryList.isEmpty()) {
                for (int i = 0; i < itemArraryList.size(); i++) {
                    buildItemListBandwidth(newItems, itemArraryList,i,openCreateEip.getBandwidth());
                }
            }
            Product product = Product.builder()
                    .region(regionCode)
                    .productLineCode(EipConstant.PRODUCTLINE_CODE)
                    .availableZone("")
                    .productTypeCode(EipConstant.PRODUCTTYPE_CODE)
                    .instanceCount("1")
                    .instanceId(openCreateEip.getSbwId())
                    .itemList(newItems)
                    .build();
            List<Product> productList = new ArrayList<>();
            productList.add(product);
            Order order = null;
            try {
                order = Order.builder()
                        .userId(CommonUtil.getUserId(token))
                        .token(token)
                        .orderRoute(EipConstant.ORDER_SBW_ROUTE)
                        .setCount("1")
                        .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                        .billType(sbwEntity.getBillType())
                        .duration(null)
                        .durationUnit("hourlySettlement".equalsIgnoreCase(sbwEntity.getBillType()) ? "H" : "M")
                        .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                        .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                        .orderType(EipConstant.ORDER_TYPE_CHANGE_CONFIG)
                        .productList(productList)
                        .build();
                return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
            } catch (KeycloakTokenException e) {
                log.info("Openapi update EIP Bindwidth");
                e.printStackTrace();
            }
        }
        return null;
    }


    private JSONArray getUserProductItems(String token) {
        JSONArray resultArray = new JSONArray();
        Map<String, String> paramMap = new HashMap<>();
        String resultString = null;
        try {
            paramMap.put("userId", CommonUtil.getUserId(token));
        } catch (KeycloakTokenException e) {
            e.printStackTrace();
        }
        paramMap.put("productLineCode", EipConstant.PRODUCTLINE_CODE);
        paramMap.put("productTypeCode", EipConstant.PRODUCTTYPE_CODE);
        paramMap.put("region", regionCode);
        ResponseEntity responseEntity = HttpClientUtil.doGet(bssProductUrl, paramMap, HttpsClientUtil.getHeader());
        System.out.println(responseEntity.toString());
        if (200 == responseEntity.getStatusCodeValue()) {
            resultString = responseEntity.getBody().toString();
        } else {
            return null;
        }
        JSONObject result = null;
        if (null != resultString) {
            String resultCode = JSONObject.parseObject(resultString).getString("code");
            if ("0".equals(resultCode)) {
                result = JSONObject.parseObject(resultString).getJSONObject("result");
            }
        }
        if (null != result) {
            JSONArray productLineList = result.getJSONArray("productLineList");
            if (productLineList == null || productLineList.isEmpty()) {
                return resultArray;
            }
            JSONObject productLine = null;
            for (int i = 0; i < productLineList.size(); i++) {
                JSONObject obj = productLineList.getJSONObject(i);
                if (EipConstant.PRODUCTLINE_CODE.equals(obj.getString("code"))) {
                    productLine = obj;
                    break;
                }
            }
            if (null != productLine) {
                JSONArray productTypeList = productLine.getJSONArray("productTypeList");
                if (productTypeList == null || productTypeList.isEmpty()) {
                    return resultArray;
                }
                JSONObject productType = null;
                for (int j = 0; j < productTypeList.size(); j++) {
                    JSONObject object = productTypeList.getJSONObject(j);
                    if (EipConstant.PRODUCTTYPE_CODE.equals(object.getString("code"))) {
                        productType = object;
                        break;
                    }
                }
                if (null != productType) {
                    resultArray = productType.getJSONArray("itemList");
                } else {
                    return resultArray;
                }
            } else {
                return resultArray;
            }
        } else {
            return resultArray;
        }
        return resultArray;
    }

    private JSONArray getUserProductItemsEipAddSbw(String token) {
        JSONArray resultArray = new JSONArray();
        Map<String, String> paramMap = new HashMap<>();
        String resultString = null;
        try {
            paramMap.put("userId", CommonUtil.getUserId(token));
        } catch (KeycloakTokenException e) {
            e.printStackTrace();
        }
        paramMap.put("productLineCode", EipConstant.PRODUCT_LINE_CODE);
        paramMap.put("productTypeCode", EipConstant.PRODUCT_TYPE_CODE);
        paramMap.put("region", regionCode);
        ResponseEntity responseEntity = HttpClientUtil.doGet(bssProductUrl, paramMap, HttpsClientUtil.getHeader());
        System.out.println(responseEntity.toString());
        if (200 == responseEntity.getStatusCodeValue()) {
            resultString = responseEntity.getBody().toString();
        } else {
            return null;
        }
        JSONObject result = null;
        if (null != resultString) {
            String resultCode = JSONObject.parseObject(resultString).getString("code");
            if ("0".equals(resultCode)) {
                result = JSONObject.parseObject(resultString).getJSONObject("result");
            }
        }
        if (null != result) {
            JSONArray productLineList = result.getJSONArray("productLineList");
            if (productLineList == null || productLineList.isEmpty()) {
                return resultArray;
            }
            JSONObject productLine = null;
            for (int i = 0; i < productLineList.size(); i++) {
                JSONObject obj = productLineList.getJSONObject(i);
                if (EipConstant.PRODUCT_LINE_CODE.equals(obj.getString("code"))) {
                    productLine = obj;
                    break;
                }
            }
            if (null != productLine) {
                JSONArray productTypeList = productLine.getJSONArray("productTypeList");
                if (productTypeList == null || productTypeList.isEmpty()) {
                    return resultArray;
                }
                JSONObject productType = null;
                for (int j = 0; j < productTypeList.size(); j++) {
                    JSONObject object = productTypeList.getJSONObject(j);
                    if (EipConstant.PRODUCT_TYPE_CODE.equals(object.getString("code"))) {
                        productType = object;
                        break;
                    }
                }
                if (null != productType) {
                    resultArray = productType.getJSONArray("itemList");
                } else {
                    return resultArray;
                }
            } else {
                return resultArray;
            }
        } else {
            return resultArray;
        }
        return resultArray;
    }


    private void buildItemList(List<Item> items, JSONArray itemArrayList, int i, String bandwidth, String sbwName, String sbwId) {
        JSONObject object = itemArrayList.getJSONObject(i);
        Item item = Item.builder().code(object.getString("code"))
                .build();
        if ("bandwidth".equals(object.getString("code"))) {
            if (StringUtils.isBlank(bandwidth)) {
                item.setValue("5");
            } else {
                item.setValue(bandwidth);
            }
            items.add(item);
        }
        if ("sbwName".equals(object.getString("code"))) {
            if (StringUtils.isBlank(sbwName)) {
                item.setValue("-");
            } else {
                item.setValue(sbwName);
            }
            items.add(item);
        }
    }

    private void buildItemListBandwidth(List<Item> items, JSONArray itemArrayList, int i, String bandwidth) {
        JSONObject object = itemArrayList.getJSONObject(i);
        Item item = Item.builder().code(object.getString("code"))
                .build();
        if ("bandwidth".equals(object.getString("code"))) {
            if (StringUtils.isBlank(bandwidth)) {
                item.setValue("5");
            } else {
                item.setValue(bandwidth);
            }
            items.add(item);
        }

    }


    private void buildEipAddSbwItemList(List<Item> items, JSONArray itemArrayList, int i, String bandwidth, String sbwName, String sbwId) {
        JSONObject object = itemArrayList.getJSONObject(i);
        Item item = Item.builder().code(object.getString("code"))
                .build();
        if ("bandwidth".equals(object.getString("code"))) {
            if (StringUtils.isBlank(bandwidth)) {
                item.setValue("1");
            } else {
                item.setValue(bandwidth);
            }
            items.add(item);
        }
        if ("transfer".equals(object.getString("code"))) {
            item.setValue("0");
            items.add(item);
        }
        if ("IP".equals(object.getString("code"))) {
            item.setValue("1");
            items.add(item);
        }
        if ("provider".equals(object.getString("code"))) {
            item.setValue("BGP");
            items.add(item);
        }
        if ("is_SBW".equals(object.getString("code"))) {
            item.setValue("yes");
            items.add(item);
        }
        if ("sbwName".equals(object.getString("code"))) {
            item.setValue(sbwName);
            items.add(item);
        }
        if ("sbwId".equals(object.getString("code"))) {
            item.setValue(sbwId);
            items.add(item);
        }
    }

    private void buildEipRemoveSbwItemList(List<Item> items, JSONArray itemArrayList, int i, String bandwidth, String sbwName, String sbwId) {
        JSONObject object = itemArrayList.getJSONObject(i);
        Item item = Item.builder().code(object.getString("code"))
                .build();
        if ("bandwidth".equals(object.getString("code"))) {
            if (StringUtils.isBlank(bandwidth)) {
                item.setValue("1");
            } else {
                item.setValue(bandwidth);
            }
            items.add(item);
        }
        if ("transfer".equals(object.getString("code"))) {
            item.setValue("0");
            items.add(item);
        }
        if ("IP".equals(object.getString("code"))) {
            item.setValue("1");
            items.add(item);
        }
        if ("provider".equals(object.getString("code"))) {
            item.setValue("BGP");
            items.add(item);
        }
        if ("is_SBW".equals(object.getString("code"))) {
            item.setValue("no");
            items.add(item);
        }
        if ("sbwName".equals(object.getString("code"))) {
            item.setValue(sbwName);
            items.add(item);
        }
        if ("sbwId".equals(object.getString("code"))) {
            item.setValue(sbwId);
            items.add(item);
        }
    }


    private String getSbwBandwidth(OpenCreateEip openCreateEip) {
        Optional<Sbw> optional = sbwRepository.findById(openCreateEip.getSbwId());
        if (optional.isPresent()) {
            Sbw eipEntity = optional.get();
            Integer bandwidthI = eipEntity.getBandWidth();
            String bandwidth = bandwidthI.toString();
            return bandwidth;
        }
        return null;
    }

    private String getSbwName(OpenCreateEip openCreateEip) {
        Optional<Sbw> optional = sbwRepository.findById(openCreateEip.getSbwId());
        if (optional.isPresent()) {
            Sbw sbwEntity = optional.get();
            String sbwName = sbwEntity.getSbwName();
            return sbwName;
        }
        return null;
    }

    private String getBillType(OpenCreateEip openCreateEip) {
        Optional<Sbw> optional = sbwRepository.findById(openCreateEip.getSbwId());
        if (optional.isPresent()) {
            Sbw sbwEntity = optional.get();
            String billType = sbwEntity.getBillType();
            return billType;
        }
        return null;
    }

}
