package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.util.List;

/**
 * 影厅信息表
 */
@Data
public class HallInfo {
    /**
     * 场次用户限购数量
     */
    Integer limit;

    /**
     * 场次所有id
     */
    String ids;

    /**
     * 座位详情，包括id、行、列
     */
    List<List<SeatInfo>> single;
}
