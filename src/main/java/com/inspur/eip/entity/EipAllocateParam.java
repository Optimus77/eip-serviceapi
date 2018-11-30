package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.inspur.eip.util.TypeConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class EipAllocateParam implements Serializable {

    @NotBlank(message = "can not be blank.")
    private String region;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType = "hourlySettlement";

    @Pattern(regexp="[0-9-]{1,2}", message="param purchase time error.")
    private String duration;

    private String durationUnit = "M";

    @NotBlank(message = "can not be blank.")
    @TypeConstraint(allowedValues = {"5_bgp","5_sbgp", "5_telcom", "5_union", "BGP"}, message = "Only 5_bgp,5_sbgp, 5_telcom, 5_union, BGP is allowed. ")
    private String iptype;

    @TypeConstraint(allowedValues = {"Bandwidth","SharedBandwidth"}, message = "Only Bandwidth,SharedBandwidth is allowed. ")
    private String chargemode = "Bandwidth";

    @Range(min=1,max=2000,message = "value must be 1-2000.")
    private int bandwidth;

    @JsonProperty("sharedbandwidthid")
    private String sharedBandWidthId;
}