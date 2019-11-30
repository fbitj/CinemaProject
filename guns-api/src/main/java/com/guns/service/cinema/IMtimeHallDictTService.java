package com.guns.service.cinema;


import java.io.Serializable;

/**
 * <p>
 * 地域信息表 服务类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
//@Service(interfaceClass = IMtimeHallDictTService.class)
public interface IMtimeHallDictTService extends Serializable {

    Object gethall(Integer id);
}
