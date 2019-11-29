package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

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
