package com.inspur.eip.entity.iam;

import lombok.Data;

@Data
public class IamParam {

    private String region;
    private String service="iam";
    private String resourceType;
    private String action;
    private String instanceId;
    private String resourceAccountId;
    private ContextIam context;
    private String resourceCreator;
}
