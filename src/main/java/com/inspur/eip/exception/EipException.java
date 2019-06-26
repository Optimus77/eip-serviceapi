package com.inspur.eip.exception;

public class EipException extends Exception {
    private int value;
    public EipException() {
        super();
    }
    public EipException(String msg, int value) {
        super(msg);
        this.value=value;
    }
    public int getValue() {
        return value;
    }

}
