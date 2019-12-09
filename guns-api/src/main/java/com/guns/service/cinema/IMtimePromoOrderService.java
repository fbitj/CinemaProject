package com.guns.service.cinema;



/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Cats
 * @since 2019-12-03
 */
public interface IMtimePromoOrderService  {

    Integer insertIntoPromOrder(Integer userId, Integer promoId, Integer amount, String stockId);


}
