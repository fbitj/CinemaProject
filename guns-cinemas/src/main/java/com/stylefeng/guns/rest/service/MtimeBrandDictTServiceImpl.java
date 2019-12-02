package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.guns.service.cinema.IMtimeBrandDictTService;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.utils.String2Array;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeBrandDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 品牌信息表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
@Component
@Service(interfaceClass = IMtimeBrandDictTService.class)
public class MtimeBrandDictTServiceImpl implements IMtimeBrandDictTService , Serializable {


    @Autowired
    MtimeBrandDictTMapper brandDictTMapper;
    @Autowired
    IMtimeCinemaTService cinemaTService;
    @Autowired


//    @Override
    public List getCinemaNames() {
        int brandId = 9;
        if(brandId == 99){
            Wrapper<MtimeBrandDictT> wrapper = new EntityWrapper<>();
            wrapper.isNotNull("uuid");
            List<MtimeBrandDictT> mtimeBrandDictTS = brandDictTMapper.selectList(wrapper);
            return mtimeBrandDictTS;
        }
        EntityWrapper<MtimeBrandDictT> wrapper = new EntityWrapper<>();
        wrapper.eq("uuid",brandId);
        List<MtimeBrandDictT> mtimeBrandDictTS = brandDictTMapper.selectList(wrapper);
        return mtimeBrandDictTS;
    }


//    @Override
    public List getCN(Integer brandId) {
        if(brandId == 99){
            Wrapper<MtimeBrandDictT> wrapper = new EntityWrapper<>();
            wrapper.isNotNull("uuid");
            List<MtimeBrandDictT> mtimeBrandDictTS = brandDictTMapper.selectList(wrapper);
            return mtimeBrandDictTS;
        }
        EntityWrapper<MtimeBrandDictT> wrapper = new EntityWrapper<>();
        wrapper.eq("uuid",brandId);
        List<MtimeBrandDictT> mtimeBrandDictTS = brandDictTMapper.selectList(wrapper);
        return mtimeBrandDictTS;
    }


    @Override
    public  List getCinemas(Integer brandId, Integer districtId, Integer hallType){
        ArrayList<Object> list = new ArrayList<>();
        PageHelper pageHelper = new PageHelper();
        List<MtimeBrandDictT> cinemaName = this.getCN(brandId);

        List<MtimeCinemaT> addressPrice = cinemaTService.getAddressPrice(districtId);
        if(cinemaName.size() == 0 || addressPrice.size() == 0){
            return new ArrayList();
        }
        for (MtimeBrandDictT brandDictT : cinemaName) {
            for (MtimeCinemaT cinemaT : addressPrice) {
                if(hallType != null && hallType != 99){
                    ArrayList<Integer> integers = String2Array.string2Array(cinemaT.getHallIds());
                    for (Integer integer : integers) {
                        if(integer == hallType){
                            if(cinemaT.getBrandId() == brandDictT.getUuid()){
                                HashMap<Object, Object> map = new HashMap<>();
                                String showName = cinemaT.getCinemaName();
                                Integer uuid = brandDictT.getUuid();
                                map.put("uuid",uuid);
                                map.put("cinemaName",showName);
                                map.put("cinemaAddress",cinemaT.getCinemaAddress());
                                map.put("minimumPrice",cinemaT.getMinimumPrice());
                                list.add(map);
                            }
                        }
                    }
                }else if(hallType != null) {
                    if (cinemaT.getBrandId() == brandDictT.getUuid()) {
                        HashMap<Object, Object> map = new HashMap<>();
                        String showName = cinemaT.getCinemaName();
                        Integer uuid = brandDictT.getUuid();
                        map.put("uuid", uuid);
                        map.put("cinemaName", showName);
                        map.put("cinemaAddress", cinemaT.getCinemaAddress());
                        map.put("minimumPrice", cinemaT.getMinimumPrice());
                        list.add(map);
                    }
                }
            }
        }
        return list;
    }

}
