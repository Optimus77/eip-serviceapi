package com.inspur.eip.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReturnMsg<T> {
    private String code;
    private String message;
    private T data;

}
