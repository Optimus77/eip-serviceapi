package com.inspur.eip.util;

import com.inspur.eip.entity.ReturnMsg;

public class ReturnMsgUtil {

    public static <T> ReturnMsg success(T t) {
        ReturnMsg<Object> returnMsg = ReturnMsg.builder().code(200).msg("success").data(t).build();

        return returnMsg;
    }


    public static ReturnMsg success() {
        return success(null);
    }

    public static ReturnMsg error(Integer code, String msg) {
        return ReturnMsg.builder().code(code).msg(msg).build();
    }
}
