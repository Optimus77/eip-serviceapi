package com.inspur.eip.entity.sbw;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.eip.util.TypeConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
@Data
public class SbwAllocateParam {

    @NotBlank(message = "can not be blank.")
    private String region;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType = "hourlySettlement";

    @Pattern(regexp="[0-9-]{1,2}", message="param purchase time error.")
    private String duration;

    private String durationUnit = "M";

    @TypeConstraint(allowedValues = {"Bandwidth","SharedBandwidth"}, message = "Only Bandwidth,SharedBandwidth is allowed. ")
    private String chargemode = "Bandwidth";

    @Range(min=5,max=2000,message = "value must be 1-2000.")
    private int bandwidth;

    @JsonProperty
    private JSONObject consoleCustomization;
}
