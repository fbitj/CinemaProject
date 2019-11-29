package com.guns.service.film;


import com.guns.vo.film.FilmInfoVO;

import java.util.List;

/**
 * Created by fwj on 2019-11-27.
 */
public interface FilmService {

    List<FilmInfoVO> queryFilmByStatus(int status);

    List<FilmInfoVO> queryFilmByColumnDesc(String column);
}
