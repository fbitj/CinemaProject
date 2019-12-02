package com.guns.service.order;

import com.guns.bo.FieldBO;
import com.guns.bo.OrderBO;
import com.guns.vo.OrderVO;

import java.io.IOException;

public interface OrderService {
    boolean verifySeat(Integer fieldId, String[] soldSeats) throws IOException;

    boolean verifyOrder(String[] soldSeats);

    OrderVO addOrder(OrderBO orderBO, FieldBO fieldBO);


    FieldBO selectFieldById(Integer fieldId);
}
