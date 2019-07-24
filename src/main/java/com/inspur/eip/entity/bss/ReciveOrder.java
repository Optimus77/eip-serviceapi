package com.inspur.eip.entity.bss;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder

@Data
public class ReciveOrder {
    private String consoleOrderFlowId;
    private String orderId;
    private String orderStatus;
    private String statusTime;
    private String token;
    @Builder.Default
    private String orderRoute = "EIP";
    private JSONObject consoleCustomization;
    private String userId;
    @Builder.Default
    private String setCount = "1";
    @Builder.Default
    private String billType = "monthly";
    @Builder.Default
    private String orderType = "new";
    private String duration;
    private String durationUnit;
    private List<OrderProduct> productList;

}
