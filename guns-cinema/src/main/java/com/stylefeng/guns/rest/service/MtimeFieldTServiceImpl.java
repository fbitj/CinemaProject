package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.*;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 放映场次表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
@Component
@Service(interfaceClass = IMtimeFieldTService.class)
public class MtimeFieldTServiceImpl implements IMtimeFieldTService {

    @Autowired
    MtimeFieldTMapper fieldTMapper;
    @Autowired
    IMtimeHallFilmInfoTService infoTService;
    @Autowired
    IMtimeCinemaTService cinemaTService;
    @Autowired
    IMtimeHallDictTService hallDictTService;
    @Reference(interfaceClass = IMoocOrderTService.class, check = false)
    IMoocOrderTService orderTService;

    @Override
    public Object getFieldMessage(Integer cinemaId, Integer fieldId, Integer uuid) {
        HashMap<Object, Object> map = new HashMap<>();
        EntityWrapper<MtimeFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", fieldId);
        List<MtimeFieldT> mtimeFieldTS = fieldTMapper.selectList(wrapper);
        MtimeFieldT mtimeFieldT = mtimeFieldTS.get(0);

        List hallFilm = (List) infoTService.getHallFilm(mtimeFieldT.getFilmId());
        MtimeHallFilmInfoT o = (MtimeHallFilmInfoT) hallFilm.get(0);
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("filmId",o.getFilmId());
        map1.put("filmName",o.getFilmName());
        map1.put("imgAddress",o.getImgAddress());
        map1.put("filmCats",o.getFilmCats());
        map1.put("filmLength",Integer.parseInt(o.getFilmLength().trim()));
        map.put("filmInfo",map1);

        EntityWrapper<MtimeCinemaT> wrapper1 = new EntityWrapper<>();
        MtimeCinemaT cine = (MtimeCinemaT) cinemaTService.getCine(mtimeFieldT.getCinemaId());
        HashMap<Object, Object> map2 = new HashMap<>();
        map2.put("cinemaId",cine.getUuid());
        map2.put("imgUrl",cine.getImgAddress());
        map2.put("cinemaName",cine.getCinemaName());
        map2.put("cinemaAdress",cine.getCinemaAddress());
        map2.put("cinemaPhone",cine.getCinemaPhone());
        map.put("cinemaInfo",map2);

        HashMap<Object, Object> map3 = new HashMap<>();
        map3.put("hallFieldId",mtimeFieldT.getHallId());
        map3.put("hallName",mtimeFieldT.getHallName());
        map3.put("price",mtimeFieldT.getPrice());
        MtimeHallDictT gethall = (MtimeHallDictT) hallDictTService.gethall(mtimeFieldT.getHallId());
        map3.put("seatFile",gethall.getSeatAddress());
        List<MoocOrderT> orders = (List<MoocOrderT>) orderTService.getOrders(cinemaId, fieldId, o.getFilmId(), 1);
        if(orders.size() == 0){
            map3.put("soldSeats","");
        }else{
            StringBuffer sb = new StringBuffer();
            for (MoocOrderT order : orders) {
                sb.append(order.getSeatsIds()).append(",");
            }
            map3.put("soldSeats",sb.toString().substring(0,sb.toString().length() - 1));
        }
//        map3.put("soldSeats","");//已购票座位,  完成订单后实现
        map.put("hallInfo",map3);

        return map;
    }
}
