package com.inspur.eip.entity.sbw;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SbwResultProduct{
    private String orderDetailFlowId;
    private String productSetStatus;
    private String billType;
    private String duration;
    private String durationUnit = "M";
    private String orderWhat = "formal";
    private String orderType = "new";
    private String serviceStartTime;
    private String serviceEndTime;
    private String rewardActivity;

    private List<SbwProduct> productList;
}
