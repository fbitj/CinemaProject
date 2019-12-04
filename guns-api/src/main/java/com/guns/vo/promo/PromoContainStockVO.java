package com.guns.vo.promo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PromoContainStockVO implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer uuid;

    private Integer cinemaId;

    private BigDecimal price;

    private Date startTime;

    private Date endTime;

    private Integer status;

    private String description;

    private Integer stock;
/*

    public Integer getUuid() {
        return uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

    public Integer getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Integer cinemaId) {
        this.cinemaId = cinemaId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "MtimePromo{" +
        "uuid=" + uuid +
        ", cinemaId=" + cinemaId +
        ", price=" + price +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", status=" + status +
        ", description=" + description +
        "}";
    }*/
}
