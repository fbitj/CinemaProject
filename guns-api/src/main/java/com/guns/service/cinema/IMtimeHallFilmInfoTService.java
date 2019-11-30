package com.guns.service.cinema;


import java.io.Serializable;

/**
 * <p>
 * 影厅电影信息表 服务类
 * </p>
 *
 * @author Cats
 * @since 2019-11-27
 */
//@Service(interfaceClass = IMtimeHallFilmInfoTService.class)
public interface IMtimeHallFilmInfoTService extends Serializable {

    Object getHallFilm(Integer filmId);
}
