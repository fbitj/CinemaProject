package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/12/1 20:48
 */
@Data
public class OrderVo implements Serializable {

    private Integer status;

    private String msg;

    private Object data;

}
