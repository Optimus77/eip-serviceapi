package com.inspur.eip.entity.ipv6;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipV6ReturnDetail implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("ipv6")
    private String ipv6;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @JsonProperty("region")
    private String region;

    @JsonProperty("eipBandwidth")
    private int eipBandwidth;


    @JsonProperty("eipBillType")
    private String eipChargeType;

    @JsonProperty("eipv6Bandwidth")
    private int eipv6Bandwidth;


    @JsonProperty("eipId")
    private String eipId;


    @JsonProperty("ipv4")
    private String ipv4;

}
