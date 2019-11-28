package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.guns.service.film.FilmService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by fwj on 2019-11-27.
 */
@Component
@Service(interfaceClass = FilmService.class)
public class FilmServiceImpl implements FilmService{
    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;

}
