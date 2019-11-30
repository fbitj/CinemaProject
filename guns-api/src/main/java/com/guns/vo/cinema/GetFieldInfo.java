package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/11/29 15:25
 */
@Data
public class GetFieldInfo<T> implements Serializable {

    private Integer status;

    private String imgPre;

    private T data;
}
