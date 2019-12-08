package com.stylefeng.guns.rest.common.exception;

public class TokenException extends RuntimeException {
    public TokenException() {
    }

    public TokenException(String message) {
        super(message);
    }
}
