package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author liaohao
 * @since 2019-12-05
 */
public interface MtimePromoStockMapper extends BaseMapper<MtimePromoStock> {

    /**
     * 减少指定id的库存
     *
     * @param promoId
     * @param amount
     * @return
     */
    int decreaseStockByPromoId(@Param("promoId") String promoId, @Param("amount") Integer amount);
}
