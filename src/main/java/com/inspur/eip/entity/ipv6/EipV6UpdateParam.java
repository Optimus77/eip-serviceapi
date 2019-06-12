package com.inspur.eip.entity.ipv6;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EipV6UpdateParam {

    @JsonProperty("eipAddress")
    @NotBlank(message = "can not be blank.")
    private String eipAddress;


}
