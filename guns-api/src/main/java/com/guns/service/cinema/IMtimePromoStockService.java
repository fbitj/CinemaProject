package com.guns.service.cinema;


import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Cats
 * @since 2019-12-03
 */
public interface IMtimePromoStockService {

    Object getPromoStock(Integer uuid);

    Integer updatePromStockByPromoId(Integer promoId, Integer amount);

    List<HashMap<String, Object>> pushPromoStock();

}
