package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.guns.service.film.CatService;
import com.guns.vo.film.CatInfoVO;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCatDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCatDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CatService.class)
public class CatServiceImpl implements CatService{

    @Autowired
    MtimeCatDictTMapper catDictTMapper;

    /**
     * 查询所有类型信息
     * @param catId
     * @return
     */
    @Override
    public List<CatInfoVO> selectAllCat(Integer catId) {
        List<MtimeCatDictT> mtimeCatDictTS = catDictTMapper.selectList(null);
        List<CatInfoVO> catInfoVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mtimeCatDictTS)) {
            for (MtimeCatDictT mtimeCatDictT : mtimeCatDictTS) {
                CatInfoVO catInfoVO = new CatInfoVO();
                Integer uuid = mtimeCatDictT.getUuid();
                String showName = mtimeCatDictT.getShowName();
                if (catId == uuid) {
                    catInfoVO.setActive(true);
                }
                catInfoVO.setCatId(uuid.toString());
                catInfoVO.setCatName(showName);
                catInfoVOS.add(catInfoVO);
            }
        }
        return catInfoVOS;
    }
}
