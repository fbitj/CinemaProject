package com.stylefeng.guns.rest.service;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.guns.service.cinema.*;
import com.guns.utils.String2Array;
import com.guns.vo.cinema.SeatsJson;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.component.AliyunComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trade.Main;
import trade.config.Configs;
import trade.model.ExtendParams;
import trade.model.GoodsDetail;
import trade.model.builder.AlipayTradePrecreateRequestBuilder;
import trade.model.builder.AlipayTradeQueryRequestBuilder;
import trade.model.result.AlipayF2FPrecreateResult;
import trade.model.result.AlipayF2FQueryResult;
import trade.service.AlipayMonitorService;
import trade.service.AlipayTradeService;
import trade.service.impl.AlipayMonitorServiceImpl;
import trade.service.impl.AlipayTradeServiceImpl;
import trade.service.impl.AlipayTradeWithHBServiceImpl;
import trade.utils.Utils;
import trade.utils.ZxingUtils;


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
    @Autowired
    AliyunComponent aliyunComponent;



    private static Log log = LogFactory.getLog(MoocOrderTServiceImpl.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService   tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }


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
        String str = "";
        String string = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((str = bufferedReader.readLine() )!= null){
                sb.append(str);
            }
            string = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //转化为SeatsJson 对象座位信息，然后取出座位信息，看是否包含响应报文中的座位信息
        //如果是，  合法的，返回true
        //如果不是，不合法，返回false
        int i = string.indexOf("ids\":\"");
        int i1 = string.indexOf("\"single");
        String substring = string.substring(i + 6, i1 - 4);

        ArrayList<Integer> integers = String2Array.string2Arrays(substring);
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

    @Override
    public Object getOrder(Integer cinemaId, Integer fieldId, Integer filmId) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("film_id",filmId);
        map.put("cinema_id",cinemaId);
        map.put("field_id",fieldId);
        wrapper.allEq(map);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectByMap(map);
        return moocOrderTS;
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

    @Override
    public Object getOrderByUuid(String uuid) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", uuid);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectList(wrapper);
        MoocOrderT moocOrderT = moocOrderTS.get(0);
        return moocOrderT;
    }

    @Override
    public String getImg(String orderId) {
        MoocOrderT order = (MoocOrderT) this.getOrderByUuid(orderId);

        EntityWrapper<MtimeFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", order.getFilmId());
        List<MtimeFilmT> mtimeFilmTS = filmTMapper.selectList(wrapper);
        MtimeFilmT mtimeFilmT = mtimeFilmTS.get(0);
        String filmName = mtimeFilmT.getFilmName();

        MtimeCinemaT cine = (MtimeCinemaT) cinemaTService.getCine(order.getCinemaId());
        String cinemaName = cine.getCinemaName();

        String img = this.test_trade_precreate(orderId, filmName, cinemaName, order.getOrderPrice(),
                String2Array.string2Arrays(order.getSeatsIds()).size());
        return img;
    }

    @Override
    public Map getPayRequest(String orderId) {
        HashMap<Object, Object> map = new HashMap<>();
        Timer timer = new Timer();


        Integer integer = this.test_trade_query(orderId);
        map.put("orderId", orderId);
        if(integer == 1){
            Integer integer1 = this.updataOrderStatus(orderId);
            map.put("orderStatus", 1);
            map.put("orderMsg", "支付成功");
            return map;
        } else if(integer == -1){
            map.put("orderStatus", -1);
            map.put("orderMsg", "查询返回该订单支付失败或被关闭!!!");
            return map;
        }else if(integer == -2){
            map.put("orderStatus", -2);
            map.put("orderMsg", "系统异常，订单支付状态未知!!!");
            return map;
        }else if(integer == -3){
            map.put("orderStatus", -3);
            map.put("orderMsg", "不支持的交易状态，交易返回异常!!!");
            return map;
        }
        return new HashMap();
    }

    @Override
    public Integer updataOrderStatus(String orderId) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        MoocOrderT moocOrderT = new MoocOrderT();
        wrapper.eq("UUID", orderId);

        moocOrderT.setOrderStatus(1);
        Integer integer = orderTMapper.update(moocOrderT, wrapper);
        return integer;
    }


    // 测试当面付2.0生成支付二维码
    public String test_trade_precreate(String orderId, String filmname,String cinameName, Double price, Integer quantity) {
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
//        String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
//                            + (long) (Math.random() * 10000000L);
        //这个是影院系统中的订单编号，需要保证唯一
        String outTradeNo = "tradeprecreate" + orderId;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = cinameName + "当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = price + "";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买电影票花费" + price + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance(orderId, filmname, 200, quantity);
//        GoodsDetail goods1 = GoodsDetail.newInstance(orderId, filmname, Double.doubleToLongBits(price), quantity);
//        GoodsDetail goods1 = GoodsDetail.newInstance(orderId, filmname, Long.getLong(Double.toString(price)), quantity);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);


        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setTimeoutExpress(timeoutExpress)
                //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
//                String filePath = String.format("http://img.meetingshop.cn/qr-%s.png", response.getOutTradeNo());
//                String filePath = String.format("http://cskaoyan.oss-cn-beijing.aliyuncs.com/qr-%s.png", response.getOutTradeNo());
                String filePath = String.format("H:\\MyMicroservice\\QRcode/qr-%s.png", response.getOutTradeNo());
                log.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                String img = "qr-" + response.getOutTradeNo() + ".png";
                File file = new File("H:\\MyMicroservice\\QRcode\\" + img);
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    if(aliyunComponent.getOssClient() != null) {
                        OSSClient ossClient = aliyunComponent.getOssClient();
                        ossClient.putObject(aliyunComponent.getOss().getBucket(), img, fileInputStream);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return img;

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return "生成失败，请重试！";
    }

    // 测试当面付2.0查询订单
    public Integer test_trade_query(String orderId) {
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        String outTradeNo = orderId;

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");

                AlipayTradeQueryResponse response = result.getResponse();
                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                return 1;

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                return -1;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                return -2;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return -3;
        }
    }


}
