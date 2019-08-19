package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.inspur.eip.util.TypeConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class EipAllocateParam implements Serializable {

    @TypeConstraint(allowedValues = {"cn-north-3","cn-south-1","cn-north-3-gov-1"}, message = "Only cn-north-3,cn-south-1 is allowed. ")
    private String region;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType = "hourlySettlement";

    @Pattern(regexp="[0-9-]{1,2}", message="param purchase time error.")
    private String duration;

    private String ipv6 = "no";

    @NotBlank(message = "can not be blank.")
    @TypeConstraint(allowedValues = {"5_bgp","5_sbgp", "5_telcom", "5_union", "BGP"}, message = "Only 5_bgp,5_sbgp, 5_telcom, 5_union, BGP is allowed. ")
    private String ipType;

    @TypeConstraint(allowedValues = {"Bandwidth","SharedBandwidth","Traffic"}, message = "Only Bandwidth,SharedBandwidth,Traffic is allowed. ")
    private String chargeMode = "Bandwidth";

    @Range(min=1,max=500,message = "value must be 1-500.")
    private int bandwidth;

    @JsonProperty("sbwId")
    private String sbwId;
}
