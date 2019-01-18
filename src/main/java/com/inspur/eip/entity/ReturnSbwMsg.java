package com.inspur.eip.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReturnSbwMsg<T> {
    private String code;
    private String message;
    private T sbw;

}
