package com.guns.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by fwj on 2019-11-28.
 */

/**
 * 公用的响应类
 * @param <T>
 */
@Data
public class BaseRespVO<T> implements Serializable {

    private int status;

    private T data;

    private String msg;

    private String imgPre;

    /**
     * 正常应答，status为0，且msg内容为空
     * @param data 要返回的数据
     * @return 正常响应对象
     */
    public static BaseRespVO ok (Object data) {
        BaseRespVO baseRespVO = new BaseRespVO();
        baseRespVO.setStatus(0);
        baseRespVO.setData(data);
        return baseRespVO;
    }

    /**
     * 正常应答，status为0
     * @param data 要返回的数据
     * @param msg 正常返回的msg信息
     * @return 正常响应对象
     */
    public static BaseRespVO ok (Object data, String msg) {
        BaseRespVO baseRespVO = new BaseRespVO();
        baseRespVO.setStatus(0);
        baseRespVO.setData(data);
        baseRespVO.setMsg(msg);
        return baseRespVO;
    }

    /**
     * 业务异常，status为1
     * @param msg 异常信息
     * @return 业务异常响应对象
     */
    public static BaseRespVO buzError(String msg) {
        BaseRespVO baseRespVO = new BaseRespVO();
        baseRespVO.setStatus(1);
        baseRespVO.setMsg(msg);
        return baseRespVO;
    }

    /**
     * 系统异常，status为999
     * @return 系统异常响应对象
     */
    public static BaseRespVO sysError() {
        BaseRespVO baseRespVO = new BaseRespVO();
        baseRespVO.setStatus(999);
        baseRespVO.setMsg("系统出现异常，请联系管理员");
        return baseRespVO;
    }
}
