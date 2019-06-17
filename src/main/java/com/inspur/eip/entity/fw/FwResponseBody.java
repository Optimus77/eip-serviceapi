package com.inspur.eip.entity.fw;

import lombok.Data;


@Data
public class FwResponseBody {
    private boolean success = false;
    private FwResponseException exception;
    private Object object;

}
