package com.stylefeng.guns.rest.order;

import com.guns.vo.OrderVO;
import com.stylefeng.guns.rest.service.impl.OrderServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderTest {

    @Autowired
    OrderServiceImpl orderService;
    @Test
    public void contextLoads() {
        Boolean trueSeats = orderService.isTrueSeats(1, "1,2,3,24,21,13");
        System.out.println(trueSeats);
        Assert.assertTrue(trueSeats);
    }

    @Test
    public void buyTicket(){
        OrderVO orderVo = orderService.saveOrderInfo(1, "4,5,6", "4,5,6", 1);
        System.out.println(orderVo);
        Assert.assertNotNull(orderVo);
    }

}
