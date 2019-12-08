package com.stylefeng.guns.rest.common.persistence;

public enum StockLogStatus {
    INIT(1,"初始化"),SUCCESS(2,"成功"),FAILED(3,"失败");

    private int status;
    private String description;

    StockLogStatus(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
