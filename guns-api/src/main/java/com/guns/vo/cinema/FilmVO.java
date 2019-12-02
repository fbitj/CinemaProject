package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmVO implements Serializable {
    private String actors;
    private String filmCats;
    private Integer filmId;
    private String filmLength;
    private String filmName;
    private String filmType;
    private String imgAddress;
    private List<FilmFieldVO> filmFields;
}
