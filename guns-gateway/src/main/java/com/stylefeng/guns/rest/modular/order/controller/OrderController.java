package com.stylefeng.guns.rest.modular.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.bo.FieldBO;
import com.guns.bo.OrderBO;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.service.film.FilmService;
import com.guns.service.order.OrderService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.OrderVO;
import com.guns.vo.cinema.CinemaVO;
import com.guns.vo.film.FilmInfoVO;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    @RequestMapping("buyTickets")
    public BaseRespVO buyTickets(Integer fieldId, String[] soldSeats, String[] seatsName, HttpServletRequest request) throws IOException {
        //判断座位表id是否正确
        //验证座位名==========待做
        boolean isTrue = orderService.verifySeat(fieldId, soldSeats);
        if (isTrue == false) return BaseRespVO.sysError();

        //判断座位是否已经被下单
        boolean isSell = orderService.verifyOrder(soldSeats);
        if (isSell == true) return BaseRespVO.sysError();

        //添加订单信息
        //获取用户id
        String token = request.getHeader(jwtProperties.getHeader());
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
        //获取电影名和影院名
        FilmInfoVO film = filmService.selectFilmNameById(fieldBO.getFilmId());
        CinemaVO cinema = cinemaTService.selectCinemaById(fieldBO.getCinemaId());
        orderVO.setCinemaName(cinema.getCinemaName());
        orderVO.setFilmName(film.getFilmName());
        return BaseRespVO.ok(orderVO);
    }
}
