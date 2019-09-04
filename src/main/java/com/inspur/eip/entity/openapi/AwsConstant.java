package com.inspur.eip.entity.openapi;

public class AwsConstant {
    private AwsConstant() {
    }

    public static final String ACTION_GET_TOKEN = "GetToken";

    public static final String HEAD_PARAM_AUTHORIZATION = "Authorization";

    public static final String PARAM_ACTION = "Action";
    public static final String PARAM_TIMESTAMP = "Timestamp";
    public static final String PARAM_NONCE = "Nonce";
    public static final String PARAM_VERSION = "Version";
    public static final String PARAM_FORMAT = "Format";

    public static final String URL_SPRIT = "/";
    public static final String URL_REALM = "realms/";
    public static final String URL_GET_TOKEN = "/protocol/openid-connect/token";

    public static final String LOG_HEAD = "head: ";
    public static final String LOG_PARAM = "param: ";
    public static final String LOG_BODY = "body: ";
    public static final String LOG_RESULT = "result: ";
}
