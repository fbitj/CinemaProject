package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 放映场次表 Mapper 接口
 * </p>
 *
 * @author nathan
 * @since 2019-12-01
 */
public interface MtimeFieldTMapper extends BaseMapper<MtimeFieldT> {

    String querySeatAddressByFieldId(Integer fieldId);
}
