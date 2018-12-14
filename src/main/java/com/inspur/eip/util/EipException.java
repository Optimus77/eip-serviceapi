package com.inspur.eip.util;

public class EipException extends Exception {
    private int value;
    public EipException() {
        super();
    }
    EipException(String msg,int value) {
        super(msg);
        this.value=value;
    }
    public int getValue() {
        return value;
    }

}
