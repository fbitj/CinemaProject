package com.stylefeng.guns.rest.roketmq;

import com.alibaba.fastjson.JSON;
import com.guns.service.cinema.IMtimePromoOrderService;
import com.guns.service.cinema.IMtimeStockLogService;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/4 17:51
 */
@Component
@Slf4j
public class MqProducer {

    @Value("${mq.nameserver.address}")
    private String address;
    @Value("${mq.topic}")
    private String topic;
    @Value("${mq.transaction}")
    private String transaction;

    @Autowired
    IMtimePromoOrderService promoOrderService;
    @Autowired
    IMtimeStockLogService stockLogService;

    private DefaultMQProducer producer;

    private DefaultMQProducer producer1;

    private TransactionMQProducer transactionMQProducer;


    @PostConstruct
    public void init(){
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(address);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("初始化完成！！address:{}", address);

        //延时更新订单座位表
        producer1 = new DefaultMQProducer("orderStock_group");
        producer1.setNamesrvAddr(address);
        try {
            producer1.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        //事务级管理器
        transactionMQProducer = new TransactionMQProducer("transaction_group");
        transactionMQProducer.setNamesrvAddr(address);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        //事务监听回调器
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            //执行本地事务
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object args) {
                HashMap<String, Object> map = (HashMap<String, Object>) args;
                Integer promoId = (Integer) map.get("promoId");
                String stockId = (String) map.get("stockId");
                Integer amount = (Integer) map.get("amount");
                Integer uuid = (Integer) map.get("uuid");

                //执行本地事务， 插入订单 扣减redis中的库存
                Integer integer = -1;
                try {
                    integer = promoOrderService.insertIntoPromOrder(uuid, promoId, amount, stockId);
                }catch (Exception e){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                if(integer < 0){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            //回复本地资源状态
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                byte[] body = msg.getBody();
                String massage = new String(body);
                HashMap map1 = JSON.parseObject(massage, HashMap.class);
                String stockId = (String) map1.get("stockId");
                MtimeStockLog stockLog = (MtimeStockLog) stockLogService.getStockLogById(stockId);
                Integer status = stockLog.getStatus();
                if(status == 2){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                if(status == 3){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.UNKNOW;
            }
        });

    }

    public Boolean updateStock(Integer prommoId, Integer amount){
        HashMap<String, Object> map = new HashMap<>();
        map.put("promoId", prommoId);
        map.put("amount", amount);
        Message message = new Message(topic, JSON.toJSONString(map).getBytes(Charset.forName("utf-8")));
        SendResult send = null;
        try {
            send = producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("发送消息完成，发送详情：{}", JSON.toJSONString(send));
        if(send == null){
            return false;
        }else{
            if(SendStatus.SEND_OK.equals(send.getSendStatus())){
                return true;
            }
            return false;
        }
    }

    //发送事务型消息
    public Boolean sendTransactionMessage(Integer promoId, Integer amount, Integer uuid, String stockId){
        HashMap<String, Object> map = new HashMap<>();
        map.put("promoId", promoId);
        map.put("amount", amount);
        map.put("uuid", uuid);
        map.put("stockId", stockId);
        HashMap<String, Object> orgsMap = new HashMap<>();
        orgsMap.put("stockId", stockId);
        orgsMap.put("promoId", promoId);
        orgsMap.put("amount", amount);
        orgsMap.put("uuid", uuid);

        byte[] body = JSON.toJSONString(map).toString().getBytes(Charset.forName("utf-8"));
        Message message = new Message(topic, body);
        TransactionSendResult send = null;
        try {
            send =transactionMQProducer.sendMessageInTransaction(message, orgsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        if(send == null){
            return false;
        }
        //成功则执行本地事务
        LocalTransactionState localTransactionState = send.getLocalTransactionState();
        if(LocalTransactionState.COMMIT_MESSAGE.equals(localTransactionState)){
            return true;
        }
        return false;
    }

    //延时跟新电影座位订单库存
    public Boolean updateStockDelayed(String orderId){
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        Message message = new Message(topic, JSON.toJSONString(map).getBytes(Charset.forName("utf-8")));
        SendResult send = null;
        // messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        // 消息延迟5分钟后发送       避免与付款超时时间冲突
        // 即消息延迟时间 > 付款超时时间 + 30s
        message.setDelayTimeLevel(9);
        try {
            send = producer1.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("发送消息完成，发送详情：{}", JSON.toJSONString(send));
        if(send == null){
            return false;
        }else{
            if(SendStatus.SEND_OK.equals(send.getSendStatus())){
                return true;
            }
            return false;
        }
    }


}
