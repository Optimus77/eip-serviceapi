package com.inspur.eip.util;

import com.inspur.eip.entity.ReturnMsg;
import com.inspur.eip.entity.ReturnSbwMsg;

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

    public static <T> ReturnMsg msg(String code, String message , T t){
        return ReturnMsg.builder().code(code).message(message).data(t).build();
    }
    public static <T> ReturnSbwMsg successSbw(T t) {
        return ReturnSbwMsg.builder().sbw(t).build();
    }

    public static <T> ReturnMsg successEipv6(T t) {
        return ReturnMsg.builder().eipv6(t).build();
    }

}
