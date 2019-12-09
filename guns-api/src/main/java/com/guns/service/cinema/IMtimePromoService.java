package com.guns.service.cinema;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Cats
 * @since 2019-12-03
 */

public interface IMtimePromoService  {

    Object getPromos();

    Object getPromo(Integer promo_id);

    Object getPromosByStatus();

    //分布式事务     扣减库存
    Boolean saveOrderPromoTransactional(Integer promoId, Integer amount, Integer uuid, String stockId);

    //获取秒杀令牌token
    String generateToken(Integer promoId, Integer userId);

}
