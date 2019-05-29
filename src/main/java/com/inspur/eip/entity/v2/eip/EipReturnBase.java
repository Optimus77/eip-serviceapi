package com.inspur.eip.entity.v2.eip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EipReturnBase implements Serializable {
    @JsonProperty("eipId")
    private String eipId;

    @Column(name="eip_address")
    @JsonProperty("eipAddress")
    private String eipAddress;

    @JsonProperty("bandWidth")
    private int bandWidth;

    @JsonProperty("ipType")
    private String ipType;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
