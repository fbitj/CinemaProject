package com.stylefeng.guns.rest.modular.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.user.UserService;
import com.guns.vo.BaseRespVO;

import com.guns.vo.UserInfoVo;
import com.stylefeng.guns.rest.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import java.util.Date;

/**
 * @author zhu rui
 * @version 1.0
 * @date 2019/11/28 21:31
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Reference(interfaceClass = UserService.class)
    private UserService userService;

    @RequestMapping(value = "getUserInfo")
    public BaseRespVO<UserInfoVo> getUserInfo() throws CustomException {
        BaseRespVO<UserInfoVo> repv = new BaseRespVO<>();
        String username = "admin";
        UserInfoVo userInfoVo = userService.selectUserInfoVo(username);
        if (userInfoVo == null) {
            throw new CustomException(1, "查询失败,用户尚未登录");
        }
        repv.setData(userInfoVo);
        repv.setStatus(0);
        repv.setMsg("成功");
        return repv;

    }

    @RequestMapping("updateUserInfo")
    public BaseRespVO<UserInfoVo> updateUserInfo(UserInfoVo userInfoVo) throws CustomException {
        BaseRespVO<UserInfoVo> respVo = new BaseRespVO<>();
        int i = userService.updateUserInfo(userInfoVo);
        if (i == 1) {
            //表示可以进行修改成功
            userInfoVo.setUpdateTime(new Date().getTime());
            respVo.setData(userInfoVo);
            respVo.setStatus(0);
            respVo.setMsg("成功");
            return respVo;
        } else if (i == 0) {
            throw new CustomException(1, "用户修改失败");
        } else {
            throw new CustomException(999, "系统出现异常，请联系管理员");
        }
    }

}
