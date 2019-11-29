package com.guns.service.film;

import com.guns.vo.film.YearInfo;

import java.util.List;

public interface YearService {
    List<YearInfo> selectAllYear(Integer yearId);
}
