package com.inspur.eip.entity.sbw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SbwProduct{
    private String region;
    private String availableZone;
    private String productLineCode = "SBW";
    private String productTypeCode = "SBW";
    private String productName = "SBW";
    private String instanceCount = "1";
    private String instanceId;
    private String instanceStatus;
    private String statusTime;
    private List<SbwProductItem> itemList;
}
