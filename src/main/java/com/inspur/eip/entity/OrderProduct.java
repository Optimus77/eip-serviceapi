package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProduct {

    private String region;
    private String availableZone;
    private String productLineCode = "EIP";
    private String productTypeCode = "EIP";
    private String instanceCount = "1";
    private String instanceId;
    private String instanceStatus;
    private String statusTime;
    private List<OrderProductItem> itemList;
}
