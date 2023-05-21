package com.bookback.exception;

public class ConditionException extends RuntimeException {
    public static final long serialVersionUID = 1l;
    private String code;

    public ConditionException(String code, String name) {
        super(name);
        this.code = code;
    }

    public ConditionException(String name) {
        super(name);
        code = "500";
    }

    public String getCode() {
        return code;
    }
}
