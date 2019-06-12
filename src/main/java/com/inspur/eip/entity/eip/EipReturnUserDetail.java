package com.inspur.eip.entity.eip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipReturnUserDetail implements Serializable {

    @JsonProperty("eipid")
    private String eipId;

    @JsonProperty("eip_address")
    private String eipAddress;

    @JsonProperty("create_at")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("userName")
    private String projectId;

    @JsonProperty("status")
    private String status;
}
