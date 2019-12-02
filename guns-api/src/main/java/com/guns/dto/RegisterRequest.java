package com.guns.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterRequest implements Serializable {
    private static final long serialVersionUID = -6849794470755667710L;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String address;
}
