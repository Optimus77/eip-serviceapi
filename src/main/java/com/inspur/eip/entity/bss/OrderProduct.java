package com.inspur.eip.entity.bss;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import lombok.Data;

import java.util.List;
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProduct {
    private String region;
    private String availableZone;
    @Builder.Default
    private String productLineCode = "EIP";
    @Builder.Default
    private String productTypeCode = "EIP";
    @Builder.Default
    private String instanceCount = "1";
    private String instanceId;
    private String instanceStatus;
    private String statusTime;
    private List<OrderProductItem> itemList;
}
