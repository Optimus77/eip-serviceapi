package com.inspur.eip.util.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ValidatorUtil
 * @Description 正则表达式验证工具类
 * @Author cp from ecs 2019.06.26
 * @CreateDate 2018/10/22 16:34:00
 */
public class ValidatorUtil {

    // (1)定义正则表达式
    /**
     * Integer正则表达式 ^-?(([1-9]\d*$)|0)
     */
    public static final String INTEGER = "^-?(([1-9]\\d*$)|0)";
    /**
     * 正整数正则表达式 >=0  ^[1-9]\d*|0$
     */
    public static final String INTEGER_NEGATIVE = "^[1-9]\\d*|0$";
    /**
     * 负整数正则表达式 <=0  ^-[1-9]\d*|0$
     */
    public static final String INTEGER_POSITIVE = "^-[1-9]\\d*|0$";
    /**
     * Double正则表达式 ^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$
     */
    public static final String DOUBLE = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
    /**
     * 正Double正则表达式 >=0  ^[1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0$
     */
    public static final String DOUBLE_NEGATIVE = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$";
    /**
     * 负Double正则表达式 <= 0  ^(-([1-9]\d*\.\d*|0\.\d*[1-9]\d*))|0?\.0+|0$
     */
    public static final String DOUBLE_POSITIVE = "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$";
    /**
     * 匹配由26个英文字母组成的字符串  ^[A-Za-z]+$
     */
    public static final String STR_ENG = "^[A-Za-z]+$";
    /**
     * 验证中文
     */
    public static final String CHINESE = "^[\u4E00-\u9FA5]+$";
    /**
     * 支持 中文 字母 数字 - _
     */
    public static final String IMAGE_STANDARD_STR = "^[a-zA-Z0-9\\-_\u4e00-\u9fa5]+$";
    /**
     * 支持 中文 字母 数字 . - _
     */
    public static final String SERVER_STANDARD_STR = "^[a-zA-Z0-9.\\-_\u4e00-\u9fa5]+$";

    /**
     * 名称规则：长度为2-128个字符，不能以特殊字符及数字开头，只可包含特殊字符中的"."，"_"或"-"
     */
    public static final String DOT_STANDARD_STR = "^[a-zA-Z\\u4e00-\\u9fa5]+[a-zA-Z0-9-_.\\u4e00-\\u9fa5]{1,127}+$";

    /**
     * 名称规则: 长度为2-64个字符，不能以特殊字符及数字开头，只可包含特殊字符中的"_"或"-"
     */
    public static final String LINE_STANDARD_STR = "^[a-zA-Z\\u4e00-\\u9fa5]+[a-zA-Z0-9-_\\u4e00-\\u9fa5]{1,63}+$";

    // (2)正则表达式使用

    /**
     * 判断名称规则, 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isDOT_STANDARD_STR(String str) {
        return regular(str, DOT_STANDARD_STR);
    }

    /**
     * @param str
     * @return boolean
     */
    public static boolean isLINE_STANDARD_STR(String str) {
        return regular(str, LINE_STANDARD_STR);
    }

    /**
     * 判断字段是否为INTEGER  符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isInteger(String str) {
        return regular(str, INTEGER);
    }

    /**
     * 判断字段是否为正整数正则表达式 >=0 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isINTEGER_NEGATIVE(String str) {
        return regular(str, INTEGER_NEGATIVE);
    }

    /**
     * 判断字段是否为负整数正则表达式 <=0 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isINTEGER_POSITIVE(String str) {
        return regular(str, INTEGER_POSITIVE);
    }


    /**
     * 判断字段是否为DOUBLE 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isDouble(String str) {
        return regular(str, DOUBLE);
    }

    /**
     * 判断字段是否为正浮点数正则表达式 >=0 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isDOUBLE_NEGATIVE(String str) {
        return regular(str, DOUBLE_NEGATIVE);
    }

    /**
     * 判断字段是否为负浮点数正则表达式 <=0 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isDOUBLE_POSITIVE(String str) {
        return regular(str, DOUBLE_POSITIVE);
    }

    /**
     * 判断字段是否为由26个英文字母组成的字符串 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isSTR_ENG(String str) {
        return regular(str, STR_ENG);
    }

    /**
     * 判断字段是否是否匹配中文 字母 数字 - _ 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isIMAGE_STANDARD_STR(String str) {
        return regular(str, IMAGE_STANDARD_STR);
    }

    /**
     * 判断字段是否匹配中文 字母 数字 . - _ 符合返回true
     *
     * @param str
     * @return boolean
     */
    public static boolean isSERVER_STANDARD_STR(String str) {
        return regular(str, SERVER_STANDARD_STR);
    }


    // 在此方法上面写:定义和使用自定义正则验证的方法

    /**
     * 匹配是否符合正则表达式pattern 匹配返回true
     *
     * @param str     匹配的字符串
     * @param pattern 匹配模式
     * @return boolean
     */
    public static boolean regular(String str, String pattern) {
        if (null == str || str.trim().length() <= 0)
            return false;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
