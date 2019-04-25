package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
public class EipUpdateParam implements Serializable {

    @JsonProperty("serverid")
    private String serverId;

    @JsonProperty("portid")
    private String portId;

    //1：ecs // 2：cps // 3：slb
    @JsonProperty("type")
    private String type;

    private String duration;

    private int bandwidth;

    private String billType;

    @JsonProperty("privateip")
    private String privateIp;

    private String chargemode;

    @JsonProperty("sharedbandwidthid")
    private String sharedBandWidthId;
}
