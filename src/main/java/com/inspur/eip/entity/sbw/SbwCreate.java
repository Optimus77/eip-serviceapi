package com.inspur.eip.entity.sbw;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SbwCreate{
    private String userId;
    private String orderId;
    private String orderStatus;
    private String statusTime;
    private String token;
    private String orderRoute = "SBW";
    private String setCount = "1";
    private String consoleOrderFlowId;
    private String billType = "monthly";
    private String orderType = "new";
    private JSONObject consoleCustomization;
    private List<SbwProduct> productList;
}
