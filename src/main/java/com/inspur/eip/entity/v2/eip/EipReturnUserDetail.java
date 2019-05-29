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
public class EipReturnUserDetail implements Serializable {

    @JsonProperty("eipId")
    private String eipId;

    @Column(name="eip_address")
    @JsonProperty("eipAddress")
    private String eipAddress;

    @JsonProperty("createTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("userName")
    private String projectId;

    @JsonProperty("status")
    private String status;
}
