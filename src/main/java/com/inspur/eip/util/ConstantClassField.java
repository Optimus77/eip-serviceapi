package com.inspur.eip.util;

/**
 * @Description TODO
 * @Author Zerah
 * @Date 2019/5/30 14:50
 **/
public final class ConstantClassField {

    private ConstantClassField() {
        throw new IllegalStateException("ConstantClassField class");
    }

    /**
     * EIP
     */
    public static final String EIP_CREATE = "CreateEipEntity";
    public static final String EIP_DELETE = "DeleteEipEntity";
    public static final String EIPS_SHOW = "ListEipEntitys";
    public static final String EIP_SHOW_DETAIL = "DescribeEipDetatil";


    public static final String PARSE_JSON_PARAM_ERROR = "parse_message_param_error! param : %s";
    public static final String PARSE_JSON_IO_ERROR = "parse_message_io_error! param : %s";


    public static final String ISUNIDIRECTIONALAUTH_NULL  = "when protocol is https,is_unidirectional_auth cannot be empty";
    public static final String SERVERCAID_NULL  = "when protocol is https and is_unidirectional_auth is 1,server_ca_id cannot be empty";

    public static final String EIP_BIND = "IntanceBindEip";
    public static final String EIP_UNBIND = "IntanceUnBindEip";

    public static final String EIP_SERVER_ID = "serverid";
    public static final String PRIVATEIP = "privateip";
    public static final String TYPE = "type";

    public static final String AUTHORIZATION = "authorization";
    public static final String INVOKE_URL = "Invoking url : ";

    public static final String DELETE_EIP_ERROR = "delete eip error: %s";

    public static final String REQUEST_ID = "Request-Id";

    public static final String EIP_PRODUCTLINE_CODE = "EIP";

    public static final String GROUP_ID = "groupId";
    public static final String ITEM_ID = "itemId";
    public static final String REGION_CODE = "regionCode";
    public static final String VALUE = "value";
    public static final String EIP_QUOTA = "slb-num";
    public static final String LISTENER_QUOTA = "slb-one-listener-num";
    public static final String MEMBER_QUOTA = "slb-one-listener-server-num";

    public static final String TAG_EIP = "eip";
    public static final String EIP_CHARGE_TYPE = "billType";
    public static final String EIP_DURATION = "duration";
    public static final String EMPTY = "";

    public static final String CHARGE_TYPE_POSTPAID = "hourlySettlement";

    public static final String EIP_NETWORK_MOD = "internet-facing";
    public static final String EIP_INTERNAL_MOD = "internal";

    public static final String CODE = "code";
    public static final String CODE_VALUE = "200";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_VALUE = "success";
    public static final String MESSAGE_VALUE_FAILED = "failed";

    public static final String KEYCLOAK_TOKEN_SUBPATH = "/realms/picp/protocol/openid-connect/token";

    public static final String VERSION_REST = "/v2.0";

    public static final String COMMAND = "command";
    public static final String OUTERNET = "outernet";
    public static final String INNERNET = "innernet";
    public static final String GW = "gw";
    public static final String CIDR = "cidr";
    public static final String LOCALIP = "localip";

    public static final String SSH_USERNAME = "SSH_USERNAME";
    public static final String SSH_MIMA = "SSH_PASSWORD";
    public static final String SSH_PORT = "SSH_PORT";

}
