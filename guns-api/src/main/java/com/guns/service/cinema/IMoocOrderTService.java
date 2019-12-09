package com.guns.service.cinema;


import java.util.Map;

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

    Object getOrder(Integer cinemaId, Integer fieldId, Integer filmId);
    Object getOrders(Integer cinemaId, Integer fieldId, Integer filmId, Integer status);

    Object buyTickets(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    Object getUserOrders(Integer nowPage, Integer pageSize, Integer userId);

    Object getOrderByUuid(String uuid);

    String getImg(String orderId);

    Map getPayRequest(String orderId);

    Integer updataOrderStatus(String orderId);

    Integer updataOrderSeats(String orderId);
}
