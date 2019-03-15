package com.inspur.eip.entity.sbw;

import com.inspur.eip.util.TypeConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class SbwAtomParam implements Serializable {

    @NotBlank(message = "can not be blank.")
    private String region;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType = "hourlySettlement";

    @Pattern(regexp="[0-9-]{1,2}", message="param purchase time error.")
    private String duration;

    @Range(min=5,max=500,message = "value must be 5-500.")
    private int bandwidth;

    @NotBlank(message = "can not be blank.")
    private String sbwName;

    private String method;
}
