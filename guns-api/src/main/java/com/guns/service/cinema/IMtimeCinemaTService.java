package com.guns.service.cinema;


import com.guns.vo.cinema.CinemaVO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 影院信息表 服务类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
//@Service(interfaceClass = IMtimeCinemaTService.class)
public interface IMtimeCinemaTService extends Serializable {

    List getAddressPrice(Integer brandId);

    Object getCine(Integer uuid);

    //tf
    //获取影院列表查询条件
    Map<String, Object> getCinema(Integer brandId, Integer hallType, Integer areaId);


    CinemaVO selectCinemaById(Integer cinemaId);
}
