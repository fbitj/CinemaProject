package com.stylefeng.guns.rest.service;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.guns.service.cinema.*;
import com.guns.utils.String2Array;
import com.guns.vo.cinema.SeatsJson;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author Cats
 * @since 2019-11-30
 */
@Component
@Service(interfaceClass = IMoocOrderTService.class)
public class MoocOrderTServiceImpl implements IMoocOrderTService, Serializable {

    @Autowired
    MoocOrderTMapper orderTMapper;
    @Autowired
    IMtimeFieldTService fieldTService;
    @Autowired
    IMtimeCinemaTService cinemaTService;
    @Autowired
    MtimeFilmTMapper filmTMapper;
    @Autowired
    IMtimeHallDictTService hallDictTService;



    @Override
    public Boolean isTrueSeats(Integer fieldId, String soldSeats) {
        MtimeFieldT fields = (MtimeFieldT) fieldTService.getFields(fieldId);
        MtimeHallDictT hallDictT = (MtimeHallDictT) hallDictTService.gethall(fields.getHallId());
        String seatAddress = hallDictT.getSeatAddress();
        //座位表的绝对路径
        String path = "H:\\MyMicroservice\\film-front\\static\\json";
        File file = new File(path + "\\" + seatAddress);
        StringBuffer sb = new StringBuffer();
        Object json = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (bufferedReader.readLine() != null){
                sb.append(bufferedReader.readLine());
            }
             json = JSON.toJSONString(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //转化为SeatsJson 对象，然后取出座位信息，看是否包含响应报文中的座位信息
        //如果是，  合法的，返回true
        //如果不是，不合法，返回false
        SeatsJson seatsJson = (SeatsJson) json;

        ArrayList<Integer> integers = String2Array.string2Arrays(seatsJson.getIds());
        ArrayList<Integer> integers1 = String2Array.string2Arrays(soldSeats);
        boolean b = org.apache.commons.collections.CollectionUtils.containsAny(integers, integers1);

        return b;
    }

    @Override
    public Boolean isSoldSeats(Integer fieldId, String soldSeats) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("field_id", fieldId);
        Object[] status = {0, 2};
        wrapper.in("order_status", status);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectList(wrapper);
        ArrayList<Integer> objects = new ArrayList<>();
        for (MoocOrderT moocOrderT : moocOrderTS) {
            ArrayList<Integer> integers = String2Array.string2Arrays(moocOrderT.getSeatsIds());
            for (Integer integer : integers) {
                objects.add(integer);
            }
        }
        ArrayList<Integer> integers = String2Array.string2Arrays(soldSeats);
        boolean b = CollectionUtils.containsAny(objects, integers);
        return b;
    }

    public Object getOrders(Integer cinemaId, Integer fieldId, Integer filmId, Integer status){
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cinema_id",cinemaId);
        map.put("field_id",fieldId);
        map.put("film_id",filmId);
        map.put("order_status",status);
        wrapper.allEq(map);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectByMap(map);
        return moocOrderTS;
    }

    @Override
    public Object buyTickets(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        MtimeFieldT fields = (MtimeFieldT) fieldTService.getFields(fieldId);
        MtimeCinemaT cine = (MtimeCinemaT) cinemaTService.getCine(fields.getCinemaId());
        EntityWrapper<MtimeFilmT> wrapper = new EntityWrapper<>();
        Wrapper<MtimeFilmT> film = wrapper.eq("UUID", fields.getFilmId());
        List<MtimeFilmT> filmTS = filmTMapper.selectList(film);

        //创建订单
        MoocOrderT moocOrderT = new MoocOrderT();
        String uuid = UUID.randomUUID().toString().replaceAll("-","").substring(10);
        moocOrderT.setUuid(uuid);
        moocOrderT.setCinemaId(fields.getCinemaId());
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setFilmId(fields.getFilmId());
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setFilmPrice(0.0 + fields.getPrice());
        ArrayList<Integer> integers = String2Array.string2Arrays(soldSeats);
        moocOrderT.setOrderPrice(0.0 + integers.size() * fields.getPrice());
        Date orderTime = new Date();
        moocOrderT.setOrderTime(orderTime);
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderStatus(0);
        orderTMapper.insert(moocOrderT);

        //返回数据
        HashMap<Object, Object> map = new HashMap<>();
        map.put("orderId",uuid);
        map.put("filmName",filmTS.get(0).getFilmName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("今天yyyy年MM月dd号 HH:mm:ss");
        String format = simpleDateFormat.format(orderTime);
        map.put("fieldTime",format);
        map.put("cinemaName",cine.getCinemaName());
        map.put("seatsName",seatsName);
        map.put("orderPrice",0.0 + integers.size() * fields.getPrice());
        map.put("orderTimestamp",orderTime);
        return map;
    }

    @Override
    public Object getUserOrders(Integer nowPage, Integer pageSize, Integer userId) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("order_user", userId);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectList(wrapper);

        ArrayList<Object> list = new ArrayList<>();
        if(moocOrderTS.size() == 0){
            return "订单列表为空~";
        } else if(moocOrderTS.size() != 0){
            for (MoocOrderT moocOrderT : moocOrderTS) {
                EntityWrapper<MtimeFilmT> wrapper1 = new EntityWrapper<>();
                MtimeCinemaT cine = (MtimeCinemaT) cinemaTService.getCine(moocOrderT.getCinemaId());
                Wrapper<MtimeFilmT> film = wrapper1.eq("UUID", moocOrderT.getFilmId());
                List<MtimeFilmT> filmTS = filmTMapper.selectList(film);

                HashMap<Object, Object> map = new HashMap<>();
                map.put("orderId", moocOrderT.getUuid());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("今天yyyy年MM月dd号 HH:mm:ss");
                String format = simpleDateFormat.format(moocOrderT.getOrderTime());
                map.put("fieldTime", format);
                map.put("filmName", filmTS.get(0).getFilmName());
                map.put("cinemaName", cine.getCinemaName());
                map.put("seatsName", moocOrderT.getSeatsName());
                map.put("orderPrice", moocOrderT.getOrderPrice());
                if(moocOrderT.getOrderStatus() == 0){
                    map.put("orderStatus", "待支付");
                }else if(moocOrderT.getOrderStatus() == 1) {
                    map.put("orderStatus", "已支付");
                }else {
                    map.put("orderStatus", "已关闭");
                }
                list.add(map);
            }
            return list;
        }
        return "系统繁忙，请重试!";
    }

}
