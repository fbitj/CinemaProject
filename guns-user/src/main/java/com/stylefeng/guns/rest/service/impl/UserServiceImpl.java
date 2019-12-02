package com.stylefeng.guns.rest.service.impl;

import com.guns.dto.RegisterRequest;
import com.guns.service.user.UserService;
import com.guns.vo.BaseRespVO;

public class UserServiceImpl implements UserService {
    @Override
    public BaseRespVO register(RegisterRequest registerRequest) {
        //1.验证用户名是否被注册——调用用户名验证接口
        //2.否：添加到数据库并完成自动登陆
        //3.返回
        return new BaseRespVO();
    }

    /**
     * 实现用户登陆功能
     * @param username
     * @param password
     * @return
     */
    @Override
    public BaseRespVO login(String username, String password) {
        // 验证用户token有效
        // 登陆
        // 返回
        return new BaseRespVO();
    }
}
