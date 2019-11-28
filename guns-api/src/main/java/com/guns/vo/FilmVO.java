package com.guns.vo;

import lombok.Data;

import java.util.List;

@Data
public class FilmVO {

    private Integer filmNum;

    private List<FilmInfoVO> filmInfo;
}
