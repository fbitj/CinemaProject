package com.guns.service.promo;

import com.guns.vo.promo.PromoContainStockVO;

import java.util.List;

public interface PromoService {
    List<PromoContainStockVO> selectPromo(Integer cinemaId, Integer promoId);

    int createOrder(Integer userId, PromoContainStockVO promo, Integer amount);

    int updateStock(Integer promoId, Integer amount);
}
