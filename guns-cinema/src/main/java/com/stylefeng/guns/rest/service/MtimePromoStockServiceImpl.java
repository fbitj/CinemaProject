package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.guns.service.cinema.IMtimePromoService;
import com.guns.service.cinema.IMtimePromoStockService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-12-03
 */
@Component
@Service(interfaceClass = IMtimePromoStockService.class)
public class MtimePromoStockServiceImpl implements IMtimePromoStockService {

    @Autowired
    MtimePromoStockMapper promoStockMapper;
    @Autowired
    IMtimePromoService promoService;



    @Override
    public Object getPromoStock(Integer promoId) {
        EntityWrapper<MtimePromoStock> wrapper = new EntityWrapper<>();
        wrapper.eq("promo_id", promoId);
        List<MtimePromoStock> mtimePromoStocks = promoStockMapper.selectList(wrapper);
        MtimePromoStock mtimePromoStock = mtimePromoStocks.get(0);
        return mtimePromoStock;
    }


    @Override
    public Integer updatePromStockByPromoId(Integer promoId, Integer amount) {
        EntityWrapper<MtimePromoStock> wrapper = new EntityWrapper<>();
        MtimePromoStock mtimePromoStock = new MtimePromoStock();
        wrapper.eq("promo_id", promoId);
        mtimePromoStock.setStock(amount);
        Integer update = promoStockMapper.update(mtimePromoStock, wrapper);
        return update;
    }

    @Override
    public List<HashMap<String, Object>> pushPromoStock() {
        List<MtimePromo> promoList = (List<MtimePromo>) promoService.getPromosByStatus();
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (MtimePromo mtimePromo : promoList) {
            HashMap<String, Object> map = new HashMap<>();
            Integer uuid = mtimePromo.getUuid();
            MtimePromoStock promoStock = (MtimePromoStock) this.getPromoStock(uuid);
            Integer stock = promoStock.getStock();
            map.put("promoId", promoStock.getPromoId());
            map.put("stock", stock);
            list.add(map);
        }
        return list;
    }

}
