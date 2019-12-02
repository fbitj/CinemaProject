package com.stylefeng.guns.rest.modular.cinema.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.cinema.IMoocOrderTService;
import com.guns.vo.UserCacheVO;
import com.guns.vo.cinema.GetFieldInfo;
import com.guns.vo.cinema.OrderVo;
import com.guns.vo.cinema.TicketsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/1 20:40
 */
@RestController
//@RequestMapping("order")
public class TestOrderController {


    @Reference(interfaceClass = IMoocOrderTService.class, check = false)
    IMoocOrderTService orderTService;
    @Autowired
    RedisTemplate redisTemplate;


    /**
     * request
     * http://localhost/order/buyTickets
     * ?fieldId=1    soldSeats=10,15,16   seatsName=%E5%8D%95%E6%8E%92%E5%BA%A7,%E5%8F%8C%E6%8E%92%E5%BA%A7
     * response
     * {
     * 	"status":0,
     * 	"msg":"",
     * 	"data":{
     * 		"orderId":"18392981493",
     * 		"filmName":"基于SpringBoot 十分钟搞定后台管理平台",
     * 		"fieldTime":"今天 9月8号 11:50",
     * 		"cinemaName":"万达影城(顺义金街店)",
     * 		"seatsName":"1排3座 1排4座 2排4座",
     * 		"orderPrice":"120",
     * 		"orderTimestamp":"1589754126"
     *        }
     * }
     */
    @RequestMapping("buyTickets")
    public OrderVo buyTicket(Integer fieldId, String soldSeats, String seatsName, HttpServletRequest request){
        Boolean trueSeats = orderTService.isTrueSeats(fieldId, soldSeats);
        OrderVo orderVo = new OrderVo();
        if(trueSeats){
            Boolean soldSeats1 = orderTService.isSoldSeats(fieldId, soldSeats);
            if(!soldSeats1) {
//        从redis缓存中取得用户的uuid
//        String token = request.getHeader("Authorization");
//        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
//        Integer uuid = userCacheVO.getUuid();
                String token = request.getHeader("Authorization");
                token = token.substring(7);
                UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
                Integer uuid = userCacheVO.getUuid();
                Object o = orderTService.buyTickets(fieldId, soldSeats, seatsName, uuid);
                orderVo.setStatus(0);
                orderVo.setMsg("");
                orderVo.setData(o);
                return orderVo;
            }else {
                orderVo.setStatus(-2);
                orderVo.setMsg("该座位已被购买");
                return orderVo;
            }
        }else {
            orderVo.setStatus(-1);
            orderVo.setMsg("非法数据");
            return orderVo;
        }
    }


    /**r
     * request      /order/getOrderInfo
     * response
     * {
     * 	"status":0,
     * 	"msg":"",
     * 	"data":[
     *                {
     * 			"orderId":"18392981493",
     * 			"filmName":"基于SpringBoot 十分钟搞定后台管理平台",
     * 			"fieldTime":"9月8号 11:50",
     * 			"cinemaName":"万达影城(顺义金街店)",
     * 			"seatsName":"1排3座 1排4座 2排4座",
     * 			"orderPrice":"120",
     * 			"orderStatus”:”已关闭”
     *        },{
     * 			"orderId":"213581239123",
     * 			"filmName":"Tomcat+Memcached/Redis集群",
     * 			"fieldTime":"9月10号 13:50",
     * 			"cinemaName":"万达影城(顺义金街店)",
     * 			"seatsName":"1排3座 1排4座 2排4座",
     * 			"orderPrice":"140",
     * 			"orderStatus”:”已完成”
     *        },
     * 	]
     * }
     */
    @RequestMapping("getOrderInfo")
    public OrderVo getOrderinfo(Integer nowPage, Integer pageSize, HttpServletRequest request){
//        从redis缓存中取得用户的uuid        需开启redis数据库
//        String token = request.getHeader("Authorization");
//        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
//        Integer uuid = userCacheVO.getUuid();
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
        Integer uuid = userCacheVO.getUuid();
        Object userOrders = orderTService.getUserOrders(nowPage, pageSize, uuid);
        OrderVo orderVo = new OrderVo();
        orderVo.setStatus(0);
        orderVo.setMsg("");
        orderVo.setData(userOrders);
        return orderVo;
    }


    //获取支付二维码
    /**
     * request              订单Id
     * //order/getPayInfo  orderId=1b4652a6a25a9ec8dd8610
     * response
     * {
     * 	"status":0,
     * 	"imgPre":"http://img.meetingshop.cn/",
     * 	"data":{
     * 		"orderId": "1234123",
     * 		"QRCodeAddress":"QRCodes/1234123.png"
     *        }
     * }
     * @param orderId
     */
    @RequestMapping("getPayInfo")
    public GetFieldInfo getPayInfo(String orderId){
        GetFieldInfo<Object> info = new GetFieldInfo<>();
        info.setStatus(0);
//        info.setImgPre("http://img.meetingshop.cn/");
//        http://cskaoyan.oss-cn-beijing.aliyuncs.com/316cb862f2c045d8b2a3d0106b92f097.jpg
        info.setImgPre("http://cskaoyan.oss-cn-beijing.aliyuncs.com/");
        String path = orderTService.getImg(orderId);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("orderId",orderId);
        map.put("qRCodeAddress",path);
        info.setData(map);
        return info;
    }


    //查看支付状态
    /**
     * request      /order/getPayResult
     *      orderId     订单id
     *      tryNums     刷新次数    15秒一刷新
     *                  超过三次，返回支付失败
     * response
     * {
     * 	"status":0,
     * 	"data":{
     * 		"orderId": "1234123",
     * 		"orderStatus": 1,
     * 		"orderMsg":"支付成功"
     *        }
     * }
     */
    @RequestMapping("getPayResult")
    public GetFieldInfo getParResult(String orderId, Integer tryNums){
        GetFieldInfo<Object> info = new GetFieldInfo<>();
        if(tryNums > 4){
            info.setStatus(-1);
            info.setData("超时");
            return info;
        }
        HashMap<String, Object> map = (HashMap<String, Object>) orderTService.getPayRequest(orderId);
        info.setStatus(0);
        info.setData(map);
        return info;
    }



}
