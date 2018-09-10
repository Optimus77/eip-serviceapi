package com.inspur.eip.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
    public static String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}
