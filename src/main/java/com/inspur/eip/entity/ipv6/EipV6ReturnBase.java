package com.inspur.eip.entity.ipv6;

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
public class EipV6ReturnBase implements Serializable {
    @JsonProperty("eipv6Id")
    private String id;

    @JsonProperty("ipv6")
    private String ipv6;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdTime;

    @JsonProperty("ipv4")
    private String ipv4;
}
