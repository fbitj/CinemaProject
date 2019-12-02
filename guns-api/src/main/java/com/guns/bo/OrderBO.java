package com.guns.bo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class OrderBO implements Serializable {

    private static final long serialVersionUID = 8992611145667341786L;
    private String uuid;

    private Integer cinemaId;

    private Integer fieldId;

    private Integer filmId;

    private String[] seatsIds;

    private String[] seatsName;

    private Double filmPrice;

    private Double orderPrice;

    private Date orderTime;

    private Integer orderUser;

    private Integer orderStatus;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Integer cinemaId) {
        this.cinemaId = cinemaId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String[] getSeatsIds() {
        return seatsIds;
    }

    public void setSeatsIds(String[] seatsIds) {
        this.seatsIds = seatsIds;
    }

    public String[] getSeatsName() {
        return seatsName;
    }

    public void setSeatsName(String[] seatsName) {
        this.seatsName = seatsName;
    }

    public Double getFilmPrice() {
        return filmPrice;
    }

    public void setFilmPrice(Double filmPrice) {
        this.filmPrice = filmPrice;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(Integer orderUser) {
        this.orderUser = orderUser;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }


    @Override
    public String toString() {
        return "MoocOrderT{" +
        "uuid=" + uuid +
        ", cinemaId=" + cinemaId +
        ", fieldId=" + fieldId +
        ", filmId=" + filmId +
        ", seatsIds=" + Arrays.toString(seatsIds) +
        ", seatsName=" +  Arrays.toString(seatsName) +
        ", filmPrice=" + filmPrice +
        ", orderPrice=" + orderPrice +
        ", orderTime=" + orderTime +
        ", orderUser=" + orderUser +
        ", orderStatus=" + orderStatus +
        "}";
    }
}
