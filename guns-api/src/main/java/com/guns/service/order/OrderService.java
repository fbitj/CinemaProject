package com.guns.service.order;

import com.guns.bo.FieldBO;
import com.guns.bo.OrderBO;
import com.guns.vo.OrderVO;

import java.io.IOException;
import java.util.List;

public interface OrderService {
    boolean verifySeat(Integer fieldId, String[] soldSeats) throws IOException;

    boolean verifyOrder(Integer fieldId, String[] soldSeats);

    OrderVO addOrder(OrderBO orderBO, FieldBO fieldBO);


    FieldBO selectFieldById(Integer fieldId);

    List selectOrderByUserId(Integer userId, Integer nowPage, Integer pageSize);

    OrderBO selectOrderByOrderId(String orderId);

    int changeOrderStatus(int orderStatus, String orderId);
}
