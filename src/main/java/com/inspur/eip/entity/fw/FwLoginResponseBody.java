package com.inspur.eip.entity.fw;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FwLoginResponseBody {
    private boolean success;
    private FwResponseResult result;
    private FwResponseException exception;
}
