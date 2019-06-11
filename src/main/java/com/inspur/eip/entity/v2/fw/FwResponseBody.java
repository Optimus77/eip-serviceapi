package com.inspur.eip.entity.v2.fw;

import lombok.Data;


@Data
public class FwResponseBody {
    private boolean success = false;
    private FwResponseException exception;
    private Object object;

}
