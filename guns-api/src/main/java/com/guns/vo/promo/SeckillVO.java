package com.guns.vo.promo;

import com.guns.vo.BaseRespVO;
import lombok.Data;

@Data
public class SeckillVO<T> extends BaseRespVO<T> {
    private static final long serialVersionUID = -6849794474667710L;

    private Integer nowPage;

    private Integer totalPage;
}
