package com.inspur.eip.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Eip {
    private String region;
    private String id;
    private String name;
    private String eipIpv4;
    private String floatingIpv4;
    private String fixedIpv4;
    private String eiIpv6;
    private String floatingIpv6;
    private String fixedIpv6;
    private String instanceId;
    private String instanceType;
    private String vpcId;
    private String bandWidth;
    private String linkType;
    private String aclId;
    private String sharedBandWidthId;
    private String state;
    private String update_at;
    private String created_at;
}
