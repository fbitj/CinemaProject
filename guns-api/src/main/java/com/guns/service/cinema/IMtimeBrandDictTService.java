package com.guns.service.cinema;


import com.alibaba.dubbo.config.annotation.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 品牌信息表 服务类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
@Service(interfaceClass = IMtimeBrandDictTService.class)
public interface IMtimeBrandDictTService extends Serializable {

    //List getCinemaNames(Integer bid);

    List getCinemas(Integer brandId, Integer districtId, Integer hallType);


}
