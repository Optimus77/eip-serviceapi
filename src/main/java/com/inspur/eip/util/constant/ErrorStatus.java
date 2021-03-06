package com.inspur.eip.util.constant;

/**
 * @Description TODO
 * @Author Zerah
 * @Date 2019/5/30 14:59
 **/
public enum ErrorStatus {
    /**
     * SC_PARAM_ERROR
     */
    SC_PARAM_ERROR("106.999400", "sc param error!"),

    SC_PARAM_NOTFOUND("106.998400", "sc param not found!"),

    SC_PARAM_UNKONWERROR("106.997400","sc param unkonwerror!"),

    SC_RESOURCE_ERROR("106.994400","sc resource error!"),
    /**
     * SC_RESOURCE_NOTENOUGH
     */
    SC_RESOURCE_NOTENOUGH("106.993400", "SC_RESOURCE_NOTENOUGH!"),

    /**
     * ENTITY_NOT_FOND_IN_DB
     */
    ENTITY_NOT_FOND_IN_DB("106.994404", "Failed to find entity  in db! -"),

    /**
     * SC_OPENSTACK_FIP_UNAVAILABLE
     */
    SC_OPENSTACK_FIP_UNAVAILABLE("106.101404", "SC_OPENSTACK_FIP_UNAVAILABLE"),


    /**
     * SC_FIREWALL_DNAT_UNAVAILABLE
     */
    SC_FIREWALL_DNAT_UNAVAILABLE("106.202404", "sc firewall dnat unavailable!"),

    /**
     * SC_FIREWALL_SNAT_UNAVAILABLE
     */
    SC_FIREWALL_SNAT_UNAVAILABLE("106.202404", "sc firewall snat unavailable!"),


    /**
     * SC_FIREWALL_QOS_UNAVAILABLE
     */
    SC_FIREWALL_QOS_UNAVAILABLE("106.203404", "sc firewall qos unavailable!"),

    /**
     * SC_FIREWALL_NATPT_UNAVAILABLE
     */
    SC_FIREWALL_NATPT_UNAVAILABLE("106.204404", "sc firewall natpt unavailable!"),

    /**
     * SC_NOT_SUPPORT
     */
    SC_NOT_SUPPORT("106.999405", "SC_NOT_SUPPORT!"),


    /**
     * SC_FIREWALL_NAT_UNAVAILABLE
     */
    SC_FIREWALL_NAT_UNAVAILABLE("106.202404", "SC_FIREWALL_NAT_UNAVAILABLE!"),

    /**
     * SC_FORBIDDEN
     */
    SC_FORBIDDEN("106.001403", "SC_FORBIDDEN!"),


    /**
     * SC_INTERNAL_SERVER_ERROR
     */
    SC_INTERNAL_SERVER_ERROR("106.999500", "SC_INTERNAL_SERVER_ERROR!"),

    /**
     * SC_OPENSTACK_UNAVAILABLE
     */
    SC_OPENSTACK_UNAVAILABLE("106.999503", "SC_OPENSTACK_UNAVAILABLE!"),

    /**
     * SC_FIREWALL_UNAVAILABLE
     */
    SC_FIREWALL_UNAVAILABLE("106.999503", "SC_FIREWALL_UNAVAILABLE!"),

    /**
     * SC_OPENSTACK_SERVER_ERROR
     */
    SC_OPENSTACK_SERVER_ERROR("106.999503", "SC_OPENSTACK_SERVER_ERROR!"),


    /**
     * SC_OPENSTACK_FIPCREATE_ERROR
     */
    SC_OPENSTACK_FIPCREATE_ERROR("106.102503", "SC_OPENSTACK_FIPCREATE_ERROR!"),

    /**
     * SC_FIREWALL_SERVER_ERROR
     */
    SC_FIREWALL_SERVER_ERROR("106.201503", "SC_FIREWALL_SERVER_ERROR!"),
    /**
     * Unauthorized: osClientV3 is null!
     */
    ENTITY_UNAUTHORIZED("106.999401", "Unauthorized: osClientV3 is null!"),

    /**
     * Unauthorized: osClientV3 is null!
     */
    ENTITY_INTERNAL_SERVER_ERROR("106.999500", "System internal server error!"),

    /**
     * ENTITY_BADREQUEST_ERROR
     */
    ENTITY_BADREQUEST_ERROR("106.000400","Bad request error,param not correct!"),

    /**
     * ENTITY_ILLEGAL_ARGUMENT_ERROR
     */
    ENTITY_ILLEGAL_ARGUMENT_ERROR("106.000400","ENTITY_ILLEGAL_ARGUMENT_ERROR!"),
    /**
     *
     */
    NOT_SUPPORT_PRODUCT_LINE_CODE("106.000400","Not support product line code!"),
    /**
     * not support order type
     */
    NOT_SUPPORT_ORDER_TYPE("106.000400","Not support order type!"),
    /**
     * eip in sbw so that can not delete sbw
     */
    EIP_IN_SBW_SO_THAT_CAN_NOT_DELETE("106.000400","EIP in sbw so that sbw cannot be removed!"),
    /**
     * param can not be null
     */
    PARAM_CAN_NOT_BE_NULL("106.000400","param can not be null! -"),

    BILL_TYPE_NOT_CORRECT("106.000400","bill type not correct! -"),
    /**
     * FireWall config error
     */
    FIREWALL_NOT_FOND_IN_DB("106.994400", "Failed to find FireWall config  in db! -"),


    VALIADATE_NAME_ERROR("106.994400", "Valiadte Illegal name! -"),

    /**
     * add qos in firewall
     */
    FIREWALL_ADD_QOS_ERROR("106.994500", "Failed to add qos config  in firewall! -"),
    /**
     * add IP in firewall addressbook
     */
    FIREWALL_DEAL_ADDRESS_BOOK_ERROR("106.994500", "Failed to manage ip config to AddressBook! -"),
    /**
     * 未被承认的 输入
     */
    FIREWALL_UNRECOGNIZED_COMMAND("106.994500", "unrecognized command ,please check the param-"),

//    ----------------------------------------Openapi error message------------------------------------

    EIP_EXCEED_QUOTA("106.001001", "弹性IP数量已超过配额。"),
    EIP_BANDWIDTH_EMPTY("106.001002", "弹性IP带宽为空。"),
    EIP_BANDWIDTH_ERROR("106.001003", "弹性IP带宽超出范围。"),
    EIP_ID_EMPTY("106.001004", "弹性IP ID为空。"),
    INVALID_BILL_TYPE("106.001005", "弹性IP计费方式错误"),
    BSS_CRM_QUOTA_ERROR("106.001006", "查询用户配额失败。"),
    EIP_NOT_FOUND("106.001007", "弹性IP信息为空"),

    SBW_BANDWIDTH_EMPTY("106.001007", "共享带宽，带宽值为空。"),
    SBW_EXCEED_QUOTA("106.001008", "共享带宽数量已超过配额。"),
    SBW_INVALID_BILL_TYPE("106.001009", "共享带宽计费方式错误"),
    SBW_NAME_EMPTY("106.001010","共享带宽名称为空"),
    SBW_ID_EMPTY("106.001011", "共享带宽 ID为空。"),
    CHARGEMODE_WRONG("106.001012","弹性IP计费类型错误"),
    EIP_CAHRGEMODE_WRONG("106.001013","弹性IP收费模式错误"),
    SBW_BANDWIDTH_ERROR("106.001014","共享带宽带宽值错误"),
    SBW_NOT_FOUND("106.001015", "共享带宽信息为空"),
    SBW_DURATION_WROMG("106.001016", "共享带宽续费月份错误"),
    EIP_ALREADY_ADD_SBW("106.001017","弹性IP已经加入共享带宽"),
    EIP_NOT_ADD_SBW("106.001018","弹性IP未加入任何共享带宽");


    private final String code;

    private final String message;

    ErrorStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }


}