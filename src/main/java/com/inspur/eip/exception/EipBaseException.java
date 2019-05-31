package com.inspur.eip.exception;

import com.inspur.common.exception.BaseException;

/**
 * @Description TODO
 * @Author muning
 * @Date 2019/5/29 17:32
 **/
public class EipBaseException extends BaseException {
    public EipBaseException(int statusCode, String code, String message, String requestId) {
        super(statusCode, code, message, requestId);
    }
}
