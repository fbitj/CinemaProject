package com.guns.service.pay;

import com.guns.bo.OrderBO;

public interface PayService {

    String precreate(OrderBO order);

    boolean query(String orderId);
}
