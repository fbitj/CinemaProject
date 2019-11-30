package com.stylefeng.guns.rest.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guns.service.cinema.IMoocOrderTService;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-30
 */
@Component
@Service(interfaceClass = IMoocOrderTService.class)
public class MoocOrderTServiceImpl implements IMoocOrderTService, Serializable {

    @Autowired
    MoocOrderTMapper orderTMapper;

    public Object getOrders(Integer cinemaId, Integer fieldId, Integer filmId, Integer status){
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cinema_id",cinemaId);
        map.put("field_id",fieldId);
        map.put("film_id",filmId);
        map.put("order_status",status);
        wrapper.allEq(map);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectByMap(map);
        return moocOrderTS;
    }

}
