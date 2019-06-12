package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Setter
@Getter
public class EipDelParam implements Serializable {
    @JsonProperty("eipIds")
    private List<String> eipids;
}
