package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by fwj on 2019-11-29.
 */

// 在Redis缓存中保存的用户信息
@Data
public class UserCacheVO implements Serializable {

    private static final long serialVersionUID = -7857159875506235288L;

    private Integer uuid;

    private String userName;

}
