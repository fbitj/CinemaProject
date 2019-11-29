package com.guns.vo.film;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmInfoVO implements Serializable {

    private static final long serialVersionUID = 1145219176055399820L;
    private String filmId;

    private String imgAddress;

    private String filmName;

    private Integer filmType;

    private Integer expectNum;

    private Integer boxNum;

    private String score;

    private String filmScore;

    private String showTime;

    //private String filmLength;
}
