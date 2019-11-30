package com.stylefeng.guns.rest.utils;

import com.guns.vo.film.ImgVO;

import java.util.HashMap;

import com.guns.vo.Info04;

import com.guns.vo.film.ActorVO;
import com.guns.vo.film.DirictorVO;
import com.guns.vo.film.FilmItemInfoVO;
import com.guns.vo.film.FilmItemVO;
import com.stylefeng.guns.rest.common.exception.FilmException;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmInfoT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFilmT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallFilmInfoT;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class FilmItemVOBuilder {
    FilmItemVO<FilmItemInfoVO> filmItemVO = new FilmItemVO<>();
    // 不能为空
    MtimeFilmT film;
    String filmSource;
    DirictorVO director;
    List<ActorVO> actors;
    // 不能为空
    MtimeFilmInfoT mtimeFilmInfoT;
    MtimeHallFilmInfoT hallFilmInfoT;

    private FilmItemVO createFilmItemVO() {
        filmItemVO.setData(createFilmItemInfoVO());
        return filmItemVO;
    }

    private FilmItemInfoVO createFilmItemInfoVO() {
        // 日期格式化
        String date = formatDate(film.getFilmTime());
        FilmItemInfoVO filmItemInfoVO = new FilmItemInfoVO();
        // 封装数据
        filmItemInfoVO.setFilmName(film.getFilmName());
        filmItemInfoVO.setFilmEnName(mtimeFilmInfoT.getFilmEnName());
        filmItemInfoVO.setFilmId(film.getUuid());
        filmItemInfoVO.setImgAddress(hallFilmInfoT.getImgAddress());
        filmItemInfoVO.setInfo01(hallFilmInfoT.getFilmCats());
        filmItemInfoVO.setInfo02(filmSource + " / " + mtimeFilmInfoT.getFilmLength() + "分钟");
        filmItemInfoVO.setInfo03(date + " / " + filmSource + "上映");
        filmItemInfoVO.setInfo04(createInfo04());
        filmItemInfoVO.setScore(mtimeFilmInfoT.getFilmScore());
        filmItemInfoVO.setScoreNum(mtimeFilmInfoT.getFilmScoreNum());
        filmItemInfoVO.setTotalBox(film.getFilmBoxOffice());
        return filmItemInfoVO;
    }

    private String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(date);
    }

    private Info04 createInfo04() {
        Info04 info04 = new Info04();
        info04.setBiography(mtimeFilmInfoT.getBiography());
        info04.setFilmId(0);
        info04.setActors(createActors());
        info04.setImgVO(createImgVO());
        return info04;
    }

    private Map<String, Object> createActors() {
        HashMap<String, Object> person = new HashMap<>();
        person.put("actors", actors);
        person.put("director", director);
        return person;
    }

    private ImgVO createImgVO() {
        ImgVO imgVO = new ImgVO();
        String filmImgs = mtimeFilmInfoT.getFilmImgs();
        if (filmImgs == null || "".equals("filmImgs")) {
            return imgVO;
        }
        String[] imgs = filmImgs.split(",");
        switch (imgs.length) {
            default:
            case 5:
                imgVO.setImg04(imgs[4]);
            case 4:
                imgVO.setImg03(imgs[3]);
            case 3:
                imgVO.setImg02(imgs[2]);
            case 2:
                imgVO.setImg01(imgs[1]);
            case 1:
                imgVO.setMainImg(imgs[0]);
                break;
        }
        return imgVO;
    }


    public FilmItemVOBuilder addImgpro(String imgPro) {
        filmItemVO.setImgPre(imgPro);
        return this;
    }

    public FilmItemVOBuilder addStatus(Integer status) {
        filmItemVO.setStatus(status);
        return this;
    }

    public FilmItemVOBuilder addFilmInfo(MtimeFilmInfoT mtimeFilmInfoT) {
        if (mtimeFilmInfoT == null){
            throw new FilmException("mtimeFilmInfoT不能为null");
        }
        this.mtimeFilmInfoT = mtimeFilmInfoT;
        return this;
    }

    public FilmItemVOBuilder addFilm(MtimeFilmT film) {
        if (film == null){
            throw new FilmException("film不能为null");
        }
        this.film = film;
        return this;
    }

    public FilmItemVOBuilder addDirictor(DirictorVO director) {
        this.director = director;
        return this;
    }

    public FilmItemVOBuilder addHallFilmInfo(MtimeHallFilmInfoT hallFilmInfoT) {
        if (hallFilmInfoT == null) {
            throw new FilmException("hallFilmInfoT不能为null");
        }
        this.hallFilmInfoT = hallFilmInfoT;
        return this;
    }

    public FilmItemVOBuilder addActors(List<ActorVO> actors) {
        this.actors = actors;
        return this;
    }

    public FilmItemVOBuilder addFilmSource(String filmSource) {
        this.filmSource = filmSource;
        return this;
    }


    /**
     * 构建一个FilmItemVO对象
     *
     * @return
     */
    public FilmItemVO builder() {
        createFilmItemVO();
        return filmItemVO;
    }
}
