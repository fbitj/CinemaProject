package com.stylefeng.guns.rest.common.persistence.dao;

import com.guns.vo.promo.CinemaPromoInfo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liaohao
 * @since 2019-12-05
 */
public interface MtimePromoMapper extends BaseMapper<MtimePromo> {

    List<CinemaPromoInfo> selectCinemasPromoInfo(@Param(value = "cinemaId") String cinemaId, @Param(value = "promoStatus")Integer status);

}
