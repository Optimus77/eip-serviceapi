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
    public static final String EXCEPTION_EIP_SOFTDOWN_OR_DELETE = "Exception in  soft down or delete Eip Entity";


    /**
     * param not correct
     */
    public static final String PARSE_JSON_PARAM_ERROR = "parse_message_param_error! param : %s";
    public static final String PARSE_JSON_IO_ERROR = "parse message io error! param : %s";
    public static final String BILL_TYPE_NOT_SUPPORT = "bill type not support,must be [monthly |hourlySettlement]";

    public static final String CREAT_EIP_CONFIG_FAILED = "Create eip config result Failed";
    public static final String CREAT_EIP_CONFIG_SUCCESS = "Create eip config result Success";
    public static final String DELETE_EIP_CONFIG_FAILED = "Delete eip config result Failed";
    public static final String DELETE_EIP_CONFIG_SUCCESS = "Delete eip config result Success";
    public static final String UPDATE_EIP_CONFIG_FAILED = "Update eip config result Failed";
    public static final String UPDATE_EIP_CONFIG_SUCCESS = "Update eip config result Success";
    public static final String SOFTDOWN_OR_DELETE_EIP_CONFIG_RESULT = "Softdown or delete eip config result Result";


    /**
     * SBW
     */
    public static final String CREAT_SBW_CONFIG_FAILED = "Create sbw config result Failed:{}";
    public static final String CREAT_SBW_CONFIG_SUCCESS = "Create sbw config result Success:{}";
    public static final String DELETE_SBW_CONFIG_FAILED = "Delete sbw config result Failed:{}";
    public static final String DELETE_SBW_CONFIG_SUCCESS = "Delete sbw config result Success:{}";
    public static final String UPDATE_SBW_CONFIG_FAILED = "Update sbw config result Failed:{}";
    public static final String UPDATE_SBW_CONFIG_SUCCESS = "Update sbw config result Success:{}";
    public static final String SOFTDOWN_OR_DELETE_SBW_CONFIG_RESULT = "Soft down or delete sbw config result Result:{}";



    public static final String EXCEPTION_SBW_CREATE = " Exception in Create Sbw Entity:{}";
    public static final String EXCEPTION_SBW_DELETE = "Exception in  Delete Sbw Entity:{}";
    public static final String EXCEPTION_SBW_UPDATE = "Exception in  Update Sbw Entity:{}";
    public static final String EXCEPTION_SBW_RENAEM= "Exception in  soft down or delete Sbw Entity:{}";
    public static final String EXCEPTION_SBW_SOFTDOWN_OR_DELETE = "Exception in  soft down or delete Sbw Entity:{}";


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
