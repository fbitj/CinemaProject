package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.dto.FilmsDTO;
import com.guns.service.film.FilmService;
import com.guns.vo.*;
import com.guns.vo.film.*;
import com.stylefeng.guns.rest.common.exception.FilmException;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.utils.FilmItemVOBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fwj on 2019-11-27.
 */
@org.springframework.stereotype.Service
@Service(interfaceClass = FilmService.class)
public class MtimeFilmServiceImpl implements FilmService {
    @Autowired
    private MtimeFilmTMapper mtimeFilmTMapper;
    @Autowired
    private MtimeFilmInfoTMapper mtimeFilmInfoTMapper;
    @Autowired
    private MtimeHallFilmInfoTMapper mtimeHallFilmInfoTMapper;
    @Autowired
    private MtimeSourceDictTMapper mtimeSourceDictTMapper;
    @Autowired
    private MtimeActorTMapper actorTMapper;
    @Autowired
    private MtimeFilmActorTMapper filmActorTMapper;

    @Override
    public FilmListVO getFilmsByConditions(FilmsDTO filmsDTO) {
        /*List<FilmInfoVO> filmInfoVOS = mtimeFilmTMapper.filmInfoVOB(
                filmsDTO.getShowType(),
                filmsDTO.getSortId(),
                filmsDTO.getCatId(),
                filmsDTO.getSourceId(),
                filmsDTO.getYearId());*/

        EntityWrapper<MtimeFilmT> mtimeFilmTEntityWrapper = new EntityWrapper<>();
        mtimeFilmTEntityWrapper.eq("film_status", filmsDTO.getShowType());
        if (filmsDTO.getCatId() != 99){
            mtimeFilmTEntityWrapper.like("film_cats", "#" + filmsDTO.getCatId() + "#");
        }
        if (filmsDTO.getSourceId() != 99){
            mtimeFilmTEntityWrapper.eq("film_area", filmsDTO.getSourceId());
        }
        if (filmsDTO.getYearId() != 99){
            mtimeFilmTEntityWrapper.eq("film_date", filmsDTO.getYearId());
        }
        //排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
        if (filmsDTO.getSortId() == 1) {
            mtimeFilmTEntityWrapper.orderBy("film_preSaleNum", true);
        } else if (filmsDTO.getSortId() == 2) {
            mtimeFilmTEntityWrapper.orderBy("film_time", true);
        } else {
            mtimeFilmTEntityWrapper.orderBy("film_score", true);
        }

        List<MtimeFilmT> films = mtimeFilmTMapper.selectList(mtimeFilmTEntityWrapper);
        // 数据封装
        FilmListVO target = new FilmListVO<>();
        //===========待修改===========
        target.setNowPage(filmsDTO.getNowPage());
        target.setImgPre("http://img.meetingshop.cn/");
        //===========待修改===========
        target.setTotalPage(3);
        target.setStatus(0);
        List<FilmInfoVO> filmInfoVOS = new ArrayList<>();
        for (MtimeFilmT film : films) {
            //通过filmID查询imgAddress
            FilmInfoVO filmInfoVO = new FilmInfoVO();

            EntityWrapper<MtimeHallFilmInfoT> wrapper = new EntityWrapper<>();
            wrapper.eq("film_id", film.getUuid());
            List<MtimeHallFilmInfoT> mtimeHallFilmInfoTS = mtimeHallFilmInfoTMapper.selectList(wrapper);
            if (mtimeHallFilmInfoTS.size() > 0){
                filmInfoVO.setImgAddress(mtimeHallFilmInfoTS.get(0).getImgAddress());
            }
            filmInfoVO.setFilmId(String.valueOf(film.getUuid()));
            filmInfoVO.setFilmName(film.getFilmName());
            filmInfoVO.setFilmType(film.getFilmType());
            filmInfoVO.setFilmScore(film.getFilmScore());

            filmInfoVOS.add(filmInfoVO);
        }
        target.setData(filmInfoVOS);
        return target;
    }


    /**
     * 查询电影详情
     *
     * @return
     */
    public FilmItemVO getFilmDetail(Integer filmId) {
        MtimeFilmInfoT mtimeFilmInfoT = null;
        MtimeFilmT film = null;
        DirictorVO director = null;
        MtimeHallFilmInfoT hallFilmInfoT = null;
        List<ActorVO> actors = null;
        String showName = null;
        String date = null;
        try {
            // 1.通过影片id查询filmInfo表
            mtimeFilmInfoT = selectFIlmInfoByFilmId(filmId);
            // 2.通过影片id查询film表
            film = mtimeFilmTMapper.selectById(filmId);
            // 查询导演
            director = selectDirectorByActorId(mtimeFilmInfoT.getDirectorId());
            // 通过filmid查询mtime_hall_film_info_t表
            hallFilmInfoT = selectHallFilmInfoByFilmId(filmId);
            // 通过影片id演员：mtime_film_actor_t mtime_actor_t
            // 通过演员id查询演员信息
            actors = selectActorsByActorNames(filmId);
            // 通过影片区域id查询片源
            showName = selectFilmSourceByFilmId(film.getFilmSource());
            // 日期格式转化
            Date filmTime = film.getFilmTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            date = simpleDateFormat.format(filmTime);
        } catch (Exception e) {
            throw new FilmException();
        }
        FilmItemVOBuilder filmItemVOBuilder = new FilmItemVOBuilder();
        FilmItemVO filmItemVO = filmItemVOBuilder.addFilm(film)
                .addFilmInfo(mtimeFilmInfoT)
                .addHallFilmInfo(hallFilmInfoT)
                .addActors(actors)
                .addDirictor(director)
                .addFilmSource(showName)
                .addImgpro("http://img.meetingshop.cn/")// ==========暂时固定返回值
                .addStatus(0).builder();
        return filmItemVO;
    }



    /**
     * 根据影片状态查找影片
     * @param status
     * @return
     */
    @Override
    public List<FilmInfoVO> queryFilmByStatus(int status) {
        EntityWrapper<MtimeFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", status);
        List<MtimeFilmT> mtimeFilmTList = mtimeFilmTMapper.selectList(wrapper);

        //封装
        List<FilmInfoVO> filmInfoVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mtimeFilmTList)){
            if (mtimeFilmTList.size() > 8) {
                //限制返回条目数
                mtimeFilmTList = mtimeFilmTList.subList(0,8);
            }
            for (MtimeFilmT mtimeFilmT : mtimeFilmTList) {
                FilmInfoVO filmInfoVO = new FilmInfoVO();
                BeanUtils.copyProperties(mtimeFilmT,filmInfoVO);
                filmInfoVO.setFilmId(mtimeFilmT.getUuid().toString());
                Date filmTime = mtimeFilmT.getFilmTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                filmInfoVO.setShowTime(simpleDateFormat.format(filmTime));
                filmInfoVOS.add(filmInfoVO);
            }
        }
        return filmInfoVOS;
    }

    /**
     * 查询所有影片按相关参数排序
     * @param column
     * @return
     */
    @Override
    public List<FilmInfoVO> queryFilmByColumnDesc(String column) {
        EntityWrapper<MtimeFilmT> wrapper = new EntityWrapper<>();
        wrapper.orderBy(column, false);

        List<MtimeFilmT> mtimeFilmTS = mtimeFilmTMapper.selectList(wrapper);

        List<FilmInfoVO> filmInfoVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mtimeFilmTS)) {
            if (mtimeFilmTS.size() > 10) {
                //显示返回条目数
                mtimeFilmTS = mtimeFilmTS.subList(0,10);
            }
            for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
                FilmInfoVO filmInfoVO = new FilmInfoVO();
                BeanUtils.copyProperties(mtimeFilmT,filmInfoVO);
                filmInfoVO.setFilmId(mtimeFilmT.getUuid().toString());
                filmInfoVO.setBoxNum(mtimeFilmT.getFilmBoxOffice());
                filmInfoVO.setExpectNum(mtimeFilmT.getFilmPresalenum());
                filmInfoVO.setScore(mtimeFilmT.getFilmScore());
                filmInfoVOS.add(filmInfoVO);
            }
        }
        return filmInfoVOS;
    }



    private DirictorVO selectDirectorByActorId(Integer directorId) {
        MtimeActorT actorT = actorTMapper.selectById(directorId);
        DirictorVO target = new DirictorVO();
        target.setImgAddress(actorT.getActorImg());
        target.setDirectorName(actorT.getActorName());
        return target;
    }


    private List<ActorVO> selectActorsByActorNames(Integer filmId) {
        return filmActorTMapper.selectActorsByFilmId(filmId);
    }


    private String selectFilmSourceByFilmId(Integer sourceid) {
        EntityWrapper<MtimeSourceDictT> wrapper = new EntityWrapper<>();
        wrapper.eq("uuid", sourceid);
        List<MtimeSourceDictT> mtimeSourceDictTS = mtimeSourceDictTMapper.selectList(wrapper);
        String showName = mtimeSourceDictTS.get(0).getShowName();
        return showName;
    }

    private MtimeFilmInfoT selectFIlmInfoByFilmId(Integer filmId) {
        EntityWrapper<MtimeFilmInfoT> objectEntityWrapper = new EntityWrapper<>();
        objectEntityWrapper.eq("film_id", filmId);
        MtimeFilmInfoT mtimeFilmInfoT = mtimeFilmInfoTMapper.selectList(objectEntityWrapper).get(0);
        return mtimeFilmInfoT;
    }

    private MtimeHallFilmInfoT selectHallFilmInfoByFilmId(Integer filmId) {
        EntityWrapper<MtimeHallFilmInfoT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_id", filmId);
        List<MtimeHallFilmInfoT> mtimeHallFilmInfoTS = mtimeHallFilmInfoTMapper.selectList(wrapper);
        if (mtimeHallFilmInfoTS.size() == 0){
            return null;
        }
        MtimeHallFilmInfoT mtimeHallFilmInfoT = mtimeHallFilmInfoTS.get(0);
        return mtimeHallFilmInfoT;
    }


}
