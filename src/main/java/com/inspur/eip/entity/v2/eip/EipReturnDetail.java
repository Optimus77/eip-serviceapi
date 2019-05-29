package com.inspur.eip.entity.v2.eip;

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

    @JsonProperty("eipId")
    private String eipId;

    @Column(name="eip_address")
    @JsonProperty("eipAddress")
    private String eipAddress;

    @JsonProperty("billType")
    private String billType;

    @JsonProperty("chargeMode")
    private String chargeMode;

    @JsonProperty("bandWidth")
    private int bandWidth;

    @JsonProperty("oldBandwidth")
    private int oldBandWidth;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("ipType")
    private String ipType;

    @JsonProperty("sbwId")
    private String sbwId;

    @JsonProperty("privateIpAddress")
    private String privateIpAddress;

    @JsonProperty("resourceSet")
    private Resourceset resourceset;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonProperty("region")
    private String region;

    @JsonProperty("eipv6Id")
    private String eipV6Id;

    @JsonProperty("ipv6Address")
    private String ipv6;

}
