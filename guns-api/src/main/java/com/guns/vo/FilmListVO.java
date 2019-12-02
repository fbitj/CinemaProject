package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmListVO<T> extends BaseRespVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer nowPage;

    private Integer totalPage;

}
