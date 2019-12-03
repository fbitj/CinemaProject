package com.stylefeng.guns.rest.bean;

public enum OrderStatus {

    ORDER_NOPAY(0, "待支付"),

    ORDER_ISPAY(1, "已支付"),

    ORDER_ISCLOSE(2, "已关闭");


    private Integer code;

    private String message;

    OrderStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    OrderStatus() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
