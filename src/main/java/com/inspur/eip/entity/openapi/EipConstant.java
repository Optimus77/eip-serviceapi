package com.inspur.eip.entity.openapi;

public class EipConstant {


    private EipConstant() {
    }

    public static final String ACTION_CREATE_EIP = "CreateEip";
    public static final String ACTION_CREATE_EIP_ADD_SBW = "createEipAddSbw";
    public static final String ACTION_DELETE_EIP = "DeleteEip";
    public static final String ACTION_BATCH_DELETE_EIP = "BatchDeleteEip";
    public static final String ACTION_RENEW_EIP = "RenewEip";
    public static final String ACTION_UPDATE_BIND_WIDTH = "UpdateBindWidth";

    public static final String ACTION_CREATE_SBW = "CreateSbw";
    public static final String ACTION_DELETE_SBW = "DeleteSbw";
    public static final String ACTION_EIP_REMOVE_SBW = "EipRemoveSbw";
    public static final String ACTION_UPDATE_SBW_BINDWIDTH = "UpdateSbwBindWidth";
    public static final String ACTION_RENEW_SBW = "renewSbw";

    public static final String PRODUCT_LINE_CODE = "EIP";
    public static final String PRODUCT_TYPE_CODE = "EIP";
    public static final String ORDER_ROUTE = "EIP";
    public static final String ORDER_ROUTE_IPTS = "IPTS";
    public static final String BILLTYPE_MONTHLY = "monthly";
    public static final String BILLTYPE_HOURLYSETTLEMENT = "hourlySettlement";
    public static final String ORDER_WHAT_FORMAL = "formal";
    public static final String ORDER_SOURCE_OPENAPI = "openApi";
    public static final String ORDER_TYPE_UNSUNSCRIBE = "unsubscribe";
    public static final String ORDER_TYPE_CHANGE_CONFIG = "changeConfigure";
    public static final String ORDER_TYPE_NEW = "new";
    public static final String ORDER_TYPE_RENEW = "renew";
    public static final String ORDER_DURATION_UNIT_MONTHLY = "M";

    public static final String PRODUCTLINE_CODE = "SBW";
    public static final String PRODUCTTYPE_CODE = "SBW";
    public static final String ORDER_SBW_ROUTE = "SBW";
    public static final String ACTION_EIP_ADD_SBW = "EipAddSbw";
    public static final String CHARGEMODE_SHARE_BANDWIDTH = "SharedBandwidth";
    public static final String CHARGEMODE_BANDWIDTH = "Bandwidth";

    public static final String ACTION_CREATE_EIPV6 = "CreateIptsBindEip";

}
