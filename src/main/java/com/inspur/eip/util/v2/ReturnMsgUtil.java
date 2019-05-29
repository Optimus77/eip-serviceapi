package com.inspur.eip.util.v2;

import com.inspur.eip.entity.v2.ReturnMsg;

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

    public static <T> ReturnMsg msg(String code,String message ,T t){
        return ReturnMsg.builder().code(code).message(message).data(t).build();
    }
}
