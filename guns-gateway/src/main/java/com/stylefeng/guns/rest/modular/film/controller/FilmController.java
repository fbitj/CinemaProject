package com.stylefeng.guns.rest.modular.film.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.dto.FilmsDTO;
import com.guns.service.film.FilmService;
import com.guns.vo.FilmItemVO;
import com.guns.vo.FilmListVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("film")
public class FilmController {

    @Reference(interfaceClass = FilmService.class, check = false)
    private FilmService filmService;

    @RequestMapping("getFilms")
    public FilmListVO getFilmsByConditions(FilmsDTO filmsDTo){
        return filmService.getFilmsByConditions(filmsDTo);
    }

    @RequestMapping("films/{filmId}")
    public FilmItemVO getFilmDetail(@PathVariable("filmId") Integer filmId) {
        return filmService.getFilmDetail(filmId);
    }
}
