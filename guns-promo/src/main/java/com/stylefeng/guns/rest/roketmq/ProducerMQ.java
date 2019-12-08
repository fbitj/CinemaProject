package com.stylefeng.guns.rest.roketmq;

import com.alibaba.fastjson.JSON;
import com.guns.service.promo.PromoService;
import com.stylefeng.guns.rest.common.persistence.StockLogStatus;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
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
import java.util.Collections;
import java.util.HashMap;


@Slf4j
@Component
public class ProducerMQ {

    private DefaultMQProducer producer;

    private TransactionMQProducer transactionMQProducer;

    @Value("${rocket-mq.name-server.address}")
    private String address;

    @Value("${rocket-mq.topic}")
    private String topic;

    @Autowired
    private MtimeStockLogMapper stockLogMapper;

    @Autowired
    private PromoService promoService;

    @PostConstruct
    private void init(){
        producer = new DefaultMQProducer("SecKillProducer");
        producer.setNamesrvAddr(address);
        try {
            producer.start();
        } catch (MQClientException e) {
            log.info("producers初始化失败！addr:{}",address);
            e.printStackTrace();
        }
        log.info("producer初始化成功！addr:{}",address);

        transactionMQProducer = new TransactionMQProducer("SecKillProducerTransaction");
        transactionMQProducer.setNamesrvAddr(address);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            log.info("transactionMQProducer初始化失败！addr:{}",address);
            e.printStackTrace();
        }
        log.info("transactionMQProducer初始化成功！addr:{}",address);

        // 设置事务回调监听器
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            // 监听器回调创建订单
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
                HashMap<String,Object> argMap = (HashMap<String, Object>) arg;
                String promoId = (String) argMap.get("promoId");
                Integer amount = (Integer) argMap.get("amount");
                Integer userId = (Integer) argMap.get("userId");
                String stockLogId = (String) argMap.get("stockLogId");

                // 执行本地事务，添加订单
                Boolean result;
                try {
                    result = promoService.savePromoOrderVO(promoId, amount, userId, stockLogId);
                } catch (Exception e) {
                    log.info("回调本地事务，添加订单时失败");
                    e.printStackTrace();
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }

                if (!result){
                    log.info("回调本地事务，添加订单时失败");
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // 根据消息体查询流水表信息确定是否下单成功
                byte[] body = messageExt.getBody();
                String content = new String(body);
                HashMap<String, Object> msgMap = JSON.parseObject(content, HashMap.class);
                String stockLogId = (String) msgMap.get("stockLogId");
                MtimeStockLog stockLog;
                try {
                    stockLog = stockLogMapper.selectById(stockLogId);
                } catch (Exception e) {
                    log.info("查询流水状态失败, stockLogId:{}",stockLogId);
                    e.printStackTrace();
                    return LocalTransactionState.UNKNOW;
                }
                if (stockLog == null){
                    log.info("没有查到相关的流水信息");
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                if (stockLog.getStatus() == StockLogStatus.SUCCESS.getStatus()){
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (stockLog.getStatus() == StockLogStatus.FAILED.getStatus()){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return  LocalTransactionState.UNKNOW;
            }
        });
    }

    /**
     * 发送减少秒杀活动商品库存消息
     * @param promoId 活动id
     * @param amount 减少库存数量
     * @return
     */
    public Boolean decreaseStock(String promoId,Integer amount) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("promoId",promoId);
        map.put("amount",amount);
        Message message = new Message(topic, JSON.toJSONString(map).getBytes(Charset.forName("utf-8")));
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (sendResult == null){
            return false;
        }
        SendStatus sendStatus = sendResult.getSendStatus();
        if (SendStatus.SEND_OK.equals(sendStatus)){
            return true;
        }
        return false;
    }

    /**
     * 发送一个库存减少的分布式事务消息
     */
    public Boolean sendStockMessageInTransaction(String promoId, Integer amount, Integer userId, String stockLogId) {
        HashMap<String, Object> msgContent = new HashMap<>();
        msgContent.put("promoId",promoId);
        msgContent.put("amount",amount);
        msgContent.put("userId",userId);
        msgContent.put("stockLogId",stockLogId);
        HashMap<String, Object> arg = (HashMap<String, Object>) msgContent.clone();
        Message message = new Message(topic, JSON.toJSONString(msgContent).getBytes(Charset.forName("utf-8")));
        // 发送一个事务消息，接着监听器回调创建订单
        TransactionSendResult transactionSendResult;
        try {
            transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, arg);
        } catch (MQClientException e) {
            log.info("发送事务型消息时异常：stock");
            e.printStackTrace();
            return false;
        }
        if (transactionSendResult == null) {
            return false;
        }
        LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
        if (LocalTransactionState.COMMIT_MESSAGE.equals(localTransactionState)) {
            return true;
        }
        return false;
    }
}
