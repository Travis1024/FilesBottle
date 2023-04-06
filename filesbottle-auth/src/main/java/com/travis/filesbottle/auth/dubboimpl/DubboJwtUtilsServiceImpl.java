package com.travis.filesbottle.auth.dubboimpl;

import com.travis.filesbottle.auth.utils.JwtTokenUtil;
import com.travis.filesbottle.common.dubboservice.auth.DubboJwtUtilsService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @ClassName DubboJwtUtilsServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/6
 */
@DubboService
public class DubboJwtUtilsServiceImpl implements DubboJwtUtilsService {

    @Override
    public boolean isTokenExpired(String token) {
        return JwtTokenUtil.isTokenExpired(token);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return JwtTokenUtil.getUserIdFromToken(token);
    }

    @Override
    public String getUserNameFromToken(String token) {
        return JwtTokenUtil.getUserNameFromToken(token);
    }
}
