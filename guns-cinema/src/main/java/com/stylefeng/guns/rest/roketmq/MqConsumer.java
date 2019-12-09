package com.stylefeng.guns.rest.roketmq;

import com.alibaba.fastjson.JSON;
import com.guns.service.cinema.IMoocOrderTService;
import com.guns.service.cinema.IMtimePromoService;
import com.guns.service.cinema.IMtimePromoStockService;
import com.guns.vo.UserCacheVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/4 17:51
 */
@Component
@Slf4j
public class MqConsumer {

    @Value("${mq.nameserver.address}")
    private String address;

    @Value("${mq.topic}")
    private String topic;

    @Autowired
    IMtimePromoStockService promoStockService;
    @Autowired
    IMoocOrderTService orderTService;

    private DefaultMQPushConsumer pushConsumer;

    private DefaultMQPushConsumer pushConsumer1;



    @PostConstruct
    public void init(){
        pushConsumer = new DefaultMQPushConsumer("consumer_group");
        pushConsumer.setNamesrvAddr(address);
        try {
            pushConsumer.subscribe(topic, "*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        pushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                byte[] body = messageExt.getBody();
                String msg = new String(body);
                HashMap map = JSON.parseObject(msg, HashMap.class);
                Integer promoId = (Integer) map.get("promoId");
                Integer amount = (Integer) map.get("amount");
                //更新数据库数据
                MtimePromoStock promoStock = (MtimePromoStock) promoStockService.getPromoStock(promoId);
                promoStockService.updatePromStockByPromoId(promoId, promoStock.getStock() - amount);
                log.info("promoId = {}, amount = {}",promoId, amount);
                //超时重试16次🗡
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            pushConsumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void initOrder(){
        pushConsumer1 = new DefaultMQPushConsumer("orderStock_group");
        pushConsumer1.setNamesrvAddr(address);
        try {
            pushConsumer1.subscribe(topic, "*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        pushConsumer1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                byte[] body = messageExt.getBody();
                String msg = new String(body);
                HashMap map = JSON.parseObject(msg, HashMap.class);
                String orderId = (String) map.get("orderId");
                //更新数据库数据
                MoocOrderT order = (MoocOrderT) orderTService.getOrderByUuid(orderId);
                Integer result = -1;
                if(order.getOrderStatus() == 0){
                    //座位库存返回，订单状态改为关闭
                    result = orderTService.updataOrderSeats(orderId);
                }
                if(result == -1){
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                //超时重试16次🗡
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            pushConsumer1.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }


}
