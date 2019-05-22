package com.inspur.eip.entity.Eipv6;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EipV6UpdateParam {

    @JsonProperty("eipaddress")
    private String eipaddress;


}
