package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnMsg<T> {
    private String code;
    private String message;
    private T eip;
    private T eipv6;
    private T data;

}
