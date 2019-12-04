package com.stylefeng.guns.rest.modular.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.bo.FieldBO;
import com.guns.bo.OrderBO;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.service.film.FilmService;
import com.guns.service.order.OrderService;
import com.guns.service.pay.PayService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.OrderVO;
import com.guns.vo.cinema.CinemaVO;
import com.guns.vo.film.FilmInfoVO;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Reference(interfaceClass = OrderService.class, check = false)
    OrderService orderService;

    @Reference(interfaceClass = FilmService.class, check = false)
    FilmService filmService;

    @Reference(interfaceClass = IMtimeCinemaTService.class, check = false)
    IMtimeCinemaTService cinemaTService;

    @Reference(interfaceClass = PayService.class, check = false)
    PayService payService;

    /**
     * 购买座位
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("buyTickets")
    @Transactional
    public BaseRespVO buyTickets(Integer fieldId, String[] soldSeats, String[] seatsName, HttpServletRequest request) throws IOException {
        //判断座位表id是否正确
        //验证座位名==========待做
        boolean isTrue = orderService.verifySeat(fieldId, soldSeats);
        if (isTrue == false) return BaseRespVO.sysError();

        //判断座位是否已经被下单
        boolean isSell = orderService.verifyOrder(fieldId,soldSeats);
        if (isSell == true) return BaseRespVO.sysError();

        //添加订单信息
        //获取用户id
        String token = request.getHeader(jwtProperties.getHeader()).substring(7);
        Integer userId = redisTemplate.opsForValue().get(token);

        //查询场次信息
        FieldBO fieldBO = orderService.selectFieldById(fieldId);
        //封装对象
        OrderBO orderBO = new OrderBO();
        orderBO.setOrderUser(userId);
        orderBO.setFieldId(fieldId);
        orderBO.setSeatsIds(soldSeats);
        orderBO.setSeatsName(seatsName);
        OrderVO orderVO = orderService.addOrder(orderBO, fieldBO);
        if (orderVO == null) {
            BaseRespVO baseRespVO = new BaseRespVO();
            baseRespVO.setStatus(1);
            baseRespVO.setMsg("订单支付失败，请稍后重试");
            return baseRespVO;
        }
        //获取电影名和影院名
        FilmInfoVO film = filmService.selectFilmNameById(fieldBO.getFilmId());
        CinemaVO cinema = cinemaTService.selectCinemaById(fieldBO.getCinemaId());
        orderVO.setCinemaName(cinema.getCinemaName());
        orderVO.setFilmName(film.getFilmName());
        return BaseRespVO.ok(orderVO);
    }

    /**
     * 查看用户订单
     * @param nowPage
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping("getOrderInfo")
    public BaseRespVO getUserOrder(Integer nowPage, Integer pageSize, HttpServletRequest request) {
        String token = request.getHeader(jwtProperties.getHeader());
        Integer userId = redisTemplate.opsForValue().get(token.substring(7));
        List<OrderVO> orders = orderService.selectOrderByUserId(userId, nowPage, pageSize);
        if (!CollectionUtils.isEmpty(orders)) {
            for (OrderVO order : orders) {
                Integer filmId = order.getFilmId();
                Integer cinemaId = order.getCinemaId();
                FilmInfoVO film = filmService.selectFilmNameById(filmId);
                CinemaVO cinema = cinemaTService.selectCinemaById(cinemaId);
                order.setCinemaName(cinema.getCinemaName());
                order.setFilmName(film.getFilmName());
            }
        }
        return BaseRespVO.ok(orders);
    }

    //支付模块

    /**
     * 生成支付二维码
     * @param orderId
     * @return
     */
    @RequestMapping("getPayInfo")
    public BaseRespVO getPayCode(String orderId) {
        //查询订单详情
        OrderBO orderBO = orderService.selectOrderByOrderId(orderId);
        String codePic = payService.precreate(orderBO);
        if (codePic == null) return BaseRespVO.sysError();
        //封装参数
        Map data = new HashMap();
        data.put("orderId", orderId);
        data.put("qRCodeAddress", codePic);
        BaseRespVO baseRespVO = new BaseRespVO();
        baseRespVO.setData(data);
        //请求地址暂定
        baseRespVO.setImgPre("http://localhost/pic/");
        baseRespVO.setStatus(0);
        return baseRespVO;
    }

    /**
     * 检验用户是否在规定时间内扫码支付
     * @param orderId
     * @param tryNums
     * @return
     */
    @RequestMapping("getPayResult")
    public BaseRespVO getPayResult(String orderId, Integer tryNums) {
        BaseRespVO baseRespVO = new BaseRespVO();
        baseRespVO.setStatus(1);
        baseRespVO.setMsg("订单支付失败, 请稍后重试");
        if (tryNums > 3) {
            //超时，将订单状态码设为2
            return baseRespVO;
        }
        boolean result = payService.query(orderId);
        if (result == true) {
            //已支付，修改订单状态码
            orderService.changeOrderStatus(1, orderId);
            //封装响应报文
            Map data = new HashMap();
            data.put("orderId", orderId);
            data.put("orderStatus", 1);
            data.put("orderMsg", "支付成功");
            return BaseRespVO.ok(data);
        }
        return baseRespVO;
    }
}
