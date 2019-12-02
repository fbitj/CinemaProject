package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.vo.cinema.AreaVO;
import com.guns.vo.cinema.BrandVO;
import com.guns.vo.cinema.HallTypeVO;
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
        List<BrandVO> brandList = packageBrand(brands, brandId);
        List<MtimeAreaDictT> areas = areaDictTMapper.selectList(null);
        List<AreaVO> areaList = packageArea(areas, areaId);
        List<MtimeHallDictT> halltypes = hallDictTMapper.selectList(null);
        List<HallTypeVO> halltypeList = packageHallType(halltypes, hallType);
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
    private List<BrandVO> packageBrand(List<MtimeBrandDictT> brandList, Integer brandId) {
        ArrayList<BrandVO> brandVOS = new ArrayList<>();
        for (MtimeBrandDictT brand : brandList) {
            BrandVO brandVO = new BrandVO();
            if(brand.getUuid() != brandId) {
                brandVO.setBrandId(brand.getUuid());
                brandVO.setBrandName(brand.getShowName());
                brandVO.setActive(false);
            } else {
                brandVO.setBrandId(brand.getUuid());
                brandVO.setBrandName(brand.getShowName());
                brandVO.setActive(true);
            }
            brandVOS.add(brandVO);
        }
        return brandVOS;
    }

    /**
     * 封装halltype
     * @param halltypeList
     * @param hallType
     * @return
     */
    private List<HallTypeVO> packageHallType(List<MtimeHallDictT> halltypeList, Integer hallType) {
        ArrayList<HallTypeVO> hallTypeVOS = new ArrayList<>();
        for (MtimeHallDictT hall : halltypeList) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            if(hall.getUuid() != hallType) {
                hallTypeVO.setHalltypeId(hall.getUuid());
                hallTypeVO.setHalltypeName(hall.getShowName());
                hallTypeVO.setActive(false);
            } else {
                hallTypeVO.setHalltypeId(hall.getUuid());
                hallTypeVO.setHalltypeName(hall.getShowName());
                hallTypeVO.setActive(true);
            }
            hallTypeVOS.add(hallTypeVO);
        }
        return hallTypeVOS;
    }

    /**
     * 封装area
     * @param areaList
     * @param areaId
     * @return
     */
    private List<AreaVO> packageArea(List<MtimeAreaDictT> areaList, Integer areaId) {
        ArrayList<AreaVO> areaVOS = new ArrayList<>();
        for (MtimeAreaDictT area : areaList) {
            AreaVO areaVO = new AreaVO();
            if(area.getUuid() != areaId) {
                areaVO.setAreaId(area.getUuid());
                areaVO.setAreaName(area.getShowName());
                areaVO.setActive(false);
            } else {
                areaVO.setAreaId(area.getUuid());
                areaVO.setAreaName(area.getShowName());
                areaVO.setActive(true);
            }
            areaVOS.add(areaVO);
        }
        return areaVOS;
    }
}
