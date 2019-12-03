package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.gson.Gson;
import com.guns.bo.FieldBO;
import com.guns.bo.OrderBO;
import com.guns.service.order.OrderService;
import com.guns.vo.OrderVO;
import com.stylefeng.guns.rest.bean.OrderStatus;
import com.stylefeng.guns.rest.bean.SeatingChart;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisTemplate<String, SeatingChart> redisTemplate;

    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;

    @Autowired
    MoocOrderTMapper orderTMapper;


    /**
     * 验证该座位是否存在
     * @param fieldId
     * @param soldSeats
     * @return
     */
    @Override
    public boolean verifySeat(Integer fieldId, String[] soldSeats) throws IOException {
        //获取该场次座位表保存路径
        String seatAd = mtimeFieldTMapper.querySeatAddressByFieldId(fieldId);

        //将文件中的信息读取到字符串中
        /*InputStream inputStream = new FileInputStream(seatAd);
        byte[] bytes = new byte[1024];
        int len = -1;
        StringBuffer stringBuffer = new StringBuffer();
        while ((len = inputStream.read(bytes)) != -1) {
            stringBuffer.append(new String(bytes));
        }*/

        //查看redis中是否保存有seatAd的对象
        SeatingChart seatingChart = redisTemplate.opsForValue().get(seatAd);
        if (seatingChart == null) {
            //若没有保存则从本地读取json文件
            //将json字符串转换成json对象
            File file = new File(seatAd);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Gson gson = new Gson();
            seatingChart = gson.fromJson(bufferedReader, SeatingChart.class);
            //将对象存入redis中
            redisTemplate.opsForValue().set(seatAd, seatingChart);
        }

        //将座位号字符串转换成数组
        String ids = seatingChart.getIds();
        String[] seatIds = ids.replace("\"", "").split(",");
        //查看座位号是否存在
        int count = 0;
        for (String soldSeat : soldSeats) {
            for (String seatId : seatIds) {
                if (soldSeat.equals(seatId)) {
                    count++;
                    break;
                }
            }
        }
        if (count == soldSeats.length) return true;
        /*JSONObject jsonObject = JSONObject.fromObject(stringBuffer);
        System.out.println(jsonObject);*/
        return false;
    }

    /**
     * 验证座位是否已被购买
     *
     * @param fieldId
     * @param soldSeats
     * @return
     */
    @Override
    public boolean verifyOrder(Integer fieldId, String[] soldSeats) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.ne("order_status", 2).eq("field_id" ,fieldId);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectList(wrapper);
        Set<String> seatList = new HashSet();
        int count = 0;
        if (!CollectionUtils.isEmpty(moocOrderTS)) {
            //遍历将所有的座位号保存在set中
            for (MoocOrderT moocOrderT : moocOrderTS) {
                String[] seatsIds = moocOrderT.getSeatsIds();
                count += seatsIds.length;
                for (String seatsId : seatsIds) {
                    seatList.add(seatsId);
                }
            }
        }
        //检验数据库中是否保存有重复的座位号
        if (count != seatList.size()) return true;
        //验证是否有座位被购买
        for (String soldSeat : soldSeats) {
            for (String s : seatList) {
                if (soldSeat.equals(s)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 新增订单
     * @param orderBO
     * @param fieldBO
     * @return
     */
    @Override
    public OrderVO addOrder(OrderBO orderBO, FieldBO fieldBO) {
        //生成uuid ==> 暂定
        String uuid = UUID.randomUUID().toString().replace("-", "");

        //获取field对象 ===> 暂定
//        MtimeFieldT mtimeFieldT = mtimeFieldTMapper.selectById(orderBO.getFieldId());
        MtimeFieldT mtimeFieldT = new MtimeFieldT();
        BeanUtils.copyProperties(fieldBO, mtimeFieldT);
        Integer cinemaId = mtimeFieldT.getCinemaId();

        //计算订单金额
        double price = mtimeFieldT.getPrice();

        String[] seatsName = orderBO.getSeatsName();
        double totalPrice = price * seatsName.length;

        //获取存入的时间
        Date time = new Date();

        //存储数据
        MoocOrderT orderT = new MoocOrderT();
        BeanUtils.copyProperties(orderBO, orderT);
        orderT.setUuid(uuid);
        orderT.setCinemaId(cinemaId);
        orderT.setFilmId(mtimeFieldT.getFilmId());
        orderT.setOrderPrice(totalPrice);
        orderT.setFilmPrice(price);
        orderT.setOrderTime(time);
        orderT.setOrderStatus(0);
        Integer update = orderTMapper.insert(orderT);

        if (update == 0) throw new IllegalArgumentException();

        //封装返回参数
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(uuid);
        orderVO.setOrderPrice(Double.toString(totalPrice));
        long timeStamp = time.getTime();
        orderVO.setOrderTimestamp(Long.toString(timeStamp));

        //获取场次日期 ==== 暂定获取当天日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("今天M月d号");
        String format = simpleDateFormat.format(time);
        orderVO.setFieldTime(format + " " + mtimeFieldT.getBeginTime());

        String seats = convertor(seatsName);
        orderVO.setSeatsName(seats);

        return orderVO;
    }

    private String convertor(String[] seatsName) {
        if (seatsName.length > 1) {
            return Arrays.toString(seatsName).replace("[", "")
                    .replace("]", "").replace(",", "");
        } else {
            return Arrays.toString(seatsName).replace("[", "")
                    .replace("]", "");
        }

    }

    /**
     * 查询场次信息
     * @param fieldId
     * @return
     */
    @Override
    public FieldBO selectFieldById(Integer fieldId) {
        MtimeFieldT mtimeFieldT = mtimeFieldTMapper.selectById(fieldId);
        FieldBO target = new FieldBO();
        BeanUtils.copyProperties(mtimeFieldT, target);
        return target;
    }

    /**
     * 获取用户所有订单并分页
     * @param userId
     * @param nowPage
     * @param pageSize
     * @return
     */
    @Override
    public List selectOrderByUserId(Integer userId, Integer nowPage, Integer pageSize) {
        Page<MoocOrderT> page = new Page<>(nowPage, pageSize, "order_time", false);
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("order_user", userId);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectPage(page, wrapper);
        List result = new ArrayList();
        //封装参数
        if (!CollectionUtils.isEmpty(moocOrderTS)) {
            for (MoocOrderT moocOrderT : moocOrderTS) {
                OrderVO orderVO = new OrderVO();
                orderVO.setOrderId(moocOrderT.getUuid());
                FieldBO fieldBO = selectFieldById(moocOrderT.getFieldId());
                String beginTime = fieldBO.getBeginTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日 ");
                orderVO.setFieldTime(simpleDateFormat.format(moocOrderT.getOrderTime()) + beginTime);
                orderVO.setSeatsName(convertor(moocOrderT.getSeatsName()));
                orderVO.setOrderPrice(moocOrderT.getOrderPrice().toString());
                Integer orderStatus = moocOrderT.getOrderStatus();
                if (orderStatus == 0) {
                    orderVO.setOrderStatus(OrderStatus.ORDER_NOPAY.getMessage());
                }
                if (orderStatus == 1) {
                    orderVO.setOrderStatus(OrderStatus.ORDER_ISPAY.getMessage());
                }
                if (orderStatus == 2) {
                    orderVO.setOrderStatus(OrderStatus.ORDER_ISCLOSE.getMessage());
                }
                long time = moocOrderT.getOrderTime().getTime();
                time = time / 1000;
                orderVO.setOrderTimestamp(Long.toString(time));
                orderVO.setFilmId(moocOrderT.getFilmId());
                orderVO.setCinemaId(moocOrderT.getCinemaId());
                result.add(orderVO);
            }
        }

        return result;
    }

    /**
     * 根据订单id查询订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderBO selectOrderByOrderId(String orderId) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", orderId);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectList(wrapper);
    if (CollectionUtils.isEmpty(moocOrderTS) || moocOrderTS.size() != 1) throw new IllegalArgumentException();
        MoocOrderT moocOrderT = moocOrderTS.get(0);
        OrderBO orderBO = new OrderBO();
        BeanUtils.copyProperties(moocOrderT, orderBO);
        return orderBO;
    }

    /**
     * 更改订单状态码
     * @param orderStatus
     * @return
     */
    @Override
    public int changeOrderStatus(int orderStatus, String orderId) {
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setOrderStatus(orderStatus);
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", orderId);
        Integer update = orderTMapper.update(moocOrderT, wrapper);
        if (update == 0) throw new IllegalArgumentException();
        return update;
    }
}