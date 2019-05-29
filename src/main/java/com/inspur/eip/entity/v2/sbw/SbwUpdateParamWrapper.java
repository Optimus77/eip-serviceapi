package com.inspur.eip.entity.v2.sbw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NotNull(message="the body must be a json ")
public class SbwUpdateParamWrapper implements Serializable {

    @JsonProperty("sbw")
    @Valid
    @NotNull(message="the body must be a json and sbw is not null")
    private SbwUpdateParam sbwUpdateParam;
}
