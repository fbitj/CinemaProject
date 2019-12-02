package com.stylefeng.guns.rest.modular.cinema.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.cinema.IMoocOrderTService;
import com.guns.vo.cinema.OrderVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/1 20:40
 */
@RestController
@RequestMapping("order")
public class TestOrderController {


    @Reference(interfaceClass = IMoocOrderTService.class, check = false)
    IMoocOrderTService orderTService;

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
    public OrderVo buyTicket(Integer fieldId, String soldSeats, String seatsName, HttpServletRequest request){
        Boolean trueSeats = orderTService.isTrueSeats(fieldId, soldSeats);
        OrderVo orderVo = new OrderVo();
        if(trueSeats){
            Boolean soldSeats1 = orderTService.isSoldSeats(fieldId, soldSeats);
            if(soldSeats1) {
//        从redis缓存中取得用户的uuid
//        String token = request.getHeader("Authorization");
//        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
//        Integer uuid = userCacheVO.getUuid();
                Object o = orderTService.buyTickets(fieldId, soldSeats, seatsName, 0);
                orderVo.setStatus(0);
                orderVo.setMsg("");
                orderVo.setData(o);
                return orderVo;
            }else {
                orderVo.setStatus(-2);
                orderVo.setMsg("该座位已被购买");
                return orderVo;
            }
        }else {
            orderVo.setStatus(-1);
            orderVo.setMsg("非法数据");
            return orderVo;
        }
    }


    /**r
     * request      /order/getOrderInfo
     * response
     * {
     * 	"status":0,
     * 	"msg":"",
     * 	"data":[
     *                {
     * 			"orderId":"18392981493",
     * 			"filmName":"基于SpringBoot 十分钟搞定后台管理平台",
     * 			"fieldTime":"9月8号 11:50",
     * 			"cinemaName":"万达影城(顺义金街店)",
     * 			"seatsName":"1排3座 1排4座 2排4座",
     * 			"orderPrice":"120",
     * 			"orderStatus”:”已关闭”
     *        },{
     * 			"orderId":"213581239123",
     * 			"filmName":"Tomcat+Memcached/Redis集群",
     * 			"fieldTime":"9月10号 13:50",
     * 			"cinemaName":"万达影城(顺义金街店)",
     * 			"seatsName":"1排3座 1排4座 2排4座",
     * 			"orderPrice":"140",
     * 			"orderStatus”:”已完成”
     *        },
     * 	]
     * }
     */
//    @RequestMapping("getOrderInfo")
    public OrderVo getOrderinfo(Integer nowPage, Integer pageSize, HttpServletRequest request){
//        从redis缓存中取得用户的uuid        需开启redis数据库
//        String token = request.getHeader("Authorization");
//        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
//        Integer uuid = userCacheVO.getUuid();
        Object userOrders = orderTService.getUserOrders(nowPage, pageSize, 0);
        OrderVo orderVo = new OrderVo();
        orderVo.setStatus(0);
        orderVo.setMsg("");
        orderVo.setData(userOrders);
        return orderVo;
    }


}
