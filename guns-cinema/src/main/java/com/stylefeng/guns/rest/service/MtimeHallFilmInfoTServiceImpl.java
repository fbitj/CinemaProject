package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.IMtimeHallFilmInfoTService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallFilmInfoTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallFilmInfoT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 影厅电影信息表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
@Component
@Service(interfaceClass = IMtimeHallFilmInfoTService.class)
public class MtimeHallFilmInfoTServiceImpl implements IMtimeHallFilmInfoTService {

    @Autowired
    MtimeHallFilmInfoTMapper infoTMapper;

    @Override
    public Object getHallFilm(Integer filmId) {
        EntityWrapper<MtimeHallFilmInfoT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_id", filmId);
        List<MtimeHallFilmInfoT> mtimeHallFilmInfoTS = infoTMapper.selectList(wrapper);
        return mtimeHallFilmInfoTS;
    }
}
