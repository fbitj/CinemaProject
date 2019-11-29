package com.guns.vo.film;

import lombok.Data;

import java.io.Serializable;

@Data
public class BannerVO implements Serializable {

    private static final long serialVersionUID = 7925110765286994472L;
    private String bannerId;

    private String bannerAddress;

    private String bannerUrl;
}
