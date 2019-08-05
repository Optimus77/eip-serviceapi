package com.inspur.eip.entity.eip;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.bss.OrderProduct;
import lombok.Data;

import java.util.List;

@Data
public class EipRenewOrder {

    private String userId;
    private String token;
    private String orderRoute;
    private String setCount;
    private String consoleOrderFlowId;
    private List   flowIdList;
    private String billType;
    private String duration;
    private String durationUnit = "M";
    private String orderWhat = "formal";
    private String orderSource = "console";
    private String orderType = "new";
    private String serviceStartTime;
    private String serviceEndTime;
    private String rewardActivity;
    private JSONObject consoleCustomization;
    private String totalMoney;
    private List<OrderProduct> originalProductList;
    private List<OrderProduct> productList;

}
