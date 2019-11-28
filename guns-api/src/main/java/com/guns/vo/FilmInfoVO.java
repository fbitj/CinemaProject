package com.guns.vo;

import lombok.Data;

@Data
public class FilmInfoVO {

    private String filmId;

    private String imgAddress;

    private String filmName;

    private Integer filmType;

    private Integer expectNum;

    private Integer boxNum;

    private String score;

    private String showTime;

    //private String filmLength;
}
