package com.travis.filesbottle.common.dubboservice.auth;

import com.travis.filesbottle.common.dubboservice.auth.bo.JwtProperties;

/**
 * @ClassName DubboJwtPropertiesService
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/6
 */
public interface DubboJwtPropertiesService {

    JwtProperties getJwtProperties();
}
