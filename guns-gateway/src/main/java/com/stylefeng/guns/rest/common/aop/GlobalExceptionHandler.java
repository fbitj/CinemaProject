package com.stylefeng.guns.rest.common.aop;

import com.guns.vo.BaseRespVO;
import com.stylefeng.guns.core.aop.BaseControllerExceptionHandler;
import com.stylefeng.guns.core.base.tips.ErrorTip;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.common.exception.CustomException;
import com.stylefeng.guns.rest.common.exception.TokenException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午3:19:56
 */
@ControllerAdvice
public class GlobalExceptionHandler extends BaseControllerExceptionHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 拦截jwt相关异常
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorTip jwtException(JwtException e) {
        return new ErrorTip(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.TOKEN_ERROR.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public BaseRespVO solveCustomException(CustomException e){
        BaseRespVO<Object> respVO = new BaseRespVO<>();
        respVO.setStatus(e.getStatus());
        respVO.setMsg(e.getMessage());
        return respVO;
    }

    @ExceptionHandler(TokenException.class)
    @ResponseBody
    public ErrorTip solveTokenException(TokenException e){
        return new ErrorTip(BizExceptionEnum.TOKEN_EXPIRED.getCode(),BizExceptionEnum.TOKEN_EXPIRED.getMessage());
    }
}
