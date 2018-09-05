package com.inspur.icp.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReturnMsg<T> {
    private int code;
    private String msg;
    private T data;
}
