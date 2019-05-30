package com.inspur.eip.exception;

import com.inspur.common.exception.BadRequestException;

/**
 * @Description 请求异常
 * @Author muning
 * @Date 2019/5/29 17:28
 **/
public class EipBadRequestException extends BadRequestException {
    public EipBadRequestException(String code, String message) {
        super(code, message);
    }
}
