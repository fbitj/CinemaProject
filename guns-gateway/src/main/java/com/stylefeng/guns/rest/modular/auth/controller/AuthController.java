package com.stylefeng.guns.rest.modular.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guns.bo.User;
import com.guns.service.user.UserService;
import com.guns.vo.BaseRespVO;
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
import java.util.concurrent.TimeUnit;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Reference(interfaceClass = UserService.class, check = false)
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Resource(name = "simpleValidator")
    private IReqValidator reqValidator;

    @RequestMapping(value = "${jwt.auth-path}")
//    public ResponseEntity<?> createAuthenticationToken(AuthRequest authRequest) {
    public BaseRespVO createAuthenticationToken(AuthRequest authRequest) {

        //boolean validate = reqValidator.validate(authRequest);
        String userName = authRequest.getUserName();
        String password = authRequest.getPassword();

        //调用数据库进行比对
        User user = userService.checkUserIsExit(userName, password);
        if (user != null) {
            //认证通过
            final String randomKey = jwtTokenUtil.getRandomKey();
            final String token = jwtTokenUtil.generateToken(authRequest.getUserName(), randomKey);

            //redisTemplate.opsForValue().set(token, user);
            redisTemplate.opsForValue().set(token, user.getUuid());
            //设置过期时间
            redisTemplate.expire(token, 20, TimeUnit.MINUTES);
            //return ResponseEntity.ok(new AuthResponse(token, randomKey));
            return BaseRespVO.ok(new AuthResponse(token, randomKey));
        } else {
            throw new GunsException(BizExceptionEnum.AUTH_REQUEST_ERROR);
        }
    }
}
