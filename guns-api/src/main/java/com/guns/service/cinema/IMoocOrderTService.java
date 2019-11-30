package com.guns.service.cinema;


/**
 * <p>
 * 订单信息表 服务类
 * </p>
 *
 * @author Cats
 * @since 2019-11-30
 */
public interface IMoocOrderTService {

    Object getOrders(Integer cinemaId, Integer fieldId, Integer filmId, Integer status);
}
