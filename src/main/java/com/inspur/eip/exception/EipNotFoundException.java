package com.inspur.eip.exception;

import com.inspur.common.exception.NotFoundException;

/**
 * @Description TODO
 * @Author muning
 * @Date 2019/5/29 17:31
 **/
public class EipNotFoundException extends NotFoundException {
    public EipNotFoundException(String code, String message) {
        super(code, message);
    }
}
