package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.guns.vo.ActorVO;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmActorT;

import java.util.List;


/**
 * <p>
 * 影片与演员映射表 Mapper 接口
 * </p>
 * @author liaohao
 * @since 2019-11-28
 */
public interface MtimeFilmActorTMapper extends BaseMapper<MtimeFilmActorT> {
    List<ActorVO> selectActorsByFilmId(Integer filmId);
}
