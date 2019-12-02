package com.stylefeng.guns.rest.modular.order.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.order.OrderService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.OrderVO;
import com.stylefeng.guns.rest.modular.order.controller.dto.BuyTicketRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("order")
public class OrderController {
    @Reference(interfaceClass = OrderService.class)
    OrderService orderService;

    @Transactional
    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public BaseRespVO buyTickets(BuyTicketRequest buyTicketRequest, HttpServletRequest request) {
        Integer fieldId = buyTicketRequest.getFieldId();
        String seatsName = buyTicketRequest.getSeatsName();
        String soldSeats = buyTicketRequest.getSoldSeats();
        // 验证作为信息是否合法
        Boolean trueSeats = orderService.isTrueSeats(fieldId, soldSeats);
        if (!trueSeats) {// 如果不合法，返回购买不合法

        }
        // 验证作为是否被售出
        Boolean isSold = orderService.isSoldSeats(fieldId, soldSeats);
        if (isSold) {// 如果已经被售出，返回被售出

        }
        // 生成订单，并返回订单信息
        // 查询用户id,通过jwt获取
        int userId = getUserId(request);
        OrderVO orderVo = orderService.saveOrderInfo(fieldId, soldSeats, seatsName, userId);
        // 返回数据
        BaseRespVO baseRespVO = new BaseRespVO();
        baseRespVO.setStatus(0);
        baseRespVO.setData(orderVo);
        return baseRespVO;
    }

    @Value("${jwt.header}")
    private String header;

    private int getUserId(HttpServletRequest request){
        /*String token = request.getHeader(header);
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(token);
        // 根据token去redis中查询用户信息*/

        return 1;
    }


    @RequestMapping(value = "getPayInfo",method = RequestMethod.POST)
    public BaseRespVO getPayInfo(String orderId){
        return orderService.getPayCode(orderId);
    }
}
