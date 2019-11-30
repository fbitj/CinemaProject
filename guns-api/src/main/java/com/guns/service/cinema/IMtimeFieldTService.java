package com.guns.service.cinema;


import java.io.Serializable;

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

    Object getFieldMessage(Integer cinemaId, Integer fieldId);
}
