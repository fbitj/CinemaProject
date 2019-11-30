package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.service.cinema.IMtimeFieldTService;
import com.guns.service.cinema.IMtimeHallDictTService;
import com.guns.service.cinema.IMtimeHallFilmInfoTService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallFilmInfoTMapper;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 放映场次表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
@Component
@Service(interfaceClass = IMtimeFieldTService.class)
public class MtimeFieldTServiceImpl implements IMtimeFieldTService {

    @Autowired
    MtimeFieldTMapper fieldTMapper;
    @Autowired
    IMtimeHallFilmInfoTService infoTService;
    @Autowired
    IMtimeCinemaTService cinemaTService;
    @Autowired
    IMtimeHallDictTService hallDictTService;

    //tf
    @Autowired
    MtimeCinemaTMapper cinemaTMapper;
    @Autowired
    MtimeHallFilmInfoTMapper hallFilmInfoTMapper;
  /*  @Autowired
    MtimeFieldTMapper fieldTMapper;*/

    @Override
    public Object getFieldMessage(Integer cinemaId, Integer fieldId) {
        HashMap<Object, Object> map = new HashMap<>();
        EntityWrapper<MtimeFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", fieldId);
        List<MtimeFieldT> mtimeFieldTS = fieldTMapper.selectList(wrapper);
        MtimeFieldT mtimeFieldT = mtimeFieldTS.get(0);

        List hallFilm = (List) infoTService.getHallFilm(mtimeFieldT.getFilmId());
        MtimeHallFilmInfoT o = (MtimeHallFilmInfoT) hallFilm.get(0);
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("filmId",o.getFilmId());
        map1.put("filmName",o.getFilmName());
        map1.put("imgAddress",o.getImgAddress());
        map1.put("filmCats",o.getFilmCats());
        map1.put("filmLength",Integer.parseInt(o.getFilmLength().trim()));
        map.put("filmInfo",map1);

        EntityWrapper<MtimeCinemaT> wrapper1 = new EntityWrapper<>();
        MtimeCinemaT cine = (MtimeCinemaT) cinemaTService.getCine(mtimeFieldT.getCinemaId());
        HashMap<Object, Object> map2 = new HashMap<>();
        map2.put("cinemaId",cine.getUuid());
        map2.put("imgUrl",cine.getImgAddress());
        map2.put("cinemaName",cine.getCinemaName());
        map2.put("cinemaAdress",cine.getCinemaAddress());
        map2.put("cinemaPhone",cine.getCinemaPhone());
        map.put("cinemaInfo",map2);

        HashMap<Object, Object> map3 = new HashMap<>();
        map3.put("hallFieldId",mtimeFieldT.getHallId());
        map3.put("hallName",mtimeFieldT.getHallName());
        map3.put("price",mtimeFieldT.getPrice());
        MtimeHallDictT gethall = (MtimeHallDictT) hallDictTService.gethall(mtimeFieldT.getHallId());
        map3.put("seatFile",gethall.getSeatAddress());
        map3.put("soldSeats","");//已购票座位,  完成订单后实现
        map.put("hallInfo",map3);
        return map;
    }

    //tf
    /**
     * 获取播放场次接口
     * @param cinemaId
     * @return
     */
    @Override
    public Map<String, Object> selectField(Integer cinemaId) {
        //查影院信息
        MtimeCinemaT cinemaT = cinemaTMapper.selectById(cinemaId);
        CinemaInfo cinemaInfo = packageCinema(cinemaT);
        //查场次信息
        EntityWrapper<MtimeFieldT> fieldTWrapper = new EntityWrapper<>();
        fieldTWrapper.eq("cinema_id", cinemaId);
        List<MtimeFieldT> fieldTS = fieldTMapper.selectList(fieldTWrapper);
        //查询电影信息
        List<FilmResp> filmList = packageFilm(fieldTS);
        Map<String, Object> map = new HashMap<>();
        map.put("cinemaInfo", cinemaInfo);
        map.put("filmList", filmList);
        return map;
    }

    /**
     * 封装cinema信息
     * @param cinemaT
     * @return
     */
    private  CinemaInfo packageCinema( MtimeCinemaT cinemaT) {
        CinemaInfo cinemaInfo = new CinemaInfo();
        cinemaInfo.setCinemaId(cinemaT.getUuid());
        cinemaInfo.setCinemaName(cinemaT.getCinemaName());
        cinemaInfo.setCinemaAdress(cinemaT.getCinemaAddress());
        cinemaInfo.setCinemaPhone(cinemaT.getCinemaPhone());
        cinemaInfo.setImgUrl(cinemaT.getImgAddress());
        return cinemaInfo;
    }

    /**
     * 封装场次信息
     * @param fieldTS
     * @return
     */
    private List<FilmField> packageFilmField(List<MtimeFieldT> fieldTS, String language, Integer filmId) {
        ArrayList<FilmField> filmFields = new ArrayList<>();
        for (MtimeFieldT fieldT : fieldTS) {
            //根据film_id封装对应电影的场次
            if(filmId != fieldT.getFilmId()) {
                continue;
            }
            FilmField filmField = new FilmField();
            filmField.setFieldId(fieldT.getUuid());
            filmField.setHallName(fieldT.getHallName());
            filmField.setBeginTime(fieldT.getBeginTime());
            filmField.setEndTime(fieldT.getEndTime());
            //film语言在电影信息表里
            filmField.setLanguage(language);
            filmField.setPrice(String.valueOf(fieldT.getPrice()));
            filmFields.add(filmField);
        }
        return filmFields;
    }

    /**
     * 封装电影基本信息，放映场次信息
     * 注意，同一部电影只封装一次，场次全部封装
     * @param fieldTS
     * @return
     */
    private List<FilmResp> packageFilm(List<MtimeFieldT> fieldTS) {
        ArrayList<FilmResp> filmResps = new ArrayList<>();
        //判断电影是否封装过
        Integer filmId = 0;
        for (MtimeFieldT fieldT : fieldTS) {
            EntityWrapper<MtimeHallFilmInfoT> wrapper = new EntityWrapper<>();
            wrapper.eq("film_id", fieldT.getFilmId());
            List<MtimeHallFilmInfoT> filmInfoTS = hallFilmInfoTMapper.selectList(wrapper);
            for (MtimeHallFilmInfoT filmInfoT : filmInfoTS) {
                if(filmId == filmInfoT.getFilmId()) {
                    //遇到封装过的电影，跳出本次循环
                    continue;
                }
                FilmResp filmResp = new FilmResp();
                filmResp.setFilmId(filmInfoT.getFilmId());
                filmResp.setActors(filmInfoT.getActors());
                filmResp.setFilmCats(filmInfoT.getFilmCats());
                filmResp.setFilmLength(filmInfoT.getFilmLength());
                filmResp.setFilmName(filmInfoT.getFilmName());
                //filmType就是语言language
                filmResp.setFilmType(filmInfoT.getFilmLanguage());
                filmResp.setImgAddress(filmInfoT.getImgAddress());
                //封装LIst<FilmField>
                //根据film_id封装对应电影的场次
                List<FilmField> filmFields = packageFilmField(fieldTS, filmInfoT.getFilmLanguage(), filmInfoT.getFilmId());
                filmResp.setFilmFields(filmFields);
                filmResps.add(filmResp);
                //把本次封装的电影id赋给叛别变量
                filmId = filmInfoT.getFilmId();
            }
        }
        return filmResps;
    }
}
