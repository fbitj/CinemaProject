package com.guns.vo.cinema;

import java.io.Serializable;

public class CinemaVO implements Serializable {

    private static final long serialVersionUID = -1975141289589337292L;
    private Integer uuid;

    private String cinemaName;

    private String cinemaPhone;

    private Integer brandId;

    private Integer areaId;

    private String hallIds;

    private String imgAddress;

    private String cinemaAddress;

    private Integer minimumPrice;


    public Integer getUuid() {
        return uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaPhone() {
        return cinemaPhone;
    }

    public void setCinemaPhone(String cinemaPhone) {
        this.cinemaPhone = cinemaPhone;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getHallIds() {
        return hallIds;
    }

    public void setHallIds(String hallIds) {
        this.hallIds = hallIds;
    }

    public String getImgAddress() {
        return imgAddress;
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    public Integer getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(Integer minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    @Override
    public String toString() {
        return "MtimeCinemaT{" +
        "uuid=" + uuid +
        ", cinemaName=" + cinemaName +
        ", cinemaPhone=" + cinemaPhone +
        ", brandId=" + brandId +
        ", areaId=" + areaId +
        ", hallIds=" + hallIds +
        ", imgAddress=" + imgAddress +
        ", cinemaAddress=" + cinemaAddress +
        ", minimumPrice=" + minimumPrice +
        "}";
    }
}
