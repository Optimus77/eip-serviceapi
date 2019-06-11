package com.inspur.eip.entity.v2.eip;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.eip.entity.EipAllocateParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.io.Serializable;

@Getter
@Setter
public class EipAllocateParamWrapper implements Serializable {
    @JsonProperty("eip")
    @Valid
    private EipAllocateParam eipAllocateParam;
}
