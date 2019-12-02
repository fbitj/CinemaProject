package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.guns.service.film.YearService;
import com.guns.vo.film.YearInfo;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeYearDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeYearDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = YearService.class)
public class YearServiceImpl implements YearService{

    @Autowired
    MtimeYearDictTMapper yearDictTMapper;

    /**
     * 获取所有的年份信息
     * @param yearId
     * @return
     */
    @Override
    public List<YearInfo> selectAllYear(Integer yearId) {
        List<MtimeYearDictT> mtimeYearDictTS = yearDictTMapper.selectList(null);
        List<YearInfo> yearList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(mtimeYearDictTS)) {
            for (MtimeYearDictT mtimeYearDictT : mtimeYearDictTS) {
                YearInfo year = new YearInfo();
                String showName = mtimeYearDictT.getShowName();
                Integer uuid = mtimeYearDictT.getUuid();

                if (yearId == uuid) {
                    year.setActive(true);
                }

                year.setYearId(uuid.toString());
                year.setYearName(showName);
                yearList.add(year);
            }
        }
        return yearList;
    }
}
