package com.travis.filesbottle.auth.dubboimpl;

import com.travis.filesbottle.auth.config.JwtPropertiesConfiguration;
import com.travis.filesbottle.common.dubboservice.auth.DubboCreateOnceTokenService;
import com.travis.filesbottle.common.utils.R;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DubboCreateOnceTokenServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/5
 */
@Slf4j
@DubboService
@Service
public class DubboCreateOnceTokenServiceImpl implements DubboCreateOnceTokenService {

    private static final String keyPrefix = "jwt:hlsToken:";

    @Autowired
    private JwtPropertiesConfiguration jwtProperties;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public R<?> createOnceToken(String gridFsId, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("created", new Date());

        // 过期时间设定为半个小时
        Date expirationDate = new Date(System.currentTimeMillis() + 1800000);
        // 生成 token
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
        String key = keyPrefix + gridFsId;

        redisTemplate.boundSetOps(key).add(token);
        // 更新 key 的过期时间
        redisTemplate.expire(key, 1800000, TimeUnit.MILLISECONDS);

        return R.success(token);
    }
}
