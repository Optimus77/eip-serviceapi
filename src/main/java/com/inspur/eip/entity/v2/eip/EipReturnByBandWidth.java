package com.inspur.eip.entity.v2.eip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;


@Data
public class EipReturnByBandWidth implements Serializable {
    @JsonProperty("eipid")
    private String eipId;

    @JsonProperty("eip_address")
    private String eipAddress;
}
