package com.inspur.eip.entity.ipv6;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.eip.entity.ipv6.EipV6UpdateParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class EipV6UpdateParamWrapper {

    @Valid
    private EipV6UpdateParam eipv6;
}
