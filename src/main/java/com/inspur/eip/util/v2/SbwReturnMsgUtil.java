package com.inspur.eip.util.v2;

import com.inspur.eip.entity.v2.SbwReturnMsg;

public class SbwReturnMsgUtil {

    public static <T> SbwReturnMsg success(T t) {
        return SbwReturnMsg.builder().sbw(t).build();
    }


    public static SbwReturnMsg success() {
        return success(null);
    }

    public static SbwReturnMsg error(String code, String msg) {
        return SbwReturnMsg.builder().code(code).message(msg).build();
    }

    public static <T> SbwReturnMsg msg(String code,String message ,T t){
        return SbwReturnMsg.builder().code(code).message(message).data(t).build();
    }
}
