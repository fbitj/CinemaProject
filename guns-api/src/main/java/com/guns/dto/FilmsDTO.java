package com.guns.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer showType;
    private Integer sortId;
    private Integer catId;
    private Integer sourceId;
    private Integer yearId;
    private Integer nowPage;
    private Integer pageSize;
}
