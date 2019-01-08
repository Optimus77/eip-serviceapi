package com.inspur.eip.util;

import com.inspur.eip.entity.ReturnMsg;

public class ReturnMsgUtil {
    public static <T> ReturnMsg success(T t) {
        return ReturnMsg.builder().eip(t).build();
    }


    public static ReturnMsg success() {
        return success(null);
    }

    public static ReturnMsg error(String code, String msg) {
        return ReturnMsg.builder().code(code).message(msg).build();
    }

}
