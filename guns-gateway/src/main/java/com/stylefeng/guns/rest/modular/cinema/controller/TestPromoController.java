package com.stylefeng.guns.rest.modular.cinema.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.util.concurrent.RateLimiter;
import com.guns.service.cinema.IMtimePromoOrderService;
import com.guns.service.cinema.IMtimePromoService;
import com.guns.service.cinema.IMtimePromoStockService;
import com.guns.service.cinema.IMtimeStockLogService;
import com.guns.service.promo.RedisTokenPerfix;
import com.guns.vo.UserCacheVO;
import com.guns.vo.cinema.OrderVo;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/3 18:10
 */
@RestController
@RequestMapping("promo")
@EnableTransactionManagement
public class TestPromoController {


    @Reference(interfaceClass = IMtimePromoService.class, check = false)
    IMtimePromoService promoService;
    @Reference(interfaceClass = IMtimePromoOrderService.class, check = false)
    IMtimePromoOrderService promoOrderService;
    @Reference(interfaceClass = IMtimePromoStockService.class, check = false)
    IMtimePromoStockService promoStockService;
    @Reference(interfaceClass = IMtimeStockLogService.class, check = false)
    IMtimeStockLogService stockLogService;
    @Autowired
    RedisTemplate redisTemplate;


    //设置一个线程池
    private ExecutorService executorService;

    //设置一个令牌桶
    private RateLimiter rateLimiter;


    @PostConstruct
    public void init(){
        //创建一个固定线程大小的线程池
        executorService = Executors.newFixedThreadPool(100);

//        //创建一个动态扩容无线程限制的线程池
//        executorService = Executors.newCachedThreadPool();
//
//        //创建一个只有一个线程的线程池
//        executorService = Executors.newSingleThreadExecutor();
//
//        //创建一个定时任务去执行的线程池
//        executorService = Executors.newScheduledThreadPool(10);

        //每秒放置10个令牌
        rateLimiter = RateLimiter.create(100);
    }




    //显示优惠影院列表
    /**
     * request
     *      /promo/getPromo?brandId=99&hallType=99&areaId=99&pageSize=12&nowPage=1
     * response
     *     "data":[{
     * 			"cinemaAddress":"北京市朝阳区大屯路50号金街商场",
     * 			"cinemaId":3,
     * 			"cinemaName":"万达影院(大屯店)",
     * 			"description":"万达大酬宾",
     * 			"endTime":"2019-08-30 15:37:03",
     * 			"imgAddress":"cinema2.jpg",
     * 			"price":20,
     * 			"startTime":"2019-08-01 15:36:54",
     * 			"status":1,
     * 			"stock":0,
     * 			"uuid":3
     *                }
     * 	],
     * 	"imgPre":"",
     * 	"msg":"",
     * 	"nowPage":"",
     * 	"status":0,
     * 	"totalPage":""
     * }
     * @return
     */
    @RequestMapping("getPromo")
    public OrderVo getPrommos(Integer brandId, Integer hallType, Integer areaId, Integer pageSize, Integer nowPage){
        OrderVo orderVo = new OrderVo();
        List<HashMap<String, Object>> promos = (List) promoService.getPromos();
        for (HashMap promo : promos) {
            Integer uuid = (Integer) promo.get("uuid");
            Integer stockFromRedis = (Integer) redisTemplate.opsForValue().get(uuid + "");
            promo.put("stock", stockFromRedis);
        }
        orderVo.setStatus(0);
        orderVo.setMsg("");
        orderVo.setData(promos);
        return orderVo;
    }


    //将库存更新到redis缓存中
    /**
     * request
     * promo/publishPromoStock
     * response
     * {
     * 	"data":"",
     * 	"imgPre":"",
     * 	"msg":"发布成功!",
     * 	"nowPage":"",
     * 	"status":0,
     * 	"totalPage":""
     * }
     */
    @RequestMapping("publishPromoStock")
    public OrderVo pushPromoStock(){
        OrderVo orderVo = new OrderVo();
        //先写成1   后续根据过期时间动态调整promoId
        Object stock = redisTemplate.opsForValue().get(1 + "");
        //还没更新到redis缓存中则读取数据并更新
        //只更新一次， 防止数据库更新不及时，造成数据错乱
        if(stock == null){
            List<HashMap<String, Object>> hashMaps = promoStockService.pushPromoStock();
            for (HashMap<String, Object> hashMap : hashMaps) {
                Integer promoId = (Integer) hashMap.get("promoId");
                Integer amount = (Integer) hashMap.get("stock");
                redisTemplate.opsForValue().set(promoId + "", amount);
                //设置过期时间
                redisTemplate.expire(promoId + "", 60*500, TimeUnit.SECONDS);

                //存放秒杀令牌数量到redis缓存中
                String tokenPerfix = RedisTokenPerfix.STOCK_PERFIX + promoId;
                Integer tokenStock = amount * 7;
                redisTemplate.opsForValue().set(tokenPerfix, tokenStock);
                redisTemplate.expire(tokenPerfix + "", 60*500, TimeUnit.SECONDS);
            }
        }
        orderVo.setStatus(0);
        orderVo.setMsg("发布成功！！");
        orderVo.setData("");
        return orderVo;
    }


    //秒杀下单
    /**
     * request
     * ///promo/createOrder
     *      promoId     秒杀活动id,必须传
     *      amount  	数量
     * response
     *          {
     * 					"status":"0",
     * 					"msg":"下单成功"
     *        }
     * @param request
     * @return
     */
//    @Transactional
    @RequestMapping("createOrder")
    public OrderVo creatOrder(HttpServletRequest request,Integer promoId, Integer amount, String promoToken){
        OrderVo orderVo = new OrderVo();
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
        if(userCacheVO == null){
            orderVo.setStatus(-1);
            orderVo.setMsg("请重新登陆");
            return orderVo;
        }
        //RateLimiter  限流   返回结果是线程的等待时间
        double acquire = rateLimiter.acquire();
        if(acquire < 0){
            orderVo.setStatus(-6);
            orderVo.setMsg("秒杀失败");
            return orderVo;
        }

        //参数校验
        if(amount <= 0 || amount > 5){
            orderVo.setStatus(-2);
            orderVo.setMsg("数量不合法");
            return orderVo;
        }
        //判断库存是否足够
        Integer stock = (Integer) redisTemplate.opsForValue().get(promoId + "");
        if(stock <= 0){
            orderVo.setStatus(-3);
            orderVo.setMsg("库存不足");
            return orderVo;
        }
        //获取秒杀令牌， 如果没有秒杀令牌，则直接返回
        String tokenKey = RedisTokenPerfix.TOKEN_PERFIX + promoId + "_userId_" + userCacheVO.getUuid();
        Boolean aBoolean = redisTemplate.hasKey(tokenKey);
        if(!aBoolean){
            orderVo.setStatus(-4);
            orderVo.setMsg("活动太火爆，请刷新重试");
            return orderVo;
        }
        String userToken = (String) redisTemplate.opsForValue().get(tokenKey);
        if(!promoToken.equals(userToken)){
            orderVo.setStatus(-5);
            orderVo.setMsg("令牌不合法");
            return orderVo;
        }

        //最多300个线程来并法执行
        Future<Boolean> future = executorService.submit(() ->{
            //下单之前生成流水号id
            String stockLogId = stockLogService.getStockLogId(promoId, amount);
            if(StringUtils.isBlank(stockLogId)){
                throw new GunsException(BizExceptionEnum.AUTH_REQUEST_ERROR);
            }
            Boolean result = null;
            try {
                //下单， 削减库存
                result = promoService.saveOrderPromoTransactional(promoId, amount, userCacheVO.getUuid(), stockLogId);
            }catch (Exception r){
                throw new GunsException(BizExceptionEnum.AUTH_REQUEST_ERROR);
            }
            if(!result){
                throw new GunsException(BizExceptionEnum.ORDER_ERROR);
            }
            return result;
        });
        Boolean result = false;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (GunsException g){
            throw new GunsException(BizExceptionEnum.OTHER_ERROR);
        }

        orderVo.setStatus(0);
        orderVo.setMsg("下单成功");
        return orderVo;
    }


    //获取秒杀令牌
    ///promo/
    @RequestMapping("generateToken")
    public OrderVo generateToken(Integer promoId, HttpServletRequest request){
        //判断promoId 是否真实有效，
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        OrderVo orderVo = new OrderVo();
        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
        if(userCacheVO == null){
            orderVo.setMsg("请重新登陆");
            orderVo.setStatus(-1);
            return orderVo;
        }

        String tokenToRedis = promoService.generateToken(promoId, userCacheVO.getUuid());
        if(StringUtils.isBlank(tokenToRedis)){
            orderVo.setStatus(-2);
            orderVo.setMsg("令牌不足");
            return orderVo;
        }
        orderVo.setStatus(0);
        orderVo.setMsg(tokenToRedis);
        return orderVo;
    }

}
