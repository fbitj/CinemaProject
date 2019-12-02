package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

@Data

//public class CinemaInfo implements Serializable {

public class CinemaInfoVO implements Serializable {

    private String cinemaAdress;
    private Integer cinemaId;
    private String cinemaName;
    private String cinemaPhone;
    private String imgUrl;


}
