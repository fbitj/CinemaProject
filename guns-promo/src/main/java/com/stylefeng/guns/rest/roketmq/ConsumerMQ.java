package com.stylefeng.guns.rest.roketmq;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class ConsumerMQ {

    @Value("${rocket-mq.name-server.address}")
    private String address;

    @Value("${rocket-mq.topic}")
    private String topic;

    @Autowired
    private MtimePromoStockMapper promoStockMapper;

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    private void init() {
        consumer = new DefaultMQPushConsumer("SecKillConsumer");
        consumer.setNamesrvAddr(address);
        try {
            consumer.subscribe(topic,"*");
        } catch (MQClientException e) {
            log.info("订阅topic失败！topic:{}",topic);
            e.printStackTrace();
        }
        consumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                byte[] body = messageExt.getBody();
                String content = new String(body);
                HashMap hashMap = JSON.parseObject(content, HashMap.class);
                String promoId = (String) hashMap.get("promoId");
                Integer amount = (Integer) hashMap.get("amount");
                // 调用库存减少
                log.info("收到减少数据库秒杀库存消息，addr:{}", address);
                // 减少数据库库存
                int update;
                try {
                    update = promoStockMapper.decreaseStockByPromoId(promoId, amount);
                } catch (Exception e) {
                    log.info("更新秒杀库存失败！promoId:{}", promoId);
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                if (update == 0) {
                    log.info("更新秒杀库存失败！promoId:{}", promoId);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                log.info("更新秒杀库存成功！promoId:{}", promoId);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.info("初始化MQ的consumer失败,addr:{}", address);
            e.printStackTrace();
        }
        log.info("初始化consumer成功！addr:{}",address);
    }

}
