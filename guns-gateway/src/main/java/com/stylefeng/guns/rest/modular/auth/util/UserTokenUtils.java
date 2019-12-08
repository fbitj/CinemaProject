package com.stylefeng.guns.rest.modular.auth.util;

import com.guns.vo.UserCacheVO;
import com.stylefeng.guns.rest.common.exception.CustomException;
import com.stylefeng.guns.rest.common.exception.TokenException;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserTokenUtils {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtProperties properties;

    private static final Integer expireTime = 60 * 60;

    public Integer getUserId(HttpServletRequest request) {
        String requestHeader = request.getHeader(properties.getHeader());
        if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
            // token不正确
            log.info("获取用户信息失败，token不合法！token:{}",requestHeader);
            throw new TokenException();
        }
        String token = requestHeader.substring(7);
        UserCacheVO user = (UserCacheVO) redisTemplate.opsForValue().get(token);
        if (user == null){
            log.info("用户token已失效！请重新登陆！");
            throw new TokenException();
        }
        // 刷新redis
        redisTemplate.opsForValue().set(token, user, expireTime, TimeUnit.SECONDS);
        return user.getUuid();
    }
}
