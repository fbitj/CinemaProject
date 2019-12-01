package com.guns.vo;

import com.guns.vo.film.ImgVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class Info04 implements Serializable {
    private static final long serialVersionUID = 1L;

    String biography;

    Integer filmId;

    Map<String ,Object> actors;

    ImgVO imgVO;


}
