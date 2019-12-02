package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmFieldVO implements Serializable {
    private Integer fieldId;
    private String hallName;
    private String beginTime;
    private String endTime;
    private String language;
    private String price;
}
