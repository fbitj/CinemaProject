package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeAreaDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeBrandDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //tf
    @Autowired
    private MtimeBrandDictTMapper brandDictTMapper;
    @Autowired
    private MtimeAreaDictTMapper areaDictTMapper;
    @Autowired
    private MtimeHallDictTMapper hallDictTMapper;

    @Override
    public List getAddressPrice(Integer brandId) {
        EntityWrapper<MtimeCinemaT> entityWrapper = new EntityWrapper<>();
        if(brandId == 99) {
            entityWrapper.isNotNull("area_id");
            List<MtimeCinemaT> selectList = cinemaTMapper.selectList(entityWrapper);
            return selectList;
        }
        entityWrapper.eq("area_id", brandId);
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

    //tf
    @Override
    /**
     * 获取影院列表查询条件
     * Integer brandId, Integer hallType, Integer areaId，为active状态。选定值为true，其他为false
     */
    public Map<String, Object> getCinema(Integer brandId, Integer hallType, Integer areaId) {

        List<MtimeBrandDictT> brands = brandDictTMapper.selectList(null);
        List<BrandResp> brandList = packageBrand(brands, brandId);
        List<MtimeAreaDictT> areas = areaDictTMapper.selectList(null);
        List<AreaResp> areaList = packageArea(areas, areaId);
        List<MtimeHallDictT> halltypes = hallDictTMapper.selectList(null);
        List<HallTypeResp> halltypeList = packageHallType(halltypes, hallType);
        Map<String ,Object> map = new HashMap<>();
        map.put("brandList", brandList);
        map.put("areaList", areaList);
        map.put("halltypeList", halltypeList);
        return map;
    }

    /**
     * 封装brand
     * @param brandList
     * @param brandId
     * @return
     */
    private List<BrandResp> packageBrand(List<MtimeBrandDictT> brandList, Integer brandId) {
        ArrayList<BrandResp> brandResps = new ArrayList<>();
        for (MtimeBrandDictT brand : brandList) {
            BrandResp brandResp = new BrandResp();
            if(brand.getUuid() != brandId) {
                brandResp.setBrandId(brand.getUuid());
                brandResp.setBrandName(brand.getShowName());
                brandResp.setActive(false);
            } else {
                brandResp.setBrandId(brand.getUuid());
                brandResp.setBrandName(brand.getShowName());
                brandResp.setActive(true);
            }
            brandResps.add(brandResp);
        }
        return brandResps;
    }

    /**
     * 封装halltype
     * @param halltypeList
     * @param hallType
     * @return
     */
    private List<HallTypeResp> packageHallType(List<MtimeHallDictT> halltypeList, Integer hallType) {
        ArrayList<HallTypeResp> hallTypeResps = new ArrayList<>();
        for (MtimeHallDictT hall : halltypeList) {
            HallTypeResp hallTypeResp = new HallTypeResp();
            if(hall.getUuid() != hallType) {
                hallTypeResp.setHalltypeId(hall.getUuid());
                hallTypeResp.setHalltypeName(hall.getShowName());
                hallTypeResp.setActive(false);
            } else {
                hallTypeResp.setHalltypeId(hall.getUuid());
                hallTypeResp.setHalltypeName(hall.getShowName());
                hallTypeResp.setActive(true);
            }
            hallTypeResps.add(hallTypeResp);
        }
        return hallTypeResps;
    }

    /**
     * 封装area
     * @param areaList
     * @param areaId
     * @return
     */
    private List<AreaResp> packageArea(List<MtimeAreaDictT> areaList, Integer areaId) {
        ArrayList<AreaResp> areaResps = new ArrayList<>();
        for (MtimeAreaDictT area : areaList) {
            AreaResp areaResp = new AreaResp();
            if(area.getUuid() != areaId) {
                areaResp.setAreaId(area.getUuid());
                areaResp.setAreaName(area.getShowName());
                areaResp.setActive(false);
            } else {
                areaResp.setAreaId(area.getUuid());
                areaResp.setAreaName(area.getShowName());
                areaResp.setActive(true);
            }
            areaResps.add(areaResp);
        }
        return areaResps;
    }
}
