package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 影院信息表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
@Component
@Service(interfaceClass = IMtimeCinemaTService.class)
public class MtimeCinemaTServiceImpl implements IMtimeCinemaTService {

    @Autowired
    MtimeCinemaTMapper cinemaTMapper;

    @Override
    public List getAddressPrice(Integer brandId) {
        EntityWrapper<MtimeCinemaT> entityWrapper = new EntityWrapper<>();
        if(brandId == 99) {
            entityWrapper.isNotNull("brand_id");
            List<MtimeCinemaT> selectList = cinemaTMapper.selectList(entityWrapper);
            return selectList;
        }
        entityWrapper.eq("brand_id", brandId);
        List<MtimeCinemaT> selectList = cinemaTMapper.selectList(entityWrapper);
        return selectList;
    }

    @Override
    public Object getCine(Integer uuid) {
        EntityWrapper<MtimeCinemaT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID",uuid);
        List<MtimeCinemaT> mtimeCinemaTS = cinemaTMapper.selectList(wrapper);
        MtimeCinemaT mtimeCinemaT = mtimeCinemaTS.get(0);
        return mtimeCinemaT;
    }
}
