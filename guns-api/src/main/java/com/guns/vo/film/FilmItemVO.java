package com.guns.vo.film;

import com.guns.vo.BaseRespVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class FilmItemVO<T> extends BaseRespVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer nowPage;

    private Integer totalPage;
}
