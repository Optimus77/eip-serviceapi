package com.inspur.eip.exception;

import com.inspur.common.exception.InternalServerException;

/**
 * @Description TODO
 * @Author muning
 * @Date 2019/5/29 17:29
 **/
public class EipInternalServerException extends InternalServerException {
    public EipInternalServerException(String code, String message) {
        super(code, message);
    }
}
