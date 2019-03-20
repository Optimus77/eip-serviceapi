package com.inspur.eip.entity.sbw;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.eip.util.TypeConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
@Data
public class SbwUpdateParam {
    @NotBlank(message = "can not be blank.")
    private String region;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType = "hourlySettlement";

    @Pattern(regexp="[0-9-]{1,2}", message="param purchase time error.")
    private String duration;

    @Range(min=5,max=500,message = "value must be 5-500.")
    private int bandwidth;

    private String sbwName;

}
