package com.servustech.eduson.security.constants;

public enum ErrorCodes {
    UNAUTHORIZED(401);

    private int errorCode;

    private ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }
    public int getErrorCode() {
        return errorCode;
    }
}
