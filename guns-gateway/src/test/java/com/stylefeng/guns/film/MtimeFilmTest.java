package com.stylefeng.guns.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.film.FilmService;
import org.junit.Test;

public class MtimeFilmTest {
    @Reference(interfaceClass = FilmService.class)
    private FilmService filmService;
    @Test
    public void test(){

        /*FilmController filmController = new FilmController();
        filmController.getFilmsByConditions(filmsBO);*/
    }
}
