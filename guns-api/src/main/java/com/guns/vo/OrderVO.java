package com.guns.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = -6849794470754667710L;

    private String orderId;

    private String filmName;

    private String fieldTime;

    private String cinemaName;

    private String seatsName;

    private Double orderPrice;

    private Date orderTimestamp;

    private Integer orderStatus;
}
