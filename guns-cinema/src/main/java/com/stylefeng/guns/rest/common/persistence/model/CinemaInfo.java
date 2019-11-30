package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaInfo implements Serializable {
    private String cinemaAdress;
    private Integer cinemaId;
    private String cinemaName;
    private String cinemaPhone;
    private String imgUrl;


}
