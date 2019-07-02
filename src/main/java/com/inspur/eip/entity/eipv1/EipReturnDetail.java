package com.inspur.eip.entity.eipv1;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipReturnDetail implements Serializable {

    @JsonProperty("eipid")
    private String id;

    @Column(name="eip_address")
    @JsonProperty("eip_address")
    private String eipAddress;

    @JsonProperty("billType")
    private String billType;

    @JsonProperty("chargemode")
    private String chargeMode;

    @JsonProperty("bandwidth")
    private int bandWidth;

    @JsonProperty("oldBandwidth")
    private int oldBandWidth;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("iptype")
    private String ipType;

    @JsonProperty("sbwId")
    private String sbwId;

    @JsonProperty("private_ip_address")
    private String privateIpAddress;

    @JsonProperty("resourceset")
    private Resourceset resourceset;

    @JsonProperty("status")
    private String status;

    @JsonProperty("create_at")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdTime;

    @JsonProperty("region")
    private String region;

    @JsonProperty("eipv6id")
    private String eipV6Id;

    @JsonProperty("v6Address")
    private String ipv6;

}

