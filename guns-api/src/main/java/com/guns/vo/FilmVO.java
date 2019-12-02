package com.guns.vo;

import com.guns.vo.film.FilmInfoVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer filmNum;

    private List<FilmInfoVO> filmInfo;
}
