package com.stylefeng.guns.rest.modular.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.user.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "user")
public class UserController {

    @Reference(interfaceClass = UserService.class)
    private UserService userService;

    @RequestMapping(value = "aaaaaaaaaaa")
    public void aaaaaaaa() {
    }
}
