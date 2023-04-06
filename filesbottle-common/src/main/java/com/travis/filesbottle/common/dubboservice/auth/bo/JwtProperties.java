package com.travis.filesbottle.common.dubboservice.auth.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName JwtProperties
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/6
 */
@Data
public class JwtProperties implements Serializable {
    /**
     * 是否开启JWT，即注入相关的类对象
     */
    private Boolean enabled;
    /**
     * JWT 密钥
     */
    private String secret;
    /**
     * accessToken 有效时间
     */
    private Long expiration;
    /**
     * 前端向后端传递JWT时使用HTTP的header名称，前后端要统一
     */
    private String header;
    /**
     * 用户登录-用户名参数名称
     */
    private String userParamName = "userId";
    /**
     * 用户登录-密码参数名称
     */
    private String pwdParamName = "password";
    /**
     * 是否为一次性刷新令牌
     */
    private Boolean oneRefreshToken = false;
    /**
     * 刷新令牌有效期为单位有效期的倍数（2，表示为2 * expiration）
     */
    private Integer multipleRefreshToken;
}
