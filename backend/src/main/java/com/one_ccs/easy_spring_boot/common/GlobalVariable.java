package com.one_ccs.easy_spring_boot.common;

/**
 * GlobalVariable 全局变量类
 */
public class GlobalVariable {
    // JWT 秘钥
    public static final String JWT_SECRET = "b2bfe2b68eff9b86dd4ef33f7c3afb1bb";
    // JWT 过期时间 单位秒
    public static final Long EXPIRATION = 3600L * 24;
    // JWT 黑名单前缀
    public static final String JWT_BLACK_LIST_PREFIX = "jwt:blacklist:";
    // 跨域过滤器优先级
    public static final int ORDER_CORS = -102;
}
