package com.stylefeng.guns.rest.modular.promo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.service.promo.PromoService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.cinema.CinemaVO;
import com.guns.vo.promo.PromoContainStockVO;
import com.guns.vo.promo.PromoVO;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("promo")
public class PromoController {

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Reference(interfaceClass = PromoService.class,check = false)
    PromoService promoService;

    @Reference(interfaceClass = IMtimeCinemaTService.class, check = false)
    IMtimeCinemaTService cinemaTService;

    /**
     * 获取所有秒杀活动
     * @param cinemaId
     * @return
     */
    @RequestMapping("getPromo")
    public BaseRespVO getPromo(Integer cinemaId) {
        //分次查询
        List<PromoContainStockVO> promoList = promoService.selectPromo(cinemaId, null);
        List<PromoVO> result = new ArrayList<>();
        //再查询每个promo的影院信息
        if (promoList != null) {
            for (PromoContainStockVO promoVO : promoList) {
                PromoVO promo = new PromoVO();
                BeanUtils.copyProperties(promoVO, promo);
                //封装时间信息
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                promo.setStartTime(simpleDateFormat.format(promoVO.getStartTime()));
                promo.setEndTime(simpleDateFormat.format(promoVO.getEndTime()));

                //查询影院信息
                Integer cId = promoVO.getCinemaId();
                CinemaVO cinemaVO = cinemaTService.selectCinemaById(cId);
                if (cinemaVO == null) throw new IllegalArgumentException();
                BeanUtils.copyProperties(cinemaVO, promo);
                result.add(promo);
            }
        }
        return BaseRespVO.ok(result);
    }

    /**
     * 秒杀下单
     * @param promoId
     * @param amount
     * @return
     */
    @RequestMapping("createOrder")
    @Transactional
    public BaseRespVO createOrder(Integer promoId, Integer amount, HttpServletRequest request) {
        //查询秒杀表
        List<PromoContainStockVO> promos = promoService.selectPromo(null, promoId);
        if (CollectionUtils.isEmpty(promos) || promos.size() > 1) throw new IllegalArgumentException();
        PromoContainStockVO promo = promos.get(0);
        Integer stock = promo.getStock();
        //if (stock == 0 || stock < amount) return BaseRespVO
        //获取用户id
        String token = request.getHeader(jwtProperties.getHeader()).substring(7);
        Integer userId = redisTemplate.opsForValue().get(token);
        //插入数据库
        promoService.createOrder(userId, promo, amount);

        //更新库存
        amount = stock - amount;
        promoService.updateStock(promoId, amount);
        return BaseRespVO.ok("下单成功");
    }

    //待更改
    @RequestMapping("generateToken")
    public BaseRespVO generateToken(Integer promoId) {
        String s = UUID.randomUUID().toString();
        return BaseRespVO.ok(s);
    }
}
