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
public class OpenApiSbwServiceImpl implements OpenApiSbwService{

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
        if (EipConstant.BILLTYPE_MONTHLY.equals(openCreateEip.getBillType())){
            if (StringUtils.isBlank(openCreateEip.getDuration())){
                throw new EipInternalServerException(ErrorStatus.INVALID_BILL_TYPE.getCode(),ErrorStatus.INVALID_BILL_TYPE.getMessage());
            }
        }

        //查询用户配额
        Map<String, String> paramMap = new HashMap<>();
        try {
            paramMap.put("userId", CommonUtil.getUserId(token));
            paramMap.put("region", regionCode);
            paramMap.put("productLineCode", EipConstant.PRODUCTLINE_CODE);
            ResponseEntity responseEntity = HttpClientUtil.doGet(bssQuotaUrl, paramMap, HttpsClientUtil.getHeader());
            JSONObject responseBodyJson = JSONObject.parseObject(responseEntity.getBody().toString());
            if ("0".equals(responseBodyJson.getString("code"))) {
                if (Integer.parseInt(responseBodyJson.getJSONObject("result").getJSONArray("quotaList").getJSONObject(0).getString("leftNumber")) == 0) {
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
                buildItemList(items,itemArraryList,i,openCreateEip.getBandwidth(),openCreateEip.getSbwName());
            }
        }
//      创建EIP订单报文
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
                    .orderSource(EipConstant.ORDER_SOURCE_CONSOLE)
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


    private void buildItemList(List<Item> items, JSONArray itemArrayList, int i,String bandwidth,String sbwName) {
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

}
