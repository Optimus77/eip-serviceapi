package com.inspur.eip.entity.v2.eip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;


@Data
public class EipReturnByBandWidth implements Serializable {
    @JsonProperty("eipId")
    private String eipId;

    @Column(name="eip_address")
    @JsonProperty("eipAddress")
    private String eipAddress;
}
