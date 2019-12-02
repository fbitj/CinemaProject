package com.stylefeng.guns.rest.modular.cinema.controller;

import com.guns.vo.cinema.OrderVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/1 20:40
 */
@RestController
@RequestMapping("order")
public class TestOrderController {



    /**
     * request
     * http://localhost/order/buyTickets
     * ?fieldId=1    soldSeats=10,15,16   seatsName=%E5%8D%95%E6%8E%92%E5%BA%A7,%E5%8F%8C%E6%8E%92%E5%BA%A7
     * response
     * {
     * 	"status":0,
     * 	"msg":"",
     * 	"data":{
     * 		"orderId":"18392981493",
     * 		"filmName":"基于SpringBoot 十分钟搞定后台管理平台",
     * 		"fieldTime":"今天 9月8号 11:50",
     * 		"cinemaName":"万达影城(顺义金街店)",
     * 		"seatsName":"1排3座 1排4座 2排4座",
     * 		"orderPrice":"120",
     * 		"orderTimestamp":"1589754126"
     *        }
     * }
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     */
//    @RequestMapping("byTickets")
    public OrderVo buyTicket(Integer fieldId, String soldSeats, String seatsName){
        OrderVo orderVo = new OrderVo();
        orderVo.setStatus(0);
        orderVo.setMsg("");
        orderVo.setData(new Object());
        return orderVo;
    }
}
