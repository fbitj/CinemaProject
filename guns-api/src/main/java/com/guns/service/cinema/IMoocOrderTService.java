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

    Boolean isTrueSeats(Integer fieldId, String soldSeats);

    Boolean isSoldSeats(Integer fieldId, String soldSeats);

    Object getOrders(Integer cinemaId, Integer fieldId, Integer filmId, Integer status);

    Object buyTickets(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    Object getUserOrders(Integer nowPage, Integer pageSize, Integer userId);
}
