package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.guns.vo.film.FilmInfoVO;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmT;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 * @author fwj
 * @since 2019-11-27
 */
public interface MtimeFilmTMapper extends BaseMapper<MtimeFilmT> {

    List<FilmInfoVO> filmInfoVOB(@Param("film_status") Integer film_status,
                                 @Param("film_preSaleNum") Integer film_preSaleNum,
                                 @Param("film_cats") Integer film_cats,
                                 @Param("film_area") Integer film_area,
                                 @Param("film_date") Integer film_date);
}
