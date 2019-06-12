package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class EipReturnByBandWidth implements Serializable {
    @JsonProperty("eipid")
    private String eipId;

    @JsonProperty("eip_address")
    private String eipAddress;
}
