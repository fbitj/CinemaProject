package com.stylefeng.guns.rest.modular.order.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.order.OrderService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.UserCacheVO;
import com.guns.vo.order.OrderVO;
import com.guns.vo.order.OrdersInfoVO;
import com.stylefeng.guns.rest.modular.order.controller.dto.BuyTicketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {

    @Reference(interfaceClass = OrderService.class, check = false)
    OrderService orderService;

    @Value("${jwt.header}")
    private String header;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 购买电影票
     * @param buyTicketRequest 前端传过来的参数封装
     * @param request 用于获取token
     * @return
     */
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


    /**
     * 根据token获取到用户id
     * @param request
     * @return
     */
    private int getUserId(HttpServletRequest request){
        String token = request.getHeader(header).substring(7);
        UserCacheVO user = (UserCacheVO) redisTemplate.opsForValue().get(token);
        return user.getUuid();
    }


    /**
     * 通过订单，生成支付二维码
     * @param orderId
     * @return
     */
    @RequestMapping(value = "getPayInfo",method = RequestMethod.POST)
    public BaseRespVO getPayInfo(String orderId){
        return orderService.getPayCode(orderId);
    }


    /**
     * 获取订单详情
     * @param nowPage
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "getOrderInfo",method = RequestMethod.POST)
    public BaseRespVO<List<OrdersInfoVO>> getOrderInfo(Integer nowPage, Integer pageSize, HttpServletRequest request) {
        // 获取用户id
        int userId = getUserId(request);
        BaseRespVO<List<OrdersInfoVO>> orderInfoByPage = orderService.getOrderInfoByPage(nowPage, pageSize, userId);
        return orderInfoByPage;
    }

    /**
     * 查询订单是否支付成功
     * @param orderId
     * @param tryNums
     * @return
     */
    @RequestMapping("getPayResult")
    public BaseRespVO getPayResult(String orderId,Integer tryNums){
        return orderService.getPayResult(orderId, tryNums);
    }

}
