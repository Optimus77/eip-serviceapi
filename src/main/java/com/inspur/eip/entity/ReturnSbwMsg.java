package com.inspur.eip.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnSbwMsg<T> {
    private String code;
    private String message;
    private T sbw;
    private T data;


}
