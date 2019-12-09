package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.service.cinema.IMtimePromoService;
import com.guns.service.cinema.IMtimePromoStockService;
import com.guns.service.promo.RedisTokenPerfix;
import com.guns.vo.cinema.OrderVo;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.stylefeng.guns.rest.roketmq.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-12-03
 */
@Component
@Service(interfaceClass = IMtimePromoService.class)
public class MtimePromoServiceImpl implements IMtimePromoService {

    @Autowired
    MtimePromoMapper promoMapper;
    @Autowired
    IMtimeCinemaTService cinemaTService;
    @Autowired
    IMtimePromoStockService promoStockService;
    @Autowired
    MqProducer mqProducer;
    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public Object getPromos() {
        EntityWrapper<MtimePromo> wrapper = new EntityWrapper<>();
        wrapper.isNotNull("cinema_id");
        List<MtimePromo> mtimePromos = promoMapper.selectList(wrapper);
        ArrayList<Object> list = new ArrayList<>();
        for (MtimePromo mtimePromo : mtimePromos) {
            MtimeCinemaT cine = (MtimeCinemaT) cinemaTService.getCine(mtimePromo.getCinemaId());
            HashMap<Object, Object> map = new HashMap<>();
            map.put("cinemaAddress", cine.getCinemaAddress());
            map.put("cinemaId", mtimePromo.getCinemaId());
            map.put("cinemaName", cine.getCinemaName());
            map.put("description", mtimePromo.getDescription());
            map.put("endTime", mtimePromo.getEndTime());
            map.put("imgAddress", cine.getImgAddress());
            map.put("price", mtimePromo.getPrice());
            map.put("startTime", mtimePromo.getStartTime());
            map.put("status", mtimePromo.getStatus());
            MtimePromoStock promoStock = (MtimePromoStock) promoStockService.getPromoStock(mtimePromo.getUuid());
            map.put("stock", promoStock.getStock());
            map.put("uuid", mtimePromo.getUuid());
            list.add(map);
        }
        return list;
    }

    @Override
    public Object getPromo(Integer promo_id) {
        EntityWrapper<MtimePromo> wrapper = new EntityWrapper<>();
        wrapper.eq("uuid", promo_id);
        List<MtimePromo> mtimePromos = promoMapper.selectList(wrapper);
        MtimePromo mtimePromo = mtimePromos.get(0);
        return mtimePromo;
    }

    @Override
    public Object getPromosByStatus() {
        EntityWrapper<MtimePromo> wrapper = new EntityWrapper<>();
        wrapper.eq("status", 1);
        List<MtimePromo> mtimePromos = promoMapper.selectList(wrapper);
        return mtimePromos;
    }

    @Override
    public Boolean saveOrderPromoTransactional(Integer promoId, Integer amount, Integer uuid, String stockId) {
        boolean result = mqProducer.sendTransactionMessage(promoId, amount, uuid, stockId);
        return result;
    }

    @Override
    public String generateToken(Integer promoId, Integer userId) {
        //判断令牌数量是否足够
        String tokenPerfix = RedisTokenPerfix.STOCK_PERFIX + promoId;
        Long increment = redisTemplate.opsForValue().increment(tokenPerfix, -1);
        if(increment <= 0){
            redisTemplate.opsForValue().set(tokenPerfix, 0);
            return null;
        }

        //足够则生成令牌
        String token = UUID.randomUUID().toString().replaceAll("-", "");
//        String token = "redis";
        String tokenKey = RedisTokenPerfix.TOKEN_PERFIX + promoId + "_userId_" + userId;
        redisTemplate.opsForValue().set(tokenKey, token);
        return token;
    }
}
