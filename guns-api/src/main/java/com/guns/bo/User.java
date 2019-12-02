package com.guns.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable{

    private static final long serialVersionUID = -7570039655916205623L;

    private Integer uuid;

    private String userName;

    private String userPwd;

    private String nickName;

    private Integer userSex;

    private String birthday;

    private String email;

    private String userPhone;

    private String address;

    private String headUrl;

    private String biography;

    private Integer lifeState;

    private Date beginTime;

    private Date updateTime;

}
