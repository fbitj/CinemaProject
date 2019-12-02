package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallTypeVO implements Serializable {
    private Integer halltypeId;
    private String halltypeName;
    private boolean active;
}
