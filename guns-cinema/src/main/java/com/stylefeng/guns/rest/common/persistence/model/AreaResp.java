package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaResp implements Serializable {
    private Integer AreaId;
    private String AreaName;
    private boolean active;
}
