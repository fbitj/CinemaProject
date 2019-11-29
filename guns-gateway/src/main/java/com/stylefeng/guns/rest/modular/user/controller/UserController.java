package com.stylefeng.guns.rest.modular.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.bo.UserInfoBO;
import com.guns.service.user.UserService;
import com.guns.vo.BaseRespVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "user")
public class UserController {

    @Reference(interfaceClass = UserService.class, check = false)
    private UserService userService;

    /**
     * 用户注册
     * @return
     */
    @RequestMapping(value = "register")
    public BaseRespVO UserRegister(UserInfoBO userInfoBO) {
        boolean exist = userService.isUsernameExist(userInfoBO.getUsername());
        if (exist){
            return BaseRespVO.buzError("用户已存在");
        }
        if(userInfoBO.getUsername()==null || userInfoBO.getPassword()==null){
            return BaseRespVO.buzError("用户名和密码不得为空");
        }
        int regist = userService.userRegist(userInfoBO);
        if (regist==1){
            return BaseRespVO.ok("注册成功");
        }
        return BaseRespVO.sysError();
    }

    /**
     * 用户名验证
     * @return
     */
    @RequestMapping(value = "check")
    public BaseRespVO UserRegister(String username) {
        boolean exist = userService.isUsernameExist(username);
        if (!exist){
            return BaseRespVO.ok("用户名不存在");
        }
        if (exist){
            return BaseRespVO.buzError("用户已经注册");
        }
        return BaseRespVO.sysError();
    }
}
