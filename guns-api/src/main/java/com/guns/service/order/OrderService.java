package com.guns.service.order;

import com.guns.vo.BaseRespVO;
import com.guns.vo.order.OrderVO;
import com.guns.vo.order.OrdersInfoVO;

import java.util.List;

public interface OrderService {
    /**
     * 验证作为编号是否为真
     * @param filedId 场次信息
     * @param seatIds 购买的所有座位编号
     * @return
     */
    Boolean isTrueSeats(Integer filedId, String seatIds);

    /**
     * 验证作为是否被售出，
     * @param filedId
     * @param seatId
     * @return
     */
    Boolean isSoldSeats(Integer filedId, String seatId);

    /**
     * 生成订单信息
     * @param filedId 场次信息
     * @param soldSeats 出售的座位编号
     * @param seatsName 出售的作为名称
     * @param userId 购买的用户id
     * @return
     */
    OrderVO saveOrderInfo(Integer filedId, String soldSeats, String seatsName, Integer userId);

    /**
     * 生成支付二维码
     * @param orderId
     * @return
     */
    BaseRespVO getPayCode(String orderId);

    /**
     * 获取订单支付结果
     * @param orderId
     * @param tryNums
     * @return
     */
    BaseRespVO getPayResult(String orderId,Integer tryNums);

    /**
     * 分页查询用户订单信息
     * @param nowPage 当前页
     * @param pageSize 每页数量
     * @param userId 用户id
     * @return
     */
    BaseRespVO<List<OrdersInfoVO>> getOrderInfoByPage(Integer nowPage, Integer pageSize, int userId);
}
