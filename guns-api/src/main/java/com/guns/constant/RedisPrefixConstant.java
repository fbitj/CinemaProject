package com.guns.constant;

public class RedisPrefixConstant {

    // 秒杀库存前缀
    public static final String SEC_KILL_STOCK_PROMOID = "sec_kill_stock_promo_";

    // 秒杀库存售罄前缀
    public static final String SEC_KILL_NULL_STOCK = "stock_null_promo_";

    // 秒杀令牌token存放前缀
    public static final String SEC_KILL_TOKEN_PREFIX = "sec_kill_token_primo_user_";

    // 秒杀令牌数量限制缓存前缀
    public static final String SEC_KILL_TOKEN_NUMBER_LIMIT = "sec_kill_token_number_limit_promo_";

    // 秒杀令牌数量是订单的几倍
    public static final Integer SEC_KILL_TOKEN_TIMES = 5;

}
