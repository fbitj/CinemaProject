package com.guns.service.cinema;



/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Cats
 * @since 2019-12-03
 */
public interface IMtimeStockLogService {

    Integer updataStockLogById(String stockLogId, Integer status);

    String getStockLogId(Integer promoId, Integer amount);

    Object getStockLogById(String stockId);
}
