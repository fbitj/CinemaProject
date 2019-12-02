package com.stylefeng.guns.rest.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.demo.trade.Main;
import com.alipay.demo.trade.model.GoodsDetail;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.service.order.OrderService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.OrderVO;
import com.stylefeng.guns.rest.common.exception.OrderException;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@com.alibaba.dubbo.config.annotation.Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private MtimeFieldTMapper fieldTMapper;

    @Autowired
    private MtimeHallDictTMapper hallDictTMapper;

    @Autowired
    private MoocOrderTMapper orderTMapper;

    private Main main = new Main();

    @Override
    public Boolean isTrueSeats(Integer filedId, String seatIds) {
        // 根据场次id获取到场次的座位信息
        HallInfo hallInfo;
        try {
            hallInfo = getHallInfo(filedId);
        } catch (Exception e) {
            throw new OrderException("读取场次座位信息失败！");
        }
        // 验证座位编号是否合法
        String hallIds = hallInfo.getIds();
        return isContainAll(hallIds, seatIds);
    }


    private HallInfo getHallInfo(Integer filedId) throws IOException {
        MtimeFieldT mtimeFieldT = fieldTMapper.selectById(filedId);
        Integer hallId = mtimeFieldT.getHallId();
        MtimeHallDictT mtimeHallDictT = hallDictTMapper.selectById(hallId);
        String seatAddress = mtimeHallDictT.getSeatAddress();
        InputStream input = getClass().getClassLoader().getResourceAsStream(seatAddress);
        byte[] bytes = new byte[1024];
        int len = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while ((len = input.read(bytes)) != -1) {
            stringBuffer.append(new String(bytes, 0, len, "utf-8"));
        }
        input.close();
        String seatsInfo = stringBuffer.toString();
        HallInfo hallInfo = JSONObject.parseObject(seatsInfo, HallInfo.class);
        return hallInfo;
    }


    /**
     * 判断字符串转换为list后是否包含字符串转换后的list
     *
     * @param str
     * @param subStr
     * @return
     */
    private boolean isContainAll(String str, String subStr) {
        List<String> strList = Arrays.asList(str.split(","));
        List<String> subStrIds = Arrays.asList(subStr.split(","));
        return strList.containsAll(subStrIds);
    }


    @Override
    public Boolean isSoldSeats(Integer filedId, String seatId) {
        // 去订单表中查询该场次所有已经出售的座位
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("field_id", filedId);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectList(wrapper);
        StringBuffer stringBuffer = new StringBuffer();
        for (MoocOrderT moocOrderT : moocOrderTS) {
            String seatsIds = moocOrderT.getSeatsIds();
            stringBuffer.append(seatsIds);
        }
        String soldSeats = stringBuffer.toString();
        boolean hasSold = isContainAll(soldSeats, seatId);
        return hasSold;
    }

    @Override
    public OrderVO saveOrderInfo(Integer filedId, String soldSeats, String seatsName, Integer userId) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        MtimeFieldT mtimeFieldT = fieldTMapper.selectById(filedId);
        double price = mtimeFieldT.getPrice();
        String[] split = soldSeats.split(",");
        Double orderPrice = price * split.length;
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(uuid);
        moocOrderT.setCinemaId(mtimeFieldT.getCinemaId());
        moocOrderT.setFieldId(filedId);
        moocOrderT.setFilmId(mtimeFieldT.getFilmId());
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setFilmPrice(price);
        moocOrderT.setOrderPrice(orderPrice);
        moocOrderT.setOrderTime(new Date());
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderStatus(0);
        orderTMapper.insert(moocOrderT);
        OrderVO orderVo = packagingOrderVO(moocOrderT, mtimeFieldT);
        return orderVo;
    }

    @Autowired
    MtimeFilmTMapper filmTMapper;
    @Autowired
    MtimeCinemaTMapper cinemaTMapper;

    private OrderVO packagingOrderVO(MoocOrderT moocOrderT, MtimeFieldT mtimeFieldT) {
        // 查询场次的电影院名
        MtimeFilmT filmT = filmTMapper.selectById(moocOrderT.getFilmId());
        // 查询场次的电影名
        MtimeCinemaT mtimeCinemaT = cinemaTMapper.selectById(moocOrderT.getCinemaId());
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(moocOrderT.getUuid());
        orderVO.setFilmName(filmT.getFilmName());
        orderVO.setFieldTime(mtimeFieldT.getBeginTime() + "-" + mtimeFieldT.getEndTime());
        orderVO.setCinemaName(mtimeCinemaT.getCinemaName());
        orderVO.setSeatsName(moocOrderT.getSeatsName());
        orderVO.setOrderPrice(moocOrderT.getOrderPrice());
        orderVO.setOrderTimestamp(moocOrderT.getOrderTime());
        orderVO.setOrderStatus(moocOrderT.getOrderStatus());
        return orderVO;
    }

    /**
     * 获取支付二维码
     *
     * @param orderId
     * @return
     */
    @Override
    public BaseRespVO getPayCode(String orderId) {
        // 查询订单信息
        MoocOrderT moocOrderT = orderTMapper.selectById(orderId);
        Integer filmId = moocOrderT.getFilmId();
        MtimeFilmT filmT = filmTMapper.selectById(filmId);
        String goodsId = filmT.getUuid().toString();
        String filmName = filmT.getFilmName();

        Double filmPrice = moocOrderT.getFilmPrice();
        int number = moocOrderT.getSeatsIds().split(",").length;
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance(goodsId, filmName, filmPrice.longValue(), number);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);
        String outTradeNo = orderId;
        String totalAmount = moocOrderT.getOrderPrice().toString();
        String undiscountableAmount = "0";
        // 生成二维码

        Map<String, String> res = main.test_trade_precreate(outTradeNo, totalAmount, undiscountableAmount, goodsDetailList);
        String code = res.get("code");
        BaseRespVO objectBaseRespVO = new BaseRespVO();
        if (code != null && "10000".equals(code)) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("qRCodeAddress", res.get("filePath"));
            objectBaseRespVO.setStatus(0);
            objectBaseRespVO.setData(data);
            objectBaseRespVO.setImgPre("http://192.168.48.1:8084");
        } else {
            objectBaseRespVO.setStatus(700);
            objectBaseRespVO.setMsg("支付失败");
        }
        return objectBaseRespVO;
    }

    /**
     * 查询订单是否支付成功
     *
     * @param orderId 订单id
     * @param tryNums 查询次数
     * @return
     */
    @Override
    public BaseRespVO getPayResult(String orderId, Integer tryNums) {
        String res = main.test_trade_query(orderId);
        BaseRespVO objectBaseRespVO = new BaseRespVO();
        if ("10000".equals(res)) {
            // 修改订单状态为已支付
            orderTMapper.updateOrderStatusById(orderId);
            objectBaseRespVO.setStatus(0);
            HashMap<Object, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("orderMsg", "支付成功");
            data.put("orderStatus", 1);
            objectBaseRespVO.setData(data);
        } else {
            objectBaseRespVO.setStatus(100);
            objectBaseRespVO.setMsg("支付失败");
        }
        return null;
    }
}
