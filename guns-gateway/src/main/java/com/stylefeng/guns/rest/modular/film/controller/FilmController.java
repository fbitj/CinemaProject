package com.stylefeng.guns.rest.modular.film.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.dto.FilmsDTO;
import com.guns.service.film.*;
import com.guns.vo.BaseRespVO;
import com.guns.vo.FilmListVO;
import com.guns.vo.film.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("film")
public class FilmController {
    @Reference(interfaceClass = BannerService.class, check = false)
    BannerService bannerService;

    @Reference(interfaceClass = CatService.class, check = false)
    CatService catService;

    @Reference(interfaceClass = SourceService.class, check = false)
    SourceService sourceService;

    @Reference(interfaceClass = YearService.class, check = false)
    YearService yearService;

    @Reference(interfaceClass = FilmService.class, check = false)
    private FilmService filmService;

    @RequestMapping("getFilms")
    public FilmListVO getFilmsByConditions(FilmsDTO filmsDTo){
        return filmService.getFilmsByConditions(filmsDTo);
    }

    @RequestMapping("films/{filmId}")
    public FilmItemVO getFilmDetail(@PathVariable("filmId") Integer filmId) {
        return filmService.getFilmDetail(filmId);
    }

    /**
     * 首页显示
     * @return
     */
    @RequestMapping("getIndex")
    public BaseRespVO filmIndex() {
        List<BannerVO> bannerVOList = bannerService.queryBannersIsValid();
        List<FilmInfoVO> hotFilms = filmService.queryFilmByStatus(1);
        List<FilmInfoVO> soonFilms = filmService.queryFilmByStatus(2);
        List<FilmInfoVO> boxFilms = filmService.queryFilmByColumnDesc("film_box_office");
        List<FilmInfoVO> expectFilms = filmService.queryFilmByColumnDesc("film_preSaleNum");
        List<FilmInfoVO> highScoreFilms = filmService.queryFilmByColumnDesc("film_score");

        //封装返回参数
        BaseRespVO baseRespVO = new BaseRespVO();

        HashMap data = new HashMap();
        data.put("banners",bannerVOList);
        data.put("hotFilms", new FilmResultVO(hotFilms.size(), hotFilms));
        data.put("soonFilms", new FilmResultVO(soonFilms.size(), soonFilms));
        data.put("boxRanking", boxFilms);
        data.put("expectRanking", expectFilms);
        data.put("top100", highScoreFilms);

        baseRespVO.setData(data);
        //baseRespVO.setImgPre(bannerVOList.get(0).getBannerUrl());
        baseRespVO.setImgPre("http://img.meetingshop.cn/");
        baseRespVO.setStatus(0);
        return baseRespVO;
    }

    /**
     * 显示类型、区域和年代信息
     * @param catId
     * @param sourceId
     * @param yearId
     * @return
     */
    @RequestMapping("getConditionList")
    public BaseRespVO getConditionList(Integer catId, Integer sourceId, Integer yearId) {
        List<CatInfoVO> catList = catService.selectAllCat(catId);
        List<SourceInfoVO> sourceList = sourceService.selectAllSource(sourceId);
        List<YearInfo> yearList = yearService.selectAllYear(yearId);

        //封装
        Map result = new HashMap();
        result.put("catInfo", catList);
        result.put("sourceInfo", sourceList);
        result.put("yearInfo", yearList);

        return BaseRespVO.ok(result);
    }
}
