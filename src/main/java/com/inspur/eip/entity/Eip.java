package com.inspur.eip.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;


@Data
public class Eip implements Serializable {


    private String eipId;

    private String name;

    private String ipVersion= "IPv4";

    @NotBlank
    private String eipAddress;

    private String privateIpAddress;

    private String floatingIp;

    private String floatingIpId;

    private String portId;

    private String instanceId;

    private String instanceType;

    private String vpcId;

    private String billType = "hourlySettlement";

    private String chargeMode = "BandWidth";

    private String duration;

    private int bandWidth;

    private int oldBandWidth =1;

    private String ipType;

    private String sharedBandWidthId;

    private String aclId;

    private String pipId;

    private String snatId;

    private String dnatId;

    private String firewallId;

    private String status ="DOWN";

    private String projectId;

    private String userId;

    private String region;

    private Date createTime  = new Date(System.currentTimeMillis());

    private Date updateTime;

    private int isDelete=0;

    private String eipV6Id;

}
