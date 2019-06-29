package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
public class EipUpdateParam implements Serializable {

    @JsonProperty("serverId")
    private String serverId;

    @JsonProperty("portId")
    private String portId;

    //1：ecs // 2：cps // 3：slb
    @JsonProperty("type")
    private String type;

    private String duration;

    private int bandwidth;

    private String billType;

    @JsonProperty("privateIp")
    private String privateIp;

    private String chargemode;

    @JsonProperty("sbwId")
    private String sbwId;
}
