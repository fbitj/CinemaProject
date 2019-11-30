package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallTypeResp implements Serializable {
    private Integer HalltypeId;
    private String HalltypeName;
    private boolean active;
}
