package com.stylefeng.guns.rest.modular.auth.filter;

import com.guns.vo.BaseRespVO;
import com.stylefeng.guns.core.base.tips.ErrorTip;
import com.stylefeng.guns.core.util.RenderUtil;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.common.persistence.model.User;
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
import javax.servlet.http.Cookie;
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
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        /*if (request.getServletPath().equals("/" + jwtProperties.getAuthPath())) {
            chain.doFilter(request, response);
            return;
        }*/
        String url = jwtProperties.getUrl();
        String[] split = url.split(",");
        for (String s : split) {
            if (request.getServletPath().contains(s)) {
                chain.doFilter(request, response);
                return;
            }
        }
        /*if (url.contains(request.getServletPath())) {
            chain.doFilter(request, response);
            return;
        }*/

        final String requestHeader = request.getHeader(jwtProperties.getHeader());
        String authToken = null;
        if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
            //没有登录
            RenderUtil.renderJson(response, BaseRespVO.buzError("用户尚未登录"));
            return;
        }
        authToken = requestHeader.substring(7);
        //验证token是否过期
        //User user = (User) redisTemplate.opsForValue().get(authToken);
        Object o = redisTemplate.opsForValue().get(authToken);
        if (o == null) {
            BaseRespVO<Object> respVO = new BaseRespVO<>();
            respVO.setStatus(700);
            RenderUtil.renderJson(response, respVO);
//            RenderUtil.renderJson(response, BaseRespVO.buzError("用户尚未登录"));
            return;
        }

        //放行，更新redis过期时间
        redisTemplate.expire(authToken, 20, TimeUnit.MINUTES);
        chain.doFilter(request,response);
       /* if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7);

            //验证token是否过期,包含了验证jwt是否正确
            try {
                //boolean flag = jwtTokenUtil.isTokenExpired(authToken);
                User user = (User) redisTemplate.opsForValue().get(authToken);
                if (user != null) {
                    RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_EXPIRED.getCode(), BizExceptionEnum.TOKEN_EXPIRED.getMessage()));
                    return;
                }
            } catch (JwtException e) {
                //有异常就是token解析失败
                RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.TOKEN_ERROR.getMessage()));
                return;
            }
        } else {
            //header没有带Bearer字段
            RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.TOKEN_ERROR.getMessage()));
            return;
        }
        chain.doFilter(request, response);*/
    }
}