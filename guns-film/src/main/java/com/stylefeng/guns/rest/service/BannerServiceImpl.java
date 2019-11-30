package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.film.BannerService;
import com.guns.vo.film.BannerVO;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeBannerTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeBannerT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = BannerService.class)
public class BannerServiceImpl implements BannerService {

    @Autowired
    MtimeBannerTMapper bannerTMapper;

    /**
     * 查找可用的所有广告
     * @return
     */
    @Override
    public List<BannerVO> queryBannersIsValid() {
        EntityWrapper<MtimeBannerT> wrapper = new EntityWrapper<>();
        wrapper.eq("is_valid", false);
        List<MtimeBannerT> banners = bannerTMapper.selectList(wrapper);

        //封装返回的对象
        List<BannerVO> bannerVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(banners)) {
            for (MtimeBannerT banner : banners) {
                BannerVO bannerVO = new BannerVO();
                /*String bannerAddress = banner.getBannerAddress();
                String[] split = bannerAddress.split("/");
                bannerVO.setBannerAddress(split[1]);*/
                bannerVO.setBannerAddress(banner.getBannerAddress());
                bannerVO.setBannerUrl(banner.getBannerUrl());
                bannerVO.setBannerId(banner.getUuid().toString());
                bannerVOS.add(bannerVO);
            }
        }
        return bannerVOS;
    }
}
