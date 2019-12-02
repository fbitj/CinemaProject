package com.stylefeng.guns.rest.modular.order.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BuyTicketRequest implements Serializable {

    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * 场次编号
     */
    private Integer fieldId;

    /**
     * 购买的座位编号
     */
    private String soldSeats;

    /**
     * 购买的座位的名称
     */
    private String seatsName;
}
