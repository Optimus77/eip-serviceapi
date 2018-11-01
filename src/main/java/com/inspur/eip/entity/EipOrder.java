package com.inspur.eip.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author: jiasirui
 * @date: 2018/10/24 22:28
 * @description:
 */
@Data
public class EipOrder {

    private String userId;
    private String productLineCode="EIP";
    private String setCount = "1";
    private String consoleOrderFlowId;
    private List   flowIdList;
    private String billType = "monthly";
    private String duration;
    private String durationUnit = "M";
    private String orderWhat = "formal";
    private String orderType = "new";
    private String serviceStartTime;
    private String serviceEndTime;
    private String rewardActivity;
    private JSONObject consoleCustomization;
    private String totalMoney;
    private List<EipOrderProduct> productList;

}
