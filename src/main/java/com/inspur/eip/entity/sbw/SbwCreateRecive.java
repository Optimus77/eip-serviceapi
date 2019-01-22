package com.inspur.eip.entity.sbw;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SbwCreateRecive implements Serializable {
    private String consoleOrderFlowId;
    private String orderStatus;
    private String statusTime;
    private String orderId;
    private List<String> orderDetailFlowIdList;
    private SbwCreate returnConsoleMessage;

}
