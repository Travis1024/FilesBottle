package com.travis.filesbottle.auth.dubboimpl;

import com.travis.filesbottle.auth.config.JwtPropertiesConfiguration;
import com.travis.filesbottle.common.dubboservice.auth.DubboJwtPropertiesService;
import com.travis.filesbottle.common.dubboservice.auth.bo.JwtProperties;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName DubboJwtPropertiesServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/6
 */
@DubboService
public class DubboJwtPropertiesServiceImpl implements DubboJwtPropertiesService {

    @Autowired
    private JwtPropertiesConfiguration jwtPropertiesConfiguration;

    @Override
    public JwtProperties getJwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setEnabled(jwtPropertiesConfiguration.getEnabled());
        jwtProperties.setExpiration(jwtPropertiesConfiguration.getExpiration());
        jwtProperties.setHeader(jwtPropertiesConfiguration.getHeader());
        jwtProperties.setSecret(jwtPropertiesConfiguration.getSecret());
        jwtProperties.setMultipleRefreshToken(jwtPropertiesConfiguration.getMultipleRefreshToken());
        jwtProperties.setOneRefreshToken(jwtPropertiesConfiguration.getOneRefreshToken());
        jwtProperties.setPwdParamName(jwtPropertiesConfiguration.getPwdParamName());
        jwtProperties.setUserParamName(jwtPropertiesConfiguration.getUserParamName());
        return jwtProperties;
    }
}
