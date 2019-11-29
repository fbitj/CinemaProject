package com.guns.service.film;


import com.guns.dto.FilmsDTO;
import com.guns.vo.FilmItemVO;
import com.guns.vo.FilmListVO;

/**
 * Created by fwj on 2019-11-27.
 */
public interface FilmService {

    /**
     * 通过条件查询影片列表
     * @param filmsDTO
     * @return
     */
    FilmListVO getFilmsByConditions(FilmsDTO filmsDTO);

    /**
     * 通过影片id查询影片信息详情
     * @param filmId
     * @return
     */
    FilmItemVO getFilmDetail(Integer filmId);

}
