package com.inspur.eip.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class EipReciveOrder {
    private String consoleOrderFlowId;
    private String orderId;
    private String orderStatus;
    private String statusTime;
    private String token;
    private String orderRoute = "EIP";
    private JSONObject consoleCustomization;
    private String userId;
    private String setCount = "1";
    private String billType = "monthly";
    private String orderType = "new";
    private String duration;
    private String durationUnit;
    private List<EipOrderProduct> productList;

}
