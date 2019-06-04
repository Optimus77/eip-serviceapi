package com.inspur.eip.util;

/**
 * @Description TODO
 * @Author Zerah
 * @Date 2019/5/30 14:59
 **/
public enum ErrorStatus {
    /**
     * SC_PARAM_ERROR
     */
    SC_PARAM_ERROR("106.999400", "SC_PARAM_ERROR!"),

    SC_PARAM_NOTFOUND("106.998400", "SC_PARAM_NOTFOUND!"),

    SC_PARAM_UNKONWERROR("106.997400","SC_PARAM_UNKONWERROR!"),

    SC_RESOURCE_ERROR("106.994400","SC_RESOURCE_ERROR!"),
    /**
     * SC_RESOURCE_NOTENOUGH
     */
    SC_RESOURCE_NOTENOUGH("106.993400", "SC_RESOURCE_NOTENOUGH!"),

    /**
     * EIP_BIND_HAS_BAND
     */
    EIP_BIND_HAS_BAND("106.991400","EIP_BIND_HAS_BAND!"),


    /**
     * ENTITY_NOT_FOND_IN_DB
     */
    ENTITY_NOT_FOND_IN_DB("106.994404", "Failed to find entity  in db!"),

    /**
     * SC_OPENSTACK_FIP_UNAVAILABLE
     */
    SC_OPENSTACK_FIP_UNAVAILABLE("106.101404", "SC_OPENSTACK_FIP_UNAVAILABLE"),


    /**
     * SC_FIREWALL_DNAT_UNAVAILABLE
     */
    SC_FIREWALL_DNAT_UNAVAILABLE("106.202404", "SC_FIREWALL_DNAT_UNAVAILABLE!"),

    /**
     * SC_FIREWALL_SNAT_UNAVAILABLE
     */
    SC_FIREWALL_SNAT_UNAVAILABLE("106.202404", "SC_FIREWALL_SNAT_UNAVAILABLE!"),


    /**
     * SC_FIREWALL_QOS_UNAVAILABLE
     */
    SC_FIREWALL_QOS_UNAVAILABLE("106.203404", "SC_FIREWALL_QOS_UNAVAILABLE!"),

    /**
     * SC_FIREWALL_NATPT_UNAVAILABLE
     */
    SC_FIREWALL_NATPT_UNAVAILABLE("106.204404", "SC_FIREWALL_NATPT_UNAVAILABLE!"),

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
    ENTITY_INTERNAL_SERVER_ERROR("106.999500", "ENTITY_INTERNAL_SERVER_ERROR!"),

    /**
     * ENTITY_BADREQUEST_ERROR
     */
    ENTITY_BADREQUEST_ERROR("106.000400","ENTITY_BADREQUEST_ERROR"),

    /**
     * ENTITY_ILLEGAL_ARGUMENT_ERROR
     */
    ENTITY_ILLEGAL_ARGUMENT_ERROR("106.000400","ENTITY_ILLEGAL_ARGUMENT_ERROR!"),
    /**
     *
     */
    NOT_SUPPORT_PRODUCT_LINE_CODE("106.000400","not_support_product_line_code!"),
    /**
     * not support order type
     */
    NOT_SUPPORT_ORDER_TYPE("106.000400","Not_support_order_type!");

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