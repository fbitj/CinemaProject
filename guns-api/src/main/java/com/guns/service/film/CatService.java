package com.guns.service.film;

import com.guns.vo.film.CatInfoVO;

import java.util.List;

public interface CatService {
    List<CatInfoVO> selectAllCat(Integer catId);
}
