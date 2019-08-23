package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipReturnBase implements Serializable {
    @JsonProperty("eipId")
    private String id;

    @JsonProperty("eipAddress")
    private String eipAddress;

    @JsonProperty("bandwidth")
    private int bandWidth;

    @JsonProperty("ipType")
    private String ipType;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createdTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdTime;

    @JsonProperty("groupId")
    private String groupId;

}
