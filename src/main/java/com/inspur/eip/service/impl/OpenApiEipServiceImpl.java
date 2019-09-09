package com.inspur.eip.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.openapi.*;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.exception.KeycloakTokenException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
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
public class OpenApiEipServiceImpl implements OpenApiService {

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

    @Autowired
    private EipRepository eipRepository;

    @Override
    public ResponseEntity OpenapiCreateEip(OpenCreateEip openCreateEip, String token) {

        //检验请求参数
        if (EipConstant.BILLTYPE_MONTHLY.equals(openCreateEip.getBillType())) {
            if (StringUtils.isBlank(openCreateEip.getDuration())) {
                throw new EipInternalServerException(ErrorStatus.INVALID_BILL_TYPE.getCode(), ErrorStatus.INVALID_BILL_TYPE.getMessage());
            }
        }

        //查询用户配额
        Map<String, String> paramMap = new HashMap<>();
        try {
            paramMap.put("userId", CommonUtil.getUserId(token));
            paramMap.put("region", regionCode);
            paramMap.put("productLineCode", EipConstant.PRODUCT_LINE_CODE);
            paramMap.put("productTypeCode", EipConstant.PRODUCT_TYPE_CODE);
            paramMap.put("billType", openCreateEip.getBillType());
            ResponseEntity responseEntity = HttpClientUtil.doGet(bssQuotaUrl, paramMap, HttpsClientUtil.getHeader());
            JSONObject responseBodyJson = JSONObject.parseObject(responseEntity.getBody().toString());
            if ("0".equals(responseBodyJson.getString("code"))) {
                if ((Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("data").getJSONObject(0).getJSONArray("typeList").getJSONObject(0).getString("totalAmount")) - Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("data").getJSONObject(0).getJSONArray("typeList").getJSONObject(0).getString("usedAmount"))) <= 0) {
                    throw new EipInternalServerException(ErrorStatus.EIP_EXCEED_QUOTA.getCode(), ErrorStatus.EIP_EXCEED_QUOTA.getMessage());
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
//      创建EIP订单报文
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                .instanceCount("1")
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
                    .billType(openCreateEip.getBillType())
                    .duration(openCreateEip.getDuration())
                    .durationUnit("hourlySettlement".equalsIgnoreCase(openCreateEip.getBillType()) ? "H" : "M")
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_NEW)
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
    public ResponseEntity OpenapiCreateEipAddSbw(OpenCreateEip openCreateEip, String token) {

        //检验请求参数
        if (StringUtils.isBlank(openCreateEip.getSbwId())) {
            throw new EipInternalServerException(ErrorStatus.SBW_ID_EMPTY.getCode(), ErrorStatus.SBW_ID_EMPTY.getMessage());
        }
        //获取sbw的带宽
        String bandwidth = getSbwBandwidth(openCreateEip);
        if (StringUtils.isBlank(bandwidth)) {
            throw new EipInternalServerException(ErrorStatus.SBW_NOT_FOUND.getCode(), ErrorStatus.SBW_NOT_FOUND.getMessage());
        }

        //查询用户配额
        Map<String, String> paramMap = new HashMap<>();
        try {
            paramMap.put("userId", CommonUtil.getUserId(token));
            paramMap.put("region", regionCode);
            paramMap.put("productLineCode", EipConstant.PRODUCT_LINE_CODE);
            ResponseEntity responseEntity = HttpClientUtil.doGet(bssQuotaUrl, paramMap, HttpsClientUtil.getHeader());
            JSONObject responseBodyJson = JSONObject.parseObject(responseEntity.getBody().toString());
            if ("0".equals(responseBodyJson.getString("code"))) {
                if (Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("quotaList").getJSONObject(0).getString("leftNumber")) == 0) {
                    throw new EipInternalServerException(ErrorStatus.EIP_EXCEED_QUOTA.getCode(), ErrorStatus.EIP_EXCEED_QUOTA.getMessage());
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
                buildCreateEipAddSbwItemList(items, itemArraryList, i, bandwidth, openCreateEip.getSbwId());
            }
        }
//      创建Eip AddSbw订单报文
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                .instanceCount("1")
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
                    .billType(EipConstant.BILLTYPE_HOURLYSETTLEMENT)
                    .duration("1")
                    .durationUnit("H")
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_NEW)
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
    public ResponseEntity OpenapiDeleteEip(OpenCreateEip openCreateEip, String token) {

        //检验请求参数
        if (StringUtils.isBlank(openCreateEip.getEipId())) {
            throw new EipInternalServerException(ErrorStatus.EIP_ID_EMPTY.getCode(), ErrorStatus.EIP_ID_EMPTY.getMessage());
        }

        List<Item> items = new ArrayList<>();
        JSONArray itemArraryList = getUserProductItems(token);
        if (null != itemArraryList && !itemArraryList.isEmpty()) {
            String bandwidth = getEipBandwidth(openCreateEip);
            for (int i = 0; i < itemArraryList.size(); i++) {
                buildItemList(items, itemArraryList, i, bandwidth, openCreateEip.getSbwName(), openCreateEip.getSbwId());
            }
        }
//      创建EIP订单报文
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
            log.info("Openapi Delete EIP Erroe");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity OpenapiRenewEip(OpenCreateEip openCreateEip, String token) {

        if (StringUtils.isBlank(openCreateEip.getEipId())) {
            throw new EipInternalServerException(ErrorStatus.EIP_ID_EMPTY.getCode(), ErrorStatus.EIP_ID_EMPTY.getMessage());
        }
        if (StringUtils.isBlank(openCreateEip.getDuration())) {
            throw new EipInternalServerException(ErrorStatus.INVALID_BILL_TYPE.getCode(), ErrorStatus.INVALID_BILL_TYPE.getMessage());
        }

        List<Item> items = new ArrayList<>();
        JSONArray itemArraryList = getUserProductItems(token);
        if (null != itemArraryList && !itemArraryList.isEmpty()) {
            String bandwidth = getEipBandwidth(openCreateEip);
            for (int i = 0; i < itemArraryList.size(); i++) {
                buildItemList(items, itemArraryList, i, bandwidth, openCreateEip.getSbwName(), openCreateEip.getSbwId());
            }
        }
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                .instanceCount("1")
                .instanceId(openCreateEip.getEipId())
                .itemList(items)
                .build();
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Order order = null;
        try {
            order = Order.builder()
                    .userId(CommonUtil.getUserId(token))
                    .token(token)
                    .orderRoute(EipConstant.ORDER_ROUTE)
                    .setCount("1")
                    .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .billType(EipConstant.BILLTYPE_MONTHLY)
                    .duration(openCreateEip.getDuration())
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_RENEW)
                    .productList(productList)
                    .build();
            return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
        } catch (KeycloakTokenException e) {
            log.info("Openapi Renew EIP Erroe");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity OpenapiEipupdateBandwidth(OpenCreateEip openCreateEip, String token) {

        if (StringUtils.isBlank(openCreateEip.getEipId())) {
            throw new EipInternalServerException(ErrorStatus.EIP_ID_EMPTY.getCode(), ErrorStatus.EIP_ID_EMPTY.getMessage());
        }
        if (StringUtils.isBlank(openCreateEip.getBandwidth())) {
            throw new EipInternalServerException(ErrorStatus.EIP_BANDWIDTH_EMPTY.getCode(), ErrorStatus.EIP_BANDWIDTH_EMPTY.getMessage());
        }
//      比较更新前后带宽大小；按需可以调大调小,包年包月只能调大
        Optional<Eip> optionalEip = eipRepository.findById(openCreateEip.getEipId());
        if (optionalEip.isPresent()) {
            Eip eipEntity = optionalEip.get();
            if (EipConstant.BILLTYPE_MONTHLY.equals(eipEntity.getBillType())) {
                String str = openCreateEip.getBandwidth();
                if (str != null) {
                    Integer newBandwidth = Integer.valueOf(str);
                    if (newBandwidth <= eipEntity.getBandWidth()) {
                        throw new EipInternalServerException(ErrorStatus.EIP_BANDWIDTH_ERROR.getCode(), ErrorStatus.EIP_BANDWIDTH_ERROR.getMessage());
                    }
                }

            }

            List<Item> newItems = new ArrayList<>();
            JSONArray itemArraryList = getUserProductItems(token);
            if (null != itemArraryList && !itemArraryList.isEmpty()) {
                for (int i = 0; i < itemArraryList.size(); i++) {
                    buildItemList(newItems, itemArraryList, i, openCreateEip.getBandwidth(), openCreateEip.getSbwName(), openCreateEip.getSbwId());
                }
            }
            Product product = Product.builder()
                    .region(regionCode)
                    .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                    .availableZone("")
                    .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                    .instanceCount("1")
                    .instanceId(openCreateEip.getEipId())
                    .itemList(newItems)
                    .build();
            List<Product> productList = new ArrayList<>();
            productList.add(product);
            Order order = null;
            try {
                order = Order.builder()
                        .userId(CommonUtil.getUserId(token))
                        .token(token)
                        .orderRoute(EipConstant.ORDER_ROUTE)
                        .setCount("1")
                        .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                        .billType(eipEntity.getBillType())
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

    //        购买并绑定IPTS是按需，需要计费；使用已有IPTS按需和报年报月都支持，不需要计费
    @Override
    public ResponseEntity OpenapicreateIptsBandEip(OpenCreateEip openCreateEip, String token) {

        if (StringUtils.isBlank(openCreateEip.getBandwidth())) {
            throw new EipInternalServerException(ErrorStatus.EIP_BANDWIDTH_EMPTY.getCode(), ErrorStatus.EIP_BANDWIDTH_EMPTY.getMessage());
        }

        if (EipConstant.BILLTYPE_MONTHLY.equals(openCreateEip.getBillType())) {
            if (StringUtils.isBlank(openCreateEip.getDuration())) {
                throw new EipInternalServerException(ErrorStatus.INVALID_BILL_TYPE.getCode(), ErrorStatus.INVALID_BILL_TYPE.getMessage());
            }
        }

        // 查询用户配额
        Map<String, String> paramMap = new HashMap<>();
        try {
            paramMap.put("userId", CommonUtil.getUserId(token));
            paramMap.put("region", regionCode);
            paramMap.put("productLineCode", EipConstant.PRODUCT_LINE_CODE);
            paramMap.put("productTypeCode", EipConstant.PRODUCT_TYPE_CODE);
            paramMap.put("billType", openCreateEip.getBillType());
            ResponseEntity responseEntity = HttpClientUtil.doGet(bssQuotaUrl, paramMap, HttpsClientUtil.getHeader());
            JSONObject responseBodyJson = JSONObject.parseObject(responseEntity.getBody().toString());
            if ("0".equals(responseBodyJson.getString("code"))) {
                if ((Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("data").getJSONObject(0).getJSONArray("typeList").getJSONObject(0).getString("totalAmount")) - Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("data").getJSONObject(0).getJSONArray("typeList").getJSONObject(0).getString("usedAmount"))) <= 0) {
                    throw new EipInternalServerException(ErrorStatus.EIP_EXCEED_QUOTA.getCode(), ErrorStatus.EIP_EXCEED_QUOTA.getMessage());
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
                buildIptsBindEip(items, itemArraryList, i, openCreateEip.getBandwidth(), openCreateEip.getSbwName(), openCreateEip.getSbwId());
            }
        }
        Product product = Product.builder()
                .region(regionCode)
                .productLineCode(EipConstant.PRODUCT_LINE_CODE)
                .availableZone("")
                .productTypeCode(EipConstant.PRODUCT_TYPE_CODE)
                .instanceCount("1")
                .itemList(items)
                .build();
        List<Product> products = new ArrayList<>();
        products.add(product);
        try {
            Order order = Order.builder()
                    .userId(CommonUtil.getUserId(token))
                    .token(token)
                    .orderRoute(EipConstant.ORDER_ROUTE_IPTS)
                    .setCount("1")
                    .consoleOrderFlowId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .billType("hourlySettlement")
                    .duration("1")
                    .durationUnit("H")
                    .orderWhat(EipConstant.ORDER_WHAT_FORMAL)
                    .orderSource(EipConstant.ORDER_SOURCE_OPENAPI)
                    .orderType(EipConstant.ORDER_TYPE_NEW)
                    .isAutoRenew("false")
                    .productList(products)
                    .build();
            return HttpClientUtil.doPost(bssSubmitUrl, JSONObject.toJSONString(order), HttpsClientUtil.getHeader());
        } catch (KeycloakTokenException e) {
            log.info("Openapi createIptsBindEip Erroe");
            e.printStackTrace();
        }
        return null;
    }


    private String getEipBandwidth(OpenCreateEip openCreateEip) {
        Optional<Eip> optionalEip = eipRepository.findById(openCreateEip.getEipId());
        if (optionalEip.isPresent()) {
            Eip eipEntity = optionalEip.get();
            Integer bandwidthI = eipEntity.getBandWidth();
            String bandwidth = bandwidthI.toString();
            return bandwidth;
        }
        return null;
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


    private JSONArray getUserProductItems(String token) {
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

    private void buildIptsBindEip(List<Item> items, JSONArray itemArrayList, int i, String bandwidth, String sbwName, String sbwId) {
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
        if ("withIpv6".equals(object.getString("code"))) {
            item.setValue("yes");
            items.add(item);
        }
    }


    private void buildCreateEipAddSbwItemList(List<Item> items, JSONArray itemArrayList, int i, String bandwidth, String sbwId) {
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
        if ("sbwId".equals(object.getString("code"))) {
            item.setValue(sbwId);
            items.add(item);
        }
    }


}






