package com.stylefeng.guns.rest.common.exception;

public class OrderException extends RuntimeException {
    public OrderException() {
    }

    public OrderException(String message) {
        super(message);
    }
}
