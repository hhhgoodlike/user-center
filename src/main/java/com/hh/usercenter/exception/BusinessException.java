package com.hh.usercenter.exception;

import com.hh.usercenter.common.ErrorCode;

public class BusinessException extends RuntimeException{
    private final int code;
    private final String description;

    public BusinessException(String s, int code, String description) {
        super(s);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();

    }

    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;

    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
