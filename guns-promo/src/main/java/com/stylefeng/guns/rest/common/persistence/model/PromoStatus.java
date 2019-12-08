package com.stylefeng.guns.rest.common.persistence.model;


public enum PromoStatus {
    PROMO_NO_START(0,"未开始"), PROMO_STARTING(1,"进行中"),PROMO_END(2,"活动结束");

    private int status;
    private String description;

    PromoStatus(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
