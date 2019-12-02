package com.stylefeng.guns.rest.bean;

import lombok.Data;

import java.util.List;

@Data
public class SeatingChart {

    private Integer limit;

    private String ids;

    private List<List<SeatDatail>> single;

    private List<List<SeatDatail>> couple;
}
