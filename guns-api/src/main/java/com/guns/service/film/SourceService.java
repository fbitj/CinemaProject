package com.guns.service.film;

import com.guns.vo.film.SourceInfoVO;

import java.util.List;

public interface SourceService {
    List<SourceInfoVO> selectAllSource(Integer sourceId);

}
