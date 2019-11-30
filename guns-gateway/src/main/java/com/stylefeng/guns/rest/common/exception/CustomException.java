package com.stylefeng.guns.rest.common.exception;

import lombok.Data;

/**
 * @author zhu rui
 * @version 1.0
 * @date 2019/11/29 16:56
 */

public class CustomException extends Exception{
    private int status;
    private String message;

    public CustomException(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
