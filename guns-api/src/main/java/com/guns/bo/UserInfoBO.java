package com.guns.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoBO implements Serializable {

    private static final long serialVersionUID = -2849558277383772980L;

    private String username;

    private String password;

    private String email;

    private String mobile;

    private String address;
}
