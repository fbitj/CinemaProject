package com.stylefeng.guns.rest.service;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.dubbo.config.annotation.Service;
import com.guns.service.cinema.IMtimePromoOrderService;
import com.guns.service.cinema.IMtimePromoService;
import com.guns.service.cinema.IMtimePromoStockService;
import com.guns.service.cinema.IMtimeStockLogService;
import com.guns.utils.MD5Util;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.stylefeng.guns.rest.roketmq.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-12-03
 */
@Component
@Service(interfaceClass = IMtimePromoOrderService.class)
@EnableTransactionManagement
public class MtimePromoOrderServiceImpl  implements IMtimePromoOrderService {

    @Autowired
    MtimePromoOrderMapper promoOrderMapper;
    @Autowired
    IMtimePromoService promoService;
    @Autowired
    IMtimePromoStockService promoStockService;
    @Autowired
    MqProducer mqProducer;
    @Autowired
    IMtimeStockLogService stockLogService;
    @Autowired
    RedisTemplate redisTemplate;


    private ExecutorService executorService;

    @PostConstruct
    public void init(){
        //创建一个线程池， 里面存放10个线程
        executorService = Executors.newFixedThreadPool(10);
    }



    //创建订单， 消减库存
    @Override
    @Transactional
    public Integer insertIntoPromOrder(Integer userId, Integer promoId, Integer amount, String stockId) {
        //创建订单
        MtimePromo promo = (MtimePromo) promoService.getPromo(promoId);
        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        mtimePromoOrder.setUuid(uuid);
        mtimePromoOrder.setUserId(userId);
        mtimePromoOrder.setCinemaId(promo.getCinemaId());
        //MD5 散列唯一标识字符串
        String md5Password = MD5Util.getMd5Password(uuid);
        mtimePromoOrder.setExchangeCode(md5Password);
        mtimePromoOrder.setAmount(amount);
        Integer integer = new Integer(amount * promo.getPrice().intValue());
        BigDecimal bigDecimal = new BigDecimal(integer.toString());
        mtimePromoOrder.setPrice(bigDecimal);
        mtimePromoOrder.setStartTime(promo.getStartTime());
        mtimePromoOrder.setCreateTime(new Date());
        mtimePromoOrder.setEndTime(promo.getEndTime());
        //插入订单
        Integer insert = promoOrderMapper.insert(mtimePromoOrder);

        //更改订单状态异常， 抛出异常并回滚
        if(insert == null || insert < 0){
            //更改流水单的状态
            executorService.submit(() ->{
                stockLogService.updataStockLogById(stockId, 3);
            });
            throw new GunsException(BizExceptionEnum.ORDER_SATUS);
        }

        //减少库存, 出现异常则抛出并回滚
        Long increment = redisTemplate.opsForValue().increment(promoId + "", amount * -1);
        if(increment < 0){
            redisTemplate.opsForValue().increment(promoId + "", amount);
            executorService.submit(() ->{
                stockLogService.updataStockLogById(stockId, 3);
            });
            throw new GunsException(BizExceptionEnum.ORDER_SATUS);
        }

//        int a = 1 / 0;
        //都没问题则更新订单状态
        stockLogService.updataStockLogById(stockId, 2);

        return insert;
    }

}
