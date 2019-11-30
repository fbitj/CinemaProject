package com.stylefeng.guns.rest.modular.cinema.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.cinema.IMtimeBrandDictTService;
import com.guns.service.cinema.IMtimeCinemaTService;
import com.guns.service.cinema.IMtimeFieldTService;
import com.guns.vo.UserCacheVO;
import com.guns.vo.cinema.GetCinemasVo;
import com.guns.vo.cinema.GetFieldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/11/28 22:45
 */
@RestController
@RequestMapping("cinema")
public class CinemaController {

    @Reference(interfaceClass = IMtimeBrandDictTService.class, check = false)
    IMtimeBrandDictTService brandDictTService;
    @Reference(interfaceClass = IMtimeCinemaTService.class, check = false)
    IMtimeCinemaTService cinemaTService;
    @Reference(interfaceClass = IMtimeFieldTService.class, check = false)
    IMtimeFieldTService fieldTService;
    @Autowired
    RedisTemplate redisTemplate;



    //根据条件，查询所有影院       cinema/getCondition?brandId=99&hallType=99&areaId=99
    /**
     * request                  pageSize 每页条数   nowPage 当前页数
     * /cinema/getCinemas?  brandId=30227   districtId=14   hallType=2
     *
     * response
     * {
     * 	"status": 0,
     * 	“nowPage”: 1,
     * 	“totalPage”: 5,
     * 	"data": [{
     * 			   " uuid”: 1231,
     * 			“cinemaName”: “大地影院”,
     * 			“address”: ”东城区滨河路乙1号雍和航星园74 - 76 号楼”,
     * 			“minimumPrice”: 48.5
     *                },
     *        {
     * 			   "uuid”: 3265,
     * 			“cinemaName”: “万达影院”,
     * 			“address”: ”丰台区开阳路8号悦秀城6层”,
     * 			“minimumPrice”: 32.8
     *        }
     * 	]
     * }
     */
    @RequestMapping("getCinemas")
    public GetCinemasVo getCinemas(Integer brandId, Integer districtId, Integer hallType){
        GetCinemasVo<Object> cinemasVo = new GetCinemasVo<>();
        List cinemas = brandDictTService.getCinemas(brandId, districtId, hallType);
        cinemasVo.setStatus(0);
        cinemasVo.setNowPage(1);
        cinemasVo.setTotalPage(cinemas.size());
        cinemasVo.setData(cinemas);
        return cinemasVo;
    }


    //获取场次的详情信息
    /**
     * request
     * ///cinema/getFieldInfo
     * cinemaId     电影编号
     *  fieldId     场次编号
     * response
     * {
     * 	"status":0,
     * 	"imgPre":"http://img.meetingshop.cn/",
     * 	"data":{
     * 		"filmInfo":{
     * 			"filmId":"12",
     * 			"filmName":"反贪风暴3",
     * 			"filmType":"国语2D",
     * 			"imgAddress":"films/294381-3.jpg",
     * 			"filmCats":"剧情,动作,犯罪",
     * 			"filmLength": 100
     *                },
     * 		"cinemaInfo":{
     * 		“cinemaId”: 123594,
     * 		“imgUrl”:”cinemas/123.jpg”,
     * 		“cinemaName”:”大地影院”,
     * 		“cinemaAdress”:” 顺义区新顺南大街11号隆华购物中心6F”,
     * 			“cinemaPhone”:” 010-89472732”
     *        },
     * 		"hallInfo":{
     * 			"hallFieldId":"1",
     * 			"hallName":"1号VIP厅",
     * 			"price":48,
     * 			"seatFile":"halls/4552.json",
     * 			"soldSeats":"1,2,3,5,12"
     *        }
     ** 	}
     * }
     * @return
     */
    @RequestMapping("getFieldInfo")
    public GetFieldInfo getFieldInfo(Integer cinemaId, Integer fieldId, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
        //从redis缓存中取得用户的uuid
        Integer uuid = userCacheVO.getUuid();
        GetFieldInfo<Object> fieldInfo = new GetFieldInfo<>();
        Object fieldMessage = fieldTService.getFieldMessage(cinemaId,fieldId,uuid);
        fieldInfo.setStatus(0);
        fieldInfo.setImgPre("http://img.meetingshop.cn/");
        fieldInfo.setData(fieldMessage);
        return fieldInfo;
    }

}
