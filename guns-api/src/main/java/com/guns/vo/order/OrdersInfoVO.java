package com.guns.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrdersInfoVO implements Serializable {
    /*
        "orderId":"18392981493",
        "filmName":"基于SpringBoot 十分钟搞定后台管理平台",
        "fieldTime":"9月8号 11:50",
        "cinemaName":"万达影城(顺义金街店)",
        "seatsName":"1排3座 1排4座 2排4座",
        "orderPrice":"120",
        "orderStatus”:”已关闭”
    */
    private static final long serialVersionUID = -6849794470754667710L;
    private String orderId;

    private String filmName;

    private String fieldTime;

    private String cinemaName;

    private String seatsName;

    private Double orderPrice;

    private String orderStatus;

    private long orderTimestamp;

    public void setOrderStatus(Integer orderStatus) {
        if (orderStatus == 0){
            this.orderStatus = "待支付";
        } else if (orderStatus == 1){
            this.orderStatus = "已支付";
        }else if (orderStatus == 2){
            this.orderStatus = "已关闭";
        }
    }

    public void setOrderTimestamp(Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp.getTime()/1000;
    }
}
