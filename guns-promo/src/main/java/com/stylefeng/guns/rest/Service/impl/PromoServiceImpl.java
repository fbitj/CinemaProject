package com.stylefeng.guns.rest.Service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.guns.constant.RedisPrefixConstant;
import com.guns.service.promo.PromoService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.promo.CinemaPromoInfo;
import com.guns.vo.promo.SeckillVO;
import com.stylefeng.guns.rest.common.exception.PromoException;
import com.stylefeng.guns.rest.common.persistence.StockLogStatus;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.roketmq.ProducerMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@com.alibaba.dubbo.config.annotation.Service(interfaceClass = PromoService.class)
public class PromoServiceImpl implements PromoService {

    private static final String PROMO_STOCK_CACHE_PREFIX = "SecKillStock_";

    @Autowired
    private MtimePromoOrderMapper promoOrderMapper;

    @Autowired
    private MtimePromoStockMapper promoStockMapper;

    @Autowired
    private MtimeStockLogMapper stockLogMapper;

    @Autowired
    private MtimePromoMapper promoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProducerMQ producer;

    // 查询活动中的影院
    @Override
    public SeckillVO<List<CinemaPromoInfo>> getPromos(String cinemaId) {
        // 根据影院id查询活动
        PageHelper.startPage(1, 10);
        List<CinemaPromoInfo> cinemaPromoInfoList;
        try {
            cinemaPromoInfoList = promoMapper.selectCinemasPromoInfo(cinemaId,PromoStatus.PROMO_STARTING.getStatus());
        } catch (Exception e) {
            throw new PromoException("查询秒杀活动失败");
        }
        PageInfo<CinemaPromoInfo> cinemaPromoInfoPageInfo = new PageInfo<>();
        long total = cinemaPromoInfoPageInfo.getTotal();
        // 封装响应数据
        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setNowPage(1);
        seckillVO.setTotalPage((int) total);
        seckillVO.setStatus(0);
        seckillVO.setData(cinemaPromoInfoList);
        return seckillVO;
    }

    /**
     * 创建秒杀下单
     */
    /*@Override
    @Deprecated
    public BaseRespVO createOrder(String promoId, Integer amount, Integer userId) {
        BaseRespVO baseRespVO = new BaseRespVO();
        // 0.判断是否在活动期间
        if (!isActivating(promoId)) {
            baseRespVO.setStatus(100);
            baseRespVO.setMsg("不在活动期间不能下单");
            return baseRespVO;
        }
        // 1.判断库存是否足够==>原数据库中读取，改为Redis中读取
//        int stock = promoStockMapper.selectStockByPromoId(promoId);
        Integer stock = (Integer) redisTemplate.opsForValue().get("SecKillStock_" + promoId);
        if (amount > stock) {
            baseRespVO.setStatus(100);
            baseRespVO.setMsg("库存不足，还剩" + stock + "张");
            return baseRespVO;
        }
        // 2.添加秒杀订单
        MtimePromoOrder PromoOrder = NewPromoOrder(promoId, amount, userId);
        int res = promoOrderMapper.insert(PromoOrder);
        if (res == 0) {
            return BaseRespVO.buzError("订单生成错误");
        }
        // 3.库存减少==>原修改MySQL数据库，改为修改Redis
//        int update = promoStockMapper.updateStockByPromoId(promoId, amount);
        try {
            redisTemplate.opsForValue().increment("SecKillStock_" + promoId, amount * -1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PromoException("更新库存失败或错误");
        }
        // 通知consumer修改数据库库存===========后续使用分布式事务===================
        producer.decreaseStock(promoId, amount);
        baseRespVO.setStatus(0);
        baseRespVO.setMsg("下单成功");
        return baseRespVO;
    }*/

    // 将数据库中的库存刷新到缓存
    @Override
    public BaseRespVO publishPromoStock() {
        // 1.从数据库查询到所有的活动中的库存
        // 2.判断改库存是否添加到缓存
        // 否：添加到缓存，是：跳过
        List<MtimePromoStock> mtimePromoStocks = promoStockMapper.selectList(new EntityWrapper<>());
        for (MtimePromoStock promoStock : mtimePromoStocks) {
            String key = "SecKillStock_" + promoStock.getPromoId();
            Object stock = null;
            try {
                stock = redisTemplate.opsForValue().get(key);
            } catch (Exception e) {
                e.printStackTrace();
                return BaseRespVO.buzError("发布失败!");
            }
            if (stock == null) {
                // 将库存刷新到缓存
                redisTemplate.opsForValue().set(key, promoStock.getStock());
                // 存入秒杀令牌数量到redis
                String tokenLimitKey = RedisPrefixConstant.SEC_KILL_TOKEN_NUMBER_LIMIT + promoStock.getPromoId();
                Integer tokenNumberLimit = RedisPrefixConstant.SEC_KILL_TOKEN_TIMES * promoStock.getStock();
                redisTemplate.opsForValue().set(tokenLimitKey, tokenNumberLimit);
            }
        }
        return BaseRespVO.ok("发布成功!");
    }

    /**
     * 判断是否在活动期间
     */
    public Boolean isActivating(String promoId) {
        if (StringUtils.isEmpty(promoId)) return false;

        MtimePromo mtimePromo = promoMapper.selectById(promoId);
        if (mtimePromo.getStatus() != PromoStatus.PROMO_STARTING.getStatus()) {
            return false;
        }
        Date date = new Date();
        if (date.before(mtimePromo.getStartTime()) || date.after(mtimePromo.getEndTime())) {
            return false;
        }
        return true;
    }

    /**
     * 创建一个promo订单对象
     */
    public MtimePromoOrder NewPromoOrder(String promoId, Integer amount, Integer userId) {
        // 1.通过活动id查询活动表
        // 2.生成兑换码
        MtimePromo mtimePromo = promoMapper.selectById(promoId);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String exchangeCode = UUID.randomUUID().toString().replaceAll("-", "");
        BigDecimal orderPrice = mtimePromo.getPrice().multiply(new BigDecimal(amount));
        // 封装数据
        MtimePromoOrder PromoOrder = new MtimePromoOrder();
        PromoOrder.setUuid(uuid);
        PromoOrder.setUserId(userId);
        PromoOrder.setCinemaId(mtimePromo.getCinemaId());
        PromoOrder.setExchangeCode(exchangeCode);
        PromoOrder.setAmount(amount);
        PromoOrder.setPrice(orderPrice);
        PromoOrder.setStartTime(mtimePromo.getStartTime());
        PromoOrder.setCreateTime(new Date());
        PromoOrder.setEndTime(mtimePromo.getEndTime());// 兑换结束时间应该是活动结束时间吗？
        return PromoOrder;
    }

    /**
     * 生成秒杀令牌 token
     * @param promoId
     * @return
     */
    @Override
    public BaseRespVO generateToken(String promoId, Integer userId) {
        // 判断秒杀令牌的数量是否足够
        String tokenLimitKey = RedisPrefixConstant.SEC_KILL_TOKEN_NUMBER_LIMIT + promoId;
        Long increment = redisTemplate.opsForValue().increment(tokenLimitKey, -1);
        if (increment < 0){ // 令牌已发放完
            return BaseRespVO.buzError("该商品已售罄！");
        }

        // 判断库存是否售罄，是：不在发放令牌
        String stockNullKey = RedisPrefixConstant.SEC_KILL_NULL_STOCK + promoId;
        Boolean isSellOut = redisTemplate.hasKey(stockNullKey);
//        Object sellOut = redisTemplate.opsForValue().get(stockNullKey);
        if (isSellOut){
            return BaseRespVO.buzError("该商品已售罄！");
        }

        String tokenKey = RedisPrefixConstant.SEC_KILL_TOKEN_PREFIX + promoId +"_"+ userId;
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(tokenKey, token, 30, TimeUnit.MINUTES);
        return BaseRespVO.ok(token);
    }


    // 初始化一条库存流水
    @Override
    public String initPromoStockLog(String promoId, Integer amount) {
        MtimeStockLog stockLog = new MtimeStockLog();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        stockLog.setUuid(uuid);
        stockLog.setPromoId(Integer.parseInt(promoId));
        stockLog.setAmount(amount);
        stockLog.setStatus(StockLogStatus.INIT.getStatus());
        Integer insert;
        try {
            insert = stockLogMapper.insert(stockLog);
        } catch (Exception e) {
            log.info("添加流水信息时异常, uuid:{}, promoId:{}, amountId:{}", uuid, promoId, amount);
            e.printStackTrace();
            return null;
        }
        if (insert > 0){
            return uuid;
        }
        return null;
    }

    // 事务型秒杀下单
    @Override
    public Boolean savePromoOrderInTransaction(String promoId, Integer amount, Integer userId, String stockLogId) {
        return producer.sendStockMessageInTransaction(promoId, amount, userId, stockLogId);
    }

    /**
     * 插入秒杀订单，同时扣减缓存库存
     * @param promoId
     * @param amount
     * @param userId
     * @param stockLogId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Boolean savePromoOrderVO(String promoId, Integer amount, Integer userId, String stockLogId) {
        // 参数校验

        // 生成秒杀订单
        MtimePromoOrder promoOrder = null;
        try {
            promoOrder = NewPromoOrder(promoId, amount, userId);
            promoOrderMapper.insert(promoOrder);
        } catch (Exception e) {
            log.info("插入秒杀订单失败");
            e.printStackTrace();
            try {
                // 修改订单流水状态
                stockLogMapper.updateStatusById(stockLogId, StockLogStatus.FAILED.getStatus());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            throw new PromoException("生成订单失败");
        }

        // 扣减缓存库存
        Boolean res = decreaseStock(promoId, amount);
        if (!res){
            // 更新库存流水的状态
            stockLogMapper.updateStatusById(stockLogId, StockLogStatus.FAILED.getStatus());
            throw new PromoException("扣减缓存库存失败！");
        }
        // 假如本地事务执行成功  更新库存流水记录的状态 -----成功
        stockLogMapper.updateStatusById(stockLogId, StockLogStatus.SUCCESS.getStatus());
        return true;
    }

    private Boolean decreaseStock(String promoId, Integer amount) {
        String key = PROMO_STOCK_CACHE_PREFIX + promoId;
        Long increment;
        try {
            increment = redisTemplate.opsForValue().increment(key, amount*-1);
        } catch (Exception e) {
            log.info("连接缓存数据库异常");
            e.printStackTrace();
            return false;
        }
        if (increment < 0){// 库存不足
            log.info("库存不足,promoId:{}",promoId);
            // 将扣掉的库存还原
            redisTemplate.opsForValue().increment(key,amount);
            return false;
        }
        return true;
    }
}
