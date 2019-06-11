package com.inspur.eip.entity.v2.eip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.NonNull;

@Data
public class EipUpdateParam {
    //@TypeConstraint(allowedValues = {"monthly","hourlySettlement"}, message = "Only monthly,hourlySettlement is allowed. ")
    private String billType;

    //@Range(min=1,max=500)
    @JsonProperty("bandWidth")
    private int bandWidth;

    @JsonProperty("serverId")
    private String serverId;

    @JsonProperty("portId")
    private String portId;

    //1：ecs // 2：cps // 3：slb

    @JsonProperty("type")
    private String type;

    @JsonProperty("privateIp")
    private String privateIp;

    private String chargemode;

    private String sbwId;

    private String duration;
}
