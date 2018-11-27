package com.inspur.eip.util;


import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * A response that is returned when an Action is performed against the server.
 * If {@link #isSuccess()} is true then the fault will always be null.
 * The fault will indicate why the action has failed.  Any other transport/communication
 * errors will be thrown as with any other API calls.
 *
 * @author Jeremy Unruh
 */
public class ReturnResult implements Serializable  {
    private static final long serialVersionUID = 1L;

    private String message;
    private int code;


    private ReturnResult(String message, int code) {
        this.code = code;
        this.message = message;
    }



    public static ReturnResult actionSuccess(String message) {
        return new ReturnResult(message,200);
    }

    public static ReturnResult actionFailed(String message, int code) {
        return new ReturnResult(message, code);
    }

    public static ReturnResult actionResult(String message, int code) {
        return new ReturnResult(message, code);
    }

    /**
     * Returns the underlying error code (status code)
     *
     * @return the error code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return true if the action was successful
     */
    public boolean isSuccess() {
        return message == null;
    }

    /**
     * @return the fault if the action was unsuccessful otherwise null
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).
                omitNullValues().
                add("success", message == null).
                add("fault", message).
                add("code", code).
                toString();
    }
}


