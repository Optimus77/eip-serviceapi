package com.inspur.eip.entity.iam;

import lombok.Data;

import java.util.Map;

@Data
public class IamParam {

    private String region;
    private String server="iam";
    private String resourceType;
    private String action;
    private String instanceId;
    private String resourceAccountId;
    private ContextIam context;
}
