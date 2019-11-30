package com.stylefeng.guns.rest.common.persistence.model;


import lombok.Data;

import java.io.Serializable;

@Data
public class BrandResp implements Serializable {
    private Integer brandId;
    private String brandName;
    private boolean active;
}
