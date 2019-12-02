package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaVO implements Serializable {
    private Integer areaId;
    private String areaName;
    private boolean active;
}
