package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EipUpdateParam implements Serializable {


    @JsonAlias("serverid")
    private String serverId;

    @JsonAlias("portid")
    private String portId;

    //1：ecs // 2：cps // 3：slb
    @JsonProperty("type")
    private String type;

    private String duration;

    private int bandwidth;

    private String billType;

    @JsonAlias("privateip")
    private String privateIp;

    private String chargemode;

    @JsonProperty("sbwId")
    private String sbwId;
}
