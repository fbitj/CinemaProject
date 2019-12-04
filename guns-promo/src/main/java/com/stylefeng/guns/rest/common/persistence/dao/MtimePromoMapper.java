package com.stylefeng.guns.rest.common.persistence.dao;

import com.guns.vo.promo.PromoContainStockVO;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author nathan
 * @since 2019-12-03
 */
public interface MtimePromoMapper extends BaseMapper<MtimePromo> {

    List<PromoContainStockVO> selectPromo(@Param("cinemaId") Integer cinemaId,
                                          @Param("promoId") Integer promoId);
}
