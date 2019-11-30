package com.guns.vo.cinema;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/11/28 23:02
 */
@Data
public class GetCinemasVo<T> implements Serializable {

    private Integer status;

    private Integer nowPage;

    private Integer totalPage;

    private T data;

}
