package com.inspur.eip.exception;

import com.inspur.common.exception.InvalidParameterException;

/**
 * @Description TODO
 * @Author muning
 * @Date 2019/5/29 17:31
 **/
public class EipInvalidParameterException extends InvalidParameterException {
    public EipInvalidParameterException(String code, String message) {
        super(code, message);
    }
}
