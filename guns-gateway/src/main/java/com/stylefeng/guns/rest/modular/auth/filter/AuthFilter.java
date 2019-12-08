package com.stylefeng.guns.rest.modular.auth.filter;

import com.guns.vo.BaseRespVO;
import com.stylefeng.guns.core.base.tips.ErrorTip;
import com.stylefeng.guns.core.util.RenderUtil;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import io.jsonwebtoken.JwtException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 对客户端请求的jwt token验证过滤器
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:04
 */
public class AuthFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 排除不需要验证用户是否登录的接口
        String[] urls = jwtProperties.getUrls();
        String servletPath = request.getServletPath();
        for (String url : urls) {
            if (servletPath.contains(url)) {
                chain.doFilter(request, response);
                return;
            }
        }
        // 判断请求头中是否携带Authorization请求头字段，即token字段
        final String requestHeader = request.getHeader(jwtProperties.getHeader());
        if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
            RenderUtil.renderJson(response, BaseRespVO.buzError("用户尚未登录"));
            return;
        }
        String authToken = requestHeader.substring(7);
        // 通过token在redis中判断用户是否登录，若没有登录则直接返回业务异常
        Object o = redisTemplate.opsForValue().get(authToken);
        if (o == null) {  // token不存在或已过期
            RenderUtil.renderJson(response, BaseRespVO.buzError("用户尚未登录"));
            return;
        }
        // 登录成功则更新用户在Redis中的过期时间
        redisTemplate.expire(authToken, 10, TimeUnit.MINUTES);
        // 将token值保存在request域中
        request.setAttribute("token", authToken);
        chain.doFilter(request, response);
    }
}