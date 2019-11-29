package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.film.FilmService;
import com.guns.vo.film.FilmInfoVO;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fwj on 2019-11-27.
 */
@Component
@Service(interfaceClass = FilmService.class)
public class FilmServiceImpl implements FilmService{
    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;

    /**
     * 根据影片状态查找影片
     * @param status
     * @return
     */
    @Override
    public List<FilmInfoVO> queryFilmByStatus(int status) {
        EntityWrapper<MtimeFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", status);
        List<MtimeFilmT> mtimeFilmTList = mtimeFilmTMapper.selectList(wrapper);

        //封装
        List<FilmInfoVO> filmInfoVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mtimeFilmTList)){
            for (MtimeFilmT mtimeFilmT : mtimeFilmTList) {
                FilmInfoVO filmInfoVO = new FilmInfoVO();
                BeanUtils.copyProperties(mtimeFilmT,filmInfoVO);
                filmInfoVO.setFilmId(mtimeFilmT.getUuid().toString());
                filmInfoVOS.add(filmInfoVO);
            }
        }
        return filmInfoVOS;
    }

    /**
     * 查询所有影片按相关参数排序
     * @param column
     * @return
     */
    @Override
    public List<FilmInfoVO> queryFilmByColumnDesc(String column) {
        EntityWrapper<MtimeFilmT> wrapper = new EntityWrapper<>();
        wrapper.orderBy(column, false);

        List<MtimeFilmT> mtimeFilmTS = mtimeFilmTMapper.selectList(wrapper);

        List<FilmInfoVO> filmInfoVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mtimeFilmTS)) {
            for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
                FilmInfoVO filmInfoVO = new FilmInfoVO();
                BeanUtils.copyProperties(mtimeFilmT,filmInfoVO);
                filmInfoVO.setFilmId(mtimeFilmT.getUuid().toString());
                filmInfoVO.setBoxNum(mtimeFilmT.getFilmBoxOffice());
                filmInfoVO.setExpectNum(mtimeFilmT.getFilmPresalenum());
                filmInfoVO.setScore(mtimeFilmT.getFilmScore());
                filmInfoVOS.add(filmInfoVO);
            }
        }
        return filmInfoVOS;
    }
}
