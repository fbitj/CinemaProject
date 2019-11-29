package com.guns.vo.film;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmResultVO implements Serializable {

    private static final long serialVersionUID = 6336733848607716129L;
    private Integer filmNum;

    private List filmInfo;

    public FilmResultVO(Integer filmNum, List filmInfl) {
        this.filmNum = filmNum;
        this.filmInfo = filmInfl;
    }

    public FilmResultVO() {
    }
}
