package com.guns.service.cinema;


import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 放映场次表 服务类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
//@Service(interfaceClass = IMtimeFieldTService.class)
public interface IMtimeFieldTService extends Serializable {


    Object getFieldMessage(Integer cinemaId, Integer fieldId, Integer uuid);

    //tf
    //获取播放场次接口
    Map<String, Object> selectField(Integer cinemaId);

}
