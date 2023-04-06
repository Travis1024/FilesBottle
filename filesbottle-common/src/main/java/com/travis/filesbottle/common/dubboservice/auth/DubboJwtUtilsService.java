package com.travis.filesbottle.common.dubboservice.auth;

/**
 * @ClassName DubboJwtUtilesService
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/6
 */
public interface DubboJwtUtilsService {
    boolean isTokenExpired(String token);
    String getUserIdFromToken(String token);
    String getUserNameFromToken(String token);

}
