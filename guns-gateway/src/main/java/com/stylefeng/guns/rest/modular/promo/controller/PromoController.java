package com.stylefeng.guns.rest.modular.promo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.constant.RedisPrefixConstant;
import com.guns.service.promo.PromoService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.UserCacheVO;
import com.guns.vo.promo.CinemaPromoInfo;
import com.guns.vo.promo.SeckillVO;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.common.exception.CustomException;
import com.stylefeng.guns.rest.modular.auth.util.UserTokenUtils;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@RestController
@RequestMapping("promo")
public class PromoController {
    @Reference(interfaceClass = PromoService.class, check = false)
    private PromoService promoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private UserTokenUtils userTokenUtils;

    // 创建一个简单的线程池
    private ExecutorService executorService;

    @PostConstruct
    public void initExecutorService(){
        executorService = Executors.newFixedThreadPool(50);
    }
    /**
     * 查询活动信息
     * @param cinemaId
     * @return
     */
    @RequestMapping(value = "getPromo",method = RequestMethod.GET)
    public SeckillVO<List<CinemaPromoInfo>> getPromo(String cinemaId){
        return promoService.getPromos(cinemaId);
    }

    /**
     * 创建秒杀订单
     * @param promoId
     * @param amount
     * @param request
     * @return
     */
    @RequestMapping(value = "createOrder",method = RequestMethod.POST)
    public BaseRespVO createOrder(@RequestParam("promoId") String promoId,
                                  @RequestParam("amount") Integer amount,
                                  @RequestParam("promoToken") String promoToken,
                                  HttpServletRequest request) throws CustomException {
        // 数据校验
        if (StringUtils.isEmpty(promoId) || amount <= 0 || amount > 5 || StringUtils.isEmpty(promoToken.trim())){
            throw new IllegalArgumentException("请输入合法的数据");
        }

        // 获取到用户id，验证用户是否有效
        Integer userId = userTokenUtils.getUserId(request);

        // 验证库存是否售罄，查看缓存数据库中是否有该商品的库存售罄标记
        Object sellOut = redisTemplate.opsForValue().get(RedisPrefixConstant.SEC_KILL_STOCK_PROMOID + promoId);
        if (sellOut != null){// 库存售罄
            return BaseRespVO.ok("商品太火爆,已经卖光啦！");
        }else {
            // 验证用户令牌是否有效
            String key = RedisPrefixConstant.SEC_KILL_TOKEN_PREFIX + promoId +"_"+ userId;
            String promoTokenInRedis = (String) redisTemplate.opsForValue().get(key);
            if (!promoToken.equals(promoTokenInRedis)){
                // token无效
                return BaseRespVO.buzError("token无效");
            }
        }

        // 判断是否在活动期间
        if (!promoService.isActivating(promoId)) {
            throw new CustomException(100,"该商品不在活动期间！");
        }

        Future<Boolean> res = executorService.submit(()->{
            // 初始化一条流水信息，存在问题：当后续操作失败，仍然会入库，可以考虑放入到缓存数据库减轻数据库压力
            // 一般我们认为对没有资源竞争的数据库操作失败几率是很小的，对于有资源竞争的数据库操作要谨慎处理
            String stockLogId = promoService.initPromoStockLog(promoId,amount);
            if (StringUtils.isEmpty(stockLogId)){// 添加流水信息失败
                log.info("下单失败，因为流水信息初始化失败！promoId:{}, userId:{}, amount:{}", promoId, userId, amount);
                throw new CustomException(BizExceptionEnum.DATABASE_ERROR.getCode(),BizExceptionEnum.DATABASE_ERROR.getMessage());
            }

            // 创建订单
            Boolean order = promoService.savePromoOrderInTransaction(promoId, amount, userId,stockLogId);
            if (!order){// 创建订单失败, 抛出错误回滚数据库操作
                log.info("生成订单失败！");
                throw new CustomException(BizExceptionEnum.DATABASE_ERROR.getCode(),BizExceptionEnum.DATABASE_ERROR.getMessage());
            }
            return true;
        });
        try {
            Boolean aBoolean = res.get();
        } catch (Exception e) {
            throw new CustomException(BizExceptionEnum.DATABASE_ERROR.getCode(),BizExceptionEnum.DATABASE_ERROR.getMessage());
        }
        return BaseRespVO.ok("下单成功！");
    }


    /**
     * 将数据库库存刷新到缓存
     * @return
     */
    @RequestMapping(value = "publishPromoStock",method = RequestMethod.GET)
    public BaseRespVO publishPromoStock(){
        return promoService.publishPromoStock();
    }


    // 获取到秒杀令牌
    @RequestMapping(value = "generateToken",method = RequestMethod.GET)
    public BaseRespVO generateToken(@RequestParam("promoId") String promoId,HttpServletRequest request){
        // 参数校验
        if (StringUtils.isEmpty(promoId)){
            return BaseRespVO.buzError("参数不正确");
        }
        Integer userId = userTokenUtils.getUserId(request);
        return promoService.generateToken(promoId,userId);
    }

}
