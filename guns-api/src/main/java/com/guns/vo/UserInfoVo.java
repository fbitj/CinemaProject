package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhu rui
 * @version 1.0
 * @date 2019/11/28 21:48
 */
@Data
public class UserInfoVo implements Serializable {
   private Integer uuid;
   private String username;
   private String nickname;
   private String email;
   private String phone;
   private Integer  sex;
   private String birthday;
   private Integer lifeState;
   private String biography;
   private String address;
   private String headAddress;
   private long beginTime;
   private long updateTime;
}
