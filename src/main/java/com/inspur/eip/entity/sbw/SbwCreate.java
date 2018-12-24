package com.inspur.eip.entity.sbw;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.util.List;

@Data
public class SbwCreate {
    private String userId;
    private String token;
    private String orderRoute = "SBW";
    private String setCount = "1";
    private String consoleOrderFlowId;
    private String billType = "monthly";
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
    private List<SbwProduct> productList;
    private List<String> originalProductList;
}
