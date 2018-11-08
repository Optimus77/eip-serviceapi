package com.inspur.eip.entity;

import lombok.Data;

@Data
public class EipQuota {
    private String userId;
    private String region;
    private String productLineCode;
    private String productTypeCode;
    private String quotaType;
}
