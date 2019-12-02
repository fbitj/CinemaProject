package com.stylefeng.guns.rest.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.dto.RegisterRequest;
import com.guns.service.user.UserService;
import com.guns.vo.BaseRespVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {
    @Reference(interfaceClass = UserService.class, check = false)
    private UserService userService;

    @RequestMapping("register")
    public BaseRespVO register(RegisterRequest registerRequest){
        return userService.register(registerRequest);
    }
}
