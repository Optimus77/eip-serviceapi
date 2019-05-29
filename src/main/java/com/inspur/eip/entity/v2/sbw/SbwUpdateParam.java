package com.inspur.eip.entity.v2.sbw;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.eip.util.TypeConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Data
public class SbwUpdateParam implements Serializable {
    @JsonProperty(value = "bandwidth")
    private int bandWidth=5;

    private String duration;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType = "hourlySettlement";

    private String sbwName;

    private String region;

}
