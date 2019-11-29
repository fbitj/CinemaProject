package com.guns.vo.film;

import lombok.Data;

import java.io.Serializable;

@Data
public class YearInfo implements Serializable {
    private static final long serialVersionUID = 8340078579819022730L;

    private Boolean active = false;

    private String yearId;

    private String yearName;
}
