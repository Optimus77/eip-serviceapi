package com.inspur.eip.entity.sbw;

import com.inspur.eip.util.TypeConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
@Data
public class SbwUpdateParam {
    @NotBlank(message = "can not be blank.")
    @TypeConstraint(allowedValues = {"cn-north-3","cn-south-1","cn-north-3-gov-1"}, message = "Only cn-north-3,cn-south-1 is allowed. ")
    private String region;

    @TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType = "hourlySettlement";

    @Pattern(regexp="[0-9-]{1,2}", message="param purchase time error.")
    private String duration;

    @Range(min=5,max=500,message = "value must be 5-500.")
    private int bandwidth;

    private String sbwName;

    @NotBlank(message = "can not be blank.")
    @TypeConstraint(allowedValues = {"mobile","radiotv", "telecom", "unicom", "BGP"}, message = "Only mobile,radiotv, telecom, unicom ,  BGP is allowed. ")
    private String ipType;

    private String groupId;

}
