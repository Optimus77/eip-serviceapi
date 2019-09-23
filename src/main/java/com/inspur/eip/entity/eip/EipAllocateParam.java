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

    @NotBlank(message = "can not be blank.")
    private String region;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement","hourlyNetflow"}, message = "Only monthly,hourlySettlement,hourlyNetflow is allowed. ")
    private String billType = "hourlySettlement";

    @Pattern(regexp="[0-9-]{1,2}", message="param purchase time error.")
    private String duration;

    private String ipv6 = "no";

    @NotBlank(message = "can not be blank.")
    @TypeConstraint(allowedValues = {"mobile","radiotv", "telecom", "unicom", "BGP"}, message = "Only mobile,radiotv, telecom, unicom ,  BGP is allowed. ")
    private String ipType;

    @TypeConstraint(allowedValues = {"Bandwidth","SharedBandwidth"}, message = "Only Bandwidth,SharedBandwidth is allowed. ")
    private String chargeMode = "Bandwidth";

    @Range(min=1,max=500,message = "value must be 1-500.")
    private int bandwidth;

    @JsonProperty("sbwId")
    private String sbwId;

    private String groupId;
}
