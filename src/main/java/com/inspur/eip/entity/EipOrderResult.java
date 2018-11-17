package com.inspur.eip.entity;

import lombok.Data;

import java.util.List;


@Data
public class EipOrderResult {

    private String userId;
    private String consoleOrderFlowId;
    private String orderId;
    private List<EipOrderResultProduct> productSetList;

}
