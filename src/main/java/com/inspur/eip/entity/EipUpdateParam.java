package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EipUpdateParam {

    @JsonProperty("band_width")
    private String bandWidth;

    @JsonProperty("charge_type")
    private String chargeType;

    @JsonProperty("server_id")
    private String serverId;

    //1：ecs // 2：cps // 3：slb
    @JsonProperty("type")
    private String type;
}
