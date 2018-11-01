package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipOrderProduct {

    private String region;
    private String availableZone;
    private String productTypeCode = "EIP";
    private String productName = "EIP";
    private String instanceCount = "1";
    private String instanceId;
    private String instanceStatus;
    private String createTime;
    private List<EipOrderProductItem> itemList;
}
