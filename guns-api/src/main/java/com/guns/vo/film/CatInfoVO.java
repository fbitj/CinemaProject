package com.guns.vo.film;

import lombok.Data;

import java.io.Serializable;

@Data
public class CatInfoVO implements Serializable {

    private static final long serialVersionUID = 6652296762474116434L;

    private Boolean active = false;

    private String catId;

    private String catName;
}
