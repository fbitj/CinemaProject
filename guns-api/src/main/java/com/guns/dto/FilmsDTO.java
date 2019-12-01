package com.guns.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer showType = 1;

    private Integer sortId = 1;

    private Integer catId = 99;

    private Integer sourceId = 99;

    private Integer yearId = 99;

    private Integer nowPage = 1;

    private Integer pageSize = 18;
}
