package com.inspur.eip.entity.sbw;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SbwReturnDetail implements Serializable {

    @JsonProperty("sbwId")
    private String id;

    @JsonProperty("sbwName")
    private String sbwName;

    @JsonProperty("billType")
    private String billType;

    @JsonProperty("chargeMode")
    private String chargeMode ="Bandwidth";

    @JsonProperty("bandwidth")
    private int bandWidth;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("ipCount")
    private int ipCount = 0;

    @JsonProperty("createTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "GMT+8", pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdTime;

    @JsonProperty("status")
    private String status;

    //  前端需要，res:宋丽芳 8.20
    @JsonProperty("region")
    private String region;


}
