package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.guns.service.film.SourceService;
import com.guns.vo.film.SourceInfoVO;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeSourceDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeSourceDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = SourceService.class)
public class SourceServiceImpl implements SourceService{

    @Autowired
    MtimeSourceDictTMapper sourceDictTMapper;

    /**
     * 查找所有地区
     * @param sourceId
     * @return
     */
    @Override
    public List<SourceInfoVO> selectAllSource(Integer sourceId) {
        List<MtimeSourceDictT> mtimeSourceDictTS = sourceDictTMapper.selectList(null);
        List<SourceInfoVO> sourceList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(mtimeSourceDictTS)) {
            for (MtimeSourceDictT mtimeSourceDictT : mtimeSourceDictTS) {
                SourceInfoVO source = new SourceInfoVO();
                String showName = mtimeSourceDictT.getShowName();
                Integer uuid = mtimeSourceDictT.getUuid();

                if (sourceId == uuid) {
                    source.setActive(true);
                }

                source.setSourceId(uuid.toString());
                source.setSourceName(showName);
                sourceList.add(source);
            }
        }
        return sourceList;
    }
}
