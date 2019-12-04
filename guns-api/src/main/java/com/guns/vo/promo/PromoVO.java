package com.guns.vo.promo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PromoVO implements Serializable {

    private static final long serialVersionUID = -4985426181967983912L;
    private String cinemaAddress;

    private Integer cinemaId;

    private String cinemaName;

    private String description;

    private String endTime;

    private String imgAddress;

    //类型待定
    private BigDecimal price;

    private String startTime;

    private Integer status;

    private Integer stock;

    private Integer uuid;
}
