package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipReturnAdminDetail implements Serializable {

    @JsonProperty("eipId")
    private String id;

    @JsonProperty("eipAddress")
    private String eipAddress;

    @JsonProperty("billType")
    private String billType;

    @JsonProperty("chargeMode")
    private String chargeMode;

    @JsonProperty("bandwidth")
    private int bandWidth;

    @JsonProperty("oldBandwidth")
    private int oldBandWidth;

    @JsonProperty("ipType")
    private String ipType;

    @JsonProperty("resourceSet")
    private Resourceset resourceset;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createdTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdTime;

    @JsonProperty("region")
    private String region;

}
