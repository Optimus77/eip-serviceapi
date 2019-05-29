package com.inspur.eip.entity.v2.sbw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Setter
@Getter
public class SbwDelParam implements Serializable {
    @JsonProperty("sbwid")
    private String sbwid;
}
