package com.inspur.eip.entity.bss;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SoftDownInstance {

    private String subFlowId;

    private String operateType;

    private String productLineCode;

    private String productTypeCode;

    private String instanceId;

    private String result;

    private String statusTime;

    private String instanceStatus;

}
