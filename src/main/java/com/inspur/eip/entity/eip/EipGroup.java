package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipGroup implements Serializable {

    @JsonProperty("eipId")
    private String id;

    @JsonProperty("eipAddress")
    private String eipAddress;

    @JsonProperty("bandwidth")
    private int bandWidth;

    @JsonProperty("oldBandwidth")
    private int oldBandWidth;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("sbwId")
    private String sbwId;

    @JsonProperty("ipType")
    private String ipType;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createdTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdTime;


    @JsonProperty("eipv6Id")
    private String eipV6Id;

    @JsonProperty("ipv6Address")
    private String ipv6;

}
