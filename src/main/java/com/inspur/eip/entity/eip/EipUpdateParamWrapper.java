package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class EipUpdateParamWrapper {
    @JsonProperty("eip")
    @Valid
    private EipUpdateParam eipUpdateParam;
}
