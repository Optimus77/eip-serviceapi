package com.inspur.eip.entity.sbw;

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
public class SbwReturnBase implements Serializable {
    @JsonProperty("sbwId")
    private String sbwId;

    @JsonProperty("bandWidth")
    private int bandWidth;

    @JsonProperty("sbwName")
    private String sbwName;

    @JsonProperty("createTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonProperty("status")
    private String status;
}
