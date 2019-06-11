package com.inspur.eip.exception;

/**
 * @Description TODO
 * @Author muning
 * @Date 2019/5/29 18:34
 **/
public class EipUnauthorizedException extends EipBaseException {
    public EipUnauthorizedException(int statusCode, String code, String message, String requestId) {
        super(statusCode, code, message, requestId);
    }
}
