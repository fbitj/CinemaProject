package com.guns.vo.film;

import com.guns.vo.Info04;
import lombok.Data;

import java.io.Serializable;

@Data
public class FilmItemInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer filmId;
    /**
     * 电影名
     */
    private String filmName;
    /**
     * 电影英文名
     */
    private String filmEnName;
    /**
     * 图片地址
     */
    private String imgAddress;
    /**
     * 评分
     */
    private String score;
    /**
     * 评分人数
     */
    private Integer scoreNum;
    /**
     * 票房
     */
    private Integer totalBox;
    /**
     * 影片类型
     */
    private String info01;
    /**
     * 片源 / 时长
     */
    private String info02;
    /**
     * 上映时间 / 片源
     */
    private String info03;
    /**
     * 电影相关介绍
     */
    private Info04 info04;

    }
