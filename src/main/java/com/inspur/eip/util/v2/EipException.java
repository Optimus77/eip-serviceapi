package com.inspur.eip.util.v2;

class EipException extends Exception {
    private int value;
    public EipException() {
        super();
    }
    EipException(String msg, int value) {
        super(msg);
        this.value=value;
    }
    public int getValue() {
        return value;
    }

}
