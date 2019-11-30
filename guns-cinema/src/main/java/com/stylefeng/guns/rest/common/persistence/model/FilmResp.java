package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmResp{
    private String actors;
    private String filmCats;
    private Integer filmId;
    private String filmLength;
    private String filmName;
    private String filmType;
    private String imgAddress;
    private List<FilmField> filmFields;
}
