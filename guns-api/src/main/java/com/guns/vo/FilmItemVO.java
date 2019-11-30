package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmItemVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    Integer status;

    String imgPre;

    FilmItemInfoVO data;

    ImgVO imgVO;

    Integer filmId;
}
