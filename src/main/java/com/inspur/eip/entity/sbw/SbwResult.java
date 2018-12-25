package com.inspur.eip.entity.sbw;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SbwResult{
    private String userId;
    private String consoleOrderFlowId;
    private String orderId;
    private List<SbwResultProduct> productSetList;
}
