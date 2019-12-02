package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/1 23:16
 */
@Data
public class SeatsJson implements Serializable {

    private Integer limit;

    private String ids;

    private Object single;
}
