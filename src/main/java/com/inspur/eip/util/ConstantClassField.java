package com.inspur.eip.util;

/**
 * @Description TODO
 * @Author Zerah
 * @Date 2019/5/30 14:50
 **/
public final class ConstantClassField {

    private ConstantClassField() {
        throw new IllegalStateException("VersionConstant class");
    }

    /**
     * EIP
     */
    public static final String EXCEPTION_EIP_CREATE = " Exception in Create Eip Entity";
    public static final String EXCEPTION_EIP_DELETE = "Exception in  DeleteEip Entity";
    public static final String EXCEPTION_EIP_UPDATE = "Exception in  Update Eip Entity";
    public static final String EXCEPTION_EIP_SOFTDOWN_OR_DELETE = "Exception in  softdown or delete Eip Entity";
    public static final String Exception_EIPS_SHOW = "List Eip Entitys";
    public static final String Exception_EIP_SHOW_DETAIL = "Describe Eip Detatil";


    /**
     * param not correct
     */
    public static final String PARSE_JSON_PARAM_ERROR = "parse_message_param_error! param : %s";
    public static final String PARSE_JSON_IO_ERROR = "parse_message_io_error! param : %s";
    public static final String BILL_TYPE_NOT_SUPPORT = "bill type not support,must be [monthly |hourlySettlement]";

    public static final String CREAT_EIP_CONFIG_RESULT = "create eip config result : %s";
    public static final String DELETE_EIP_CONFIG_RESULT = "delete eip config result : %s";
    public static final String UPDATE_EIP_CONFIG_RESULT = "update eip config result : %s";
    public static final String SOFTDOWN_OR_DELETE_EIP_CONFIG_RESULT = "softdown or delete eip config result : %s";


    /**
     * SBW
     */
    public static final String CREAT_SBW_CONFIG_RESULT = "create sbw config result : %s";
    public static final String DELETE_SBW_CONFIG_RESULT = "delete sbw config result : %s";
    public static final String UPDATE_SBW_CONFIG_RESULT = "update sbw config result : %s";
    public static final String SOFTDOWN_OR_DELETE_SBW_CONFIG_RESULT = "soft down or delete sbw config result : %s";
    public static final String FAILED_TO_DELETE_SBW = "Failed to delete SBW";
    public static final String EXCEPTION_SBW_CREATE = " Exception in Create SBW Entity";
    public static final String EXCEPTION_SBW_DELETE = "Exception in  Delete SBW Entity";
    public static final String EXCEPTION_SBW_UPDATE = "Exception in  Update SBW Entity";
    public static final String EXCEPTION_SBW_SOFTDOWN_OR_DELETE = "Exception in  soft down or delete SBW Entity";


    /**
     * order
     */
    public static final String ORDER_NOT_PAYED = "this order not payed :{}";
    public static final String ORDER_STATUS_NOT_CORRECT = "this order status not support :{}";
    public static final String ORDER_TYPE_NOT_SUPPORT = "this order type not support :{}";

    /**
     * operation
     */
    public static final String OPERATION_RESULT_NOT_OK = "operation result not ok";





}
