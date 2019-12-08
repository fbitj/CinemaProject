package com.guns.vo.promo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀界面响应数据
 */
@Data
public class CinemaPromoInfo implements Serializable {

    private static final long serialVersionUID = -6849794474667710L;

    private String cinemaAddress;

    private Integer cinemaId;

    private String cinemaName;

    private String description;

    private Date endTime;

    private String imgAddress;

    private Integer price;

    private Date startTime;

    private Integer status;

    private Integer stock;

    private Integer uuid;
}
