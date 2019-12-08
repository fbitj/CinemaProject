package com.stylefeng.guns.rest.common.persistence.dao;

import com.guns.vo.order.OrdersInfoVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author liaohao
 * @since 2019-12-01
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    int updateOrderStatusById(String orderId);

    List<OrdersInfoVO> selectUserOrderFriendly(int userId);
}
