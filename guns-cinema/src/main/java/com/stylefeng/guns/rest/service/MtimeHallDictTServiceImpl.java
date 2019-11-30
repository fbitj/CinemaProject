package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.IMtimeHallDictTService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 地域信息表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
@Component
@Service(interfaceClass = IMtimeHallDictTService.class)
public class MtimeHallDictTServiceImpl implements IMtimeHallDictTService {

    @Autowired
    MtimeHallDictTMapper hallDictTMapper;

    @Override
    public Object gethall(Integer id) {
        EntityWrapper<MtimeHallDictT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", id);
        List<MtimeHallDictT> mtimeHallDictTS = hallDictTMapper.selectList(wrapper);
        MtimeHallDictT mtimeHallDictT = mtimeHallDictTS.get(0);
        return mtimeHallDictT;
    }
}
