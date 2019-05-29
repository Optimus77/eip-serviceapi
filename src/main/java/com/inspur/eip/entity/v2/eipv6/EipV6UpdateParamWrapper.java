package com.inspur.eip.entity.v2.eipv6;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class EipV6UpdateParamWrapper {
    @JsonProperty("eipv6")
    @Valid
    private EipV6UpdateParam eipV6UpdateParam;
}
