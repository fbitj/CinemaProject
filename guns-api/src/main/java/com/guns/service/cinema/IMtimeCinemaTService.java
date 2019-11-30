package com.guns.service.cinema;


import java.io.Serializable;
import java.util.List;

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

}
