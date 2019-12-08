package com.guns.service.promo;

import com.guns.vo.BaseRespVO;
import com.guns.vo.promo.CinemaPromoInfo;
import com.guns.vo.promo.SeckillVO;

import java.util.List;

public interface PromoService {

    /**
     * 查询秒杀活动
     * @param cinemaId：null表示查询所有，否则查询指定影院的活动
     * @return
     */
    SeckillVO<List<CinemaPromoInfo>> getPromos(String cinemaId);


    /**
     * 秒杀下单
     * @param promoId 活动id
     * @param amount 购买数量，不能超过5
     * @return
     */
//    BaseRespVO createOrder(String promoId,Integer amount,Integer userId);

    /**
     * 将数据库的秒杀库存刷新到缓存
     * @return
     */
    BaseRespVO publishPromoStock();


    /**
     * 生成秒杀口令
     * @param promoId
     * @return
     */
    BaseRespVO generateToken(String promoId, Integer userId);

    /**
     * 查询商品是否在活动中
     * @param promoId
     * @return
     */
    Boolean isActivating(String promoId);


    /**
     * 初始化一条流水信息
     * @param promoId 活动id
     * @param amount 购买数量
     * @return
     */
    String initPromoStockLog(String promoId, Integer amount);

    /**
     * 使用分布式消息事务创建秒杀订单
     * @param promoId 商品id
     * @param amount 购买数量
     * @param userId 用户id
     * @param stockLogId 库存日志id
     * @return
     */
    Boolean savePromoOrderInTransaction(String promoId, Integer amount, Integer userId, String stockLogId);


    /**
     * 创建订单，修改订单流水状态
     * @param promoId
     * @param amount
     * @param userId
     * @param stockLogId
     * @return
     */
    Boolean savePromoOrderVO(String promoId, Integer amount, Integer userId, String stockLogId);

}
