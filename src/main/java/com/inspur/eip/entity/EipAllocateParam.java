package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import java.io.Serializable;

@Data
public class EipAllocateParam implements Serializable {

    @NonNull
    @JsonProperty("region")
    private String region;

    @NonNull
    @JsonProperty("iptpye")
    private String ipType;

    @JsonProperty("chargetype")
    private String chargeType = "PrePaid";

    //BandWidth, ShareBandwidth
    @JsonProperty("chargemode")
    private String chargeMode = "BandWidth";

    @JsonProperty("puchasetime")
    private String puchaseTime;

    @JsonProperty("bandwidth")
    private String banWidth;

    @JsonProperty("sharedbandwidthid")
    private String sharedBandWidthId;
}
