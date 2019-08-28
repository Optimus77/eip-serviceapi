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
    private String id;

    @JsonProperty("bandwidth")
    private int bandWidth;

    @JsonProperty("sbwName")
    private String sbwName;

    @JsonProperty("createTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, timezone = "UTC", pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdTime;

    @JsonProperty("status")
    private String status;

    //  ǰ����Ҫ��res:������ 8.20
    @JsonProperty("region")
    private String region;
}
