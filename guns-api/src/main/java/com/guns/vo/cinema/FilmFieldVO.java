package com.guns.vo.cinema;

import lombok.Data;

@Data
public class FilmFieldVO {
    private Integer fieldId;
    private String hallName;
    private String beginTime;
    private String endTime;
    private String language;
    private String price;
}
