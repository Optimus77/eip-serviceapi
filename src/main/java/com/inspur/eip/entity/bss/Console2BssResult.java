package com.inspur.eip.entity.bss;

import lombok.Data;

import java.util.List;


@Data
public class Console2BssResult {

    private String userId;
    private String consoleOrderFlowId;
    private String orderId;
    private List<OrderResultProduct> productSetList;

}
