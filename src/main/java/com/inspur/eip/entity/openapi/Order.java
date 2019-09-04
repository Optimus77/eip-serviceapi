package com.inspur.eip.entity.openapi;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    private String userId;
    private String token;
    private String orderRoute;
    private String setCount;
    private String consoleOrderFlowId;
    private String billType;
    private String duration;
    private String durationUnit;
    private String orderWhat;
    private String orderSource;
    private String orderType;
    private String serviceStartTime;
    private String serviceEndTime;
    private String rewardActivity;
    private String isAutoRenew;
    private JSONObject consoleCustomization;
    private String totalMoney;
    private String isAutoDeducted;
    private List<Product> originalProductList;
    private List<Product> productList;
}
