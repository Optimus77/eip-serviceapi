package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EipUpdateParam {

    @JsonProperty("bandwidth")
    private String bandWidth;

    @JsonProperty("chargetype")
    private String chargeType;

    @JsonProperty("port_id")
    private String portId;
}
