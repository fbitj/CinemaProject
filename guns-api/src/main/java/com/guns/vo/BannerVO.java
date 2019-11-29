package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BannerVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bannerId;

    private String bannerAddress;

    private String bannerUrl;
}
