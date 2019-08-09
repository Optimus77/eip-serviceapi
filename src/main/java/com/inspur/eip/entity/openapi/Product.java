package com.inspur.eip.entity.openapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
    private String region;
    private String availableZone;
    private String productLineCode;
    private String productTypeCode;
    private String productName;
    private String instanceCount;
    private String instanceId;
    private String instanceStatus;
    private String statusTime;
    private List<Item> itemList;
}
