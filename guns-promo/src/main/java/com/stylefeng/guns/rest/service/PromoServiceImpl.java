package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.promo.PromoService;
import com.guns.vo.promo.PromoContainStockVO;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Service(interfaceClass = PromoService.class)
public class PromoServiceImpl implements PromoService {

    @Autowired
    MtimePromoMapper mtimePromoMapper;

    @Autowired
    MtimePromoOrderMapper promoOrderMapper;

    @Autowired
    MtimePromoStockMapper stockMapper;

    /**
     * 查询秒杀信息
     * @param cinemaId
     * @param
     * @return
     */
    @Override
    public List<PromoContainStockVO> selectPromo(Integer cinemaId, Integer promoId) {
        //分次查询
        //先联立查询秒杀表和库存表
        List<PromoContainStockVO> promos = mtimePromoMapper.selectPromo(cinemaId, promoId);
        if (CollectionUtils.isEmpty(promos)) return null;
        return promos;
    }

    /**
     * 创建订单
     * @param userId
     * @param promo
     * @param amount
     * @return
     */
    @Override
    public int createOrder(Integer userId, PromoContainStockVO promo, Integer amount) {
        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        BeanUtils.copyProperties(promo, mtimePromoOrder);
        String uuid = UUID.randomUUID().toString();
        String exchangeCode = UUID.randomUUID().toString();
        BigDecimal price = promo.getPrice().multiply(BigDecimal.valueOf(amount));
        Date time = new Date();
        mtimePromoOrder.setUuid(uuid);
        mtimePromoOrder.setUserId(userId);
        mtimePromoOrder.setExchangeCode(exchangeCode);
        mtimePromoOrder.setPrice(price);
        mtimePromoOrder.setAmount(amount);
        mtimePromoOrder.setCreateTime(time);

        //插入数据库
        Integer insert = promoOrderMapper.insert(mtimePromoOrder);
        if (insert == 0) throw new IllegalArgumentException();
        return insert;
    }

    /**
     * 更新库存
     * @param promoId
     * @param amount
     * @return
     */
    @Override
    public int updateStock(Integer promoId, Integer amount) {
        MtimePromoStock stock = new MtimePromoStock();
        stock.setStock(amount);
        EntityWrapper<MtimePromoStock> wrapper = new EntityWrapper<>();
        wrapper.eq("promo_id", promoId);
        Integer update = stockMapper.update(stock, wrapper);
        if (update == 0) throw new IllegalArgumentException();
        return update;
    }


}
