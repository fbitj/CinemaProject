package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 9130742351118892388L;
    private String orderId;

    private String filmName;

    private String fieldTime;

    private String cinemaName;

    private String seatsName;

    private String orderPrice;

    private String orderTimestamp;

    //订单详情新增
    private String orderStatus;

    private Integer filmId;

    private Integer cinemaId;
}
