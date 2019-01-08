package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
public class EipDelParam implements Serializable {
    @JsonProperty("eipids")
    private List<String> eipids;
}
