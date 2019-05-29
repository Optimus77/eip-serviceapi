package com.inspur.eip.entity.v2.sbw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.io.Serializable;

@Getter
@Setter
@Data
public class SbwAllocateParamWrapper implements Serializable {
    @JsonProperty("sbw")
    @Valid
    private SbwAllocateParam sbwAllocateParam;
}
