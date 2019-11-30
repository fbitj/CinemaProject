package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaResp implements Serializable {
    private Integer areaId;
    private String areaName;
    private boolean active;
}
