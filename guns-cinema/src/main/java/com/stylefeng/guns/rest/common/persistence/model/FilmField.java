package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmField  {
    private Integer fieldId;
    private String hallName;
    private String beginTime;
    private String endTime;
    private String language;
    private String price;
}
