package com.stylefeng.guns.rest.modular.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.service.user.UserService;
import com.guns.vo.BaseRespVO;
import com.guns.vo.UserCacheVO;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.auth.validator.IReqValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Resource(name = "simpleValidator")
    private IReqValidator reqValidator;

    @Reference(interfaceClass = UserService.class, check = false)
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "${jwt.auth-path}")
    public BaseRespVO createAuthenticationToken(AuthRequest authRequest) {

        // 根据数据库查询是否存在该用户
        UserCacheVO userCacheVO = userService.login(authRequest.getUserName(), authRequest.getPassword());
        // 若不存在该用户，返回业务异常
        if (userCacheVO == null) {
            return BaseRespVO.buzError("用户名或密码错误");
        }
        // 生成randomKey和token
        final String randomKey = jwtTokenUtil.getRandomKey();
        final String token = jwtTokenUtil.generateToken(authRequest.getUserName(), randomKey);
        // 在Redis中保存token和用户数据，key为token，用户数据为value
        redisTemplate.opsForValue().set(token, userCacheVO);
        redisTemplate.expire(token, 60*5, TimeUnit.SECONDS);
        // 返回正确的应答报文
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("randomKey", randomKey);
        dataMap.put("token", token);
        return BaseRespVO.ok(dataMap);
    }
}
