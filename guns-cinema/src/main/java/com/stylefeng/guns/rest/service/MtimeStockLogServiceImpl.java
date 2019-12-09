package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.guns.service.cinema.IMtimeStockLogService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Service(interfaceClass = IMtimeStockLogService.class)
public class MtimeStockLogServiceImpl implements IMtimeStockLogService {

    @Autowired
    MtimeStockLogMapper stockLogMapper;


    @Override
    public Integer updataStockLogById(String stockLogId, Integer status) {
        EntityWrapper<MtimeStockLog> wrapper = new EntityWrapper<>();
        MtimeStockLog mtimeStockLog = new MtimeStockLog();
        mtimeStockLog.setStatus(status);
        wrapper.eq("UUID", stockLogId);
        Integer update = stockLogMapper.update(mtimeStockLog, wrapper);
        return update;
    }

    @Override
    public String getStockLogId(Integer promoId, Integer amount) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        MtimeStockLog mtimeStockLog = new MtimeStockLog();
        mtimeStockLog.setUuid(uuid);
        mtimeStockLog.setPromoId(promoId);
        mtimeStockLog.setAmount(amount);
        mtimeStockLog.setStatus(1);
        Integer insert = stockLogMapper.insert(mtimeStockLog);
        if(insert > 0){
            return uuid;
        }
        return null;
    }

    @Override
    public Object getStockLogById(String stockId) {
        MtimeStockLog mtimeStockLog = stockLogMapper.selectById(stockId);
        return mtimeStockLog;
    }
}
