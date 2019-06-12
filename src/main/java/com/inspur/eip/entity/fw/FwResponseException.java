package com.inspur.eip.entity.fw;

import lombok.Data;


@Data
public class FwResponseException {
    private String code;
    private String message;
    private String stack;

}
