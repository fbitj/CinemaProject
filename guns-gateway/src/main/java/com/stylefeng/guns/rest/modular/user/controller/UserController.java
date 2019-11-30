package com.stylefeng.guns.rest.modular.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.bo.UserInfoBO;
import com.guns.service.user.UserService;
import com.guns.vo.BaseRespVO;

import com.guns.vo.UserCacheVO;
import com.guns.vo.UserInfoVo;
import com.stylefeng.guns.rest.common.exception.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;



@RestController
@RequestMapping(value = "user")
public class UserController {


    @Reference(interfaceClass = UserService.class, check = false)
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户注册
     * @return
     */
    @RequestMapping(value = "register")
    public BaseRespVO userRegister(UserInfoBO userInfoBO) {
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
    public BaseRespVO usernameCheck(String username) {
        boolean exist = userService.isUsernameExist(username);
        if (!exist){
            return BaseRespVO.ok("用户名不存在");
        }
        if (exist){
            return BaseRespVO.buzError("用户已经注册");
        }
        return BaseRespVO.sysError();
    }


    @RequestMapping(value = "getUserInfo")
    public BaseRespVO<UserInfoVo> getUserInfo(HttpServletRequest request) throws CustomException {
        String token = (String) request.getAttribute("token");
        UserCacheVO userCacheVO = (UserCacheVO) redisTemplate.opsForValue().get(token);
        BaseRespVO<UserInfoVo> repv = new BaseRespVO<>();
        String username = userCacheVO.getUserName();
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

    // 用户登出
    @RequestMapping("logout")
    public BaseRespVO logout(HttpServletRequest request) {
        // 从reqeust域中，获得请求头中携带的token信息
        String token = (String) request.getAttribute("token");
        // 在Redis中删除该用户信息
        Boolean delete = redisTemplate.delete(token);
        return BaseRespVO.ok("成功退出");
    }
}
