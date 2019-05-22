package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EipUpdateParam implements Serializable {

    @JsonProperty("serverid")
    private String serverid;

    @JsonProperty("portid")
    private String portid;

    //1：ecs // 2：cps // 3：slb
    @JsonProperty("type")
    private String type;

    private String duration;

    private int bandwidth;

    private String billType;

    @JsonProperty("slbip")
    private String slbIp;

    @JsonProperty("privateip")
    private String privateIp;

    private String chargemode;

    @JsonProperty("sbwid")
    private String sbwId;
}
