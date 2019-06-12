package com.inspur.eip.entity.ipv6;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EipV6UpdateParam {

    @JsonProperty("eipAddress")
    private String eipAddress;


}
