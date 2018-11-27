package com.inspur.eip.entity;

import lombok.Data;

import java.util.List;

@Data
public class EipReciveOrder {
    private String consoleOrderFlowId;
    private String orderStatus;
    private String statusTime;
    private String orderId;
    private List<String> orderDetailFlowIdList;
    private EipOrder returnConsoleMessage;

}
