package com.inspur.eip.entity.v2.fw;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FwLoginResponseBody {
    private boolean success;
    private FwResponseResult result;
    private FwResponseException exception;
}
