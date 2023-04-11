package com.travis.filesbottle.auth.utils;

import cn.hutool.core.util.StrUtil;
import com.travis.filesbottle.auth.config.JwtPropertiesConfiguration;
import com.travis.filesbottle.common.constant.JwtConstants;
import com.travis.filesbottle.common.constant.TokenConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName JwtTokenUtil
 * @Description Jwt Token工具类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/3
 */
@Component
public class JwtTokenUtil {

    private static RedisTemplate<String, Object> redisTemplate;
    private static JwtPropertiesConfiguration jwtProperties;

    @Autowired
    public JwtTokenUtil(RedisTemplate<String, Object> redisTemplate, JwtPropertiesConfiguration jwtProperties) {
        JwtTokenUtil.redisTemplate = redisTemplate;
        JwtTokenUtil.jwtProperties = jwtProperties;
    }

    /**
     * @MethodName generateTokenAndRefreshToken
     * @Description 生成新的token令牌
     * @Author travis-wei
     * @Data 2023/4/3
     * @param userId
     * @param username
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     **/
    public static Map<String, Object> generateTokenAndRefreshToken(String userId, String username) {

        // 生成令牌Token
        String accessToken = generateToken(userId, username, null);
        // 生成刷新令牌RefreshToken
        String refreshToken = generateRefreshToken(userId, username, null);

        // 初始容量设置公式：键值对数量/0.75+1, 设置完之后初始化是为2的最小次幂
        // 3 / 0.75 + 1 --> 8
        HashMap<String, Object> tokenMap = new HashMap<>(5);
        tokenMap.put(JwtConstants.ACCESS_TOKEN, accessToken);
        tokenMap.put(JwtConstants.REFRESH_TOKEN, refreshToken);
        tokenMap.put(JwtConstants.EXPIRE_IN, jwtProperties.getExpiration());

        cacheAddToken(userId, tokenMap);
        return tokenMap;
    }

    /**
     * @MethodName onceRefreshToken
     * @Description 一次性刷新令牌，并将刷新令牌弃用
     * @Author travis-wei
     * @Data 2023/4/4
     * @param token
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     **/
    public static Map<String, Object> onceRefreshToken(String token) {
        Claims claimsFromToken = getClaimsFromToken(token);
        String userId = getUserIdFromToken(token);
        String newToken = generateToken(claimsFromToken);

        HashMap<String, Object> newTokenMap = new HashMap<>(5);
        newTokenMap.put(JwtConstants.ACCESS_TOKEN, newToken);
        newTokenMap.put(JwtConstants.REFRESH_TOKEN, TokenConstants.DEPRECATED);
        newTokenMap.put(JwtConstants.EXPIRE_IN, jwtProperties.getExpiration());

        cacheDeleteToken(userId);

        redisTemplate.opsForHash().put(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.ACCESS_TOKEN, newTokenMap.get(JwtConstants.ACCESS_TOKEN));
        redisTemplate.opsForHash().put(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.REFRESH_TOKEN, newTokenMap.get(JwtConstants.REFRESH_TOKEN));
        redisTemplate.expire(JwtConstants.JWT_CACHE_KEY + userId, jwtProperties.getExpiration(), TimeUnit.MILLISECONDS);

        return newTokenMap;
    }


    /**
     * @MethodName cacheAddToken
     * @Description 向redis缓存令牌
     * @Author travis-wei
     * @Data 2023/4/3
     * @param userId
     * @param tokenMap
     * @Return void
     **/
    private static void cacheAddToken(String userId, Map<String, Object> tokenMap) {
        redisTemplate.opsForHash().put(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.ACCESS_TOKEN, tokenMap.get(JwtConstants.ACCESS_TOKEN));
        redisTemplate.opsForHash().put(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.REFRESH_TOKEN, tokenMap.get(JwtConstants.REFRESH_TOKEN));
        redisTemplate.expire(JwtConstants.JWT_CACHE_KEY + userId, jwtProperties.getExpiration() * jwtProperties.getMultipleRefreshToken(), TimeUnit.MILLISECONDS);
    }

    /**
     * @MethodName cacheDeleteToken
     * @Description 根据用户id删除redis缓存中的令牌
     * @Author travis-wei
     * @Data 2023/4/3
     * @param userId
     * @Return boolean
     **/
    public static boolean cacheDeleteToken(String userId) {
        return Boolean.TRUE.equals(redisTemplate.delete(JwtConstants.JWT_CACHE_KEY + userId));
    }

    /**
     * @MethodName generateToken
     * @Description 根据用户ID、用户名、负载生成令牌
     * @Author travis-wei
     * @Data 2023/4/3
     * @param userId	用户ID
     * @param username	用户名
     * @param payloads	负载
     * @Return java.lang.String
     **/
    public static String generateToken(String userId, String username, Map<String,String> payloads) {
        Map<String, Object> claims = buildClaims(userId, username, payloads);
        return generateToken(claims);
    }

    /**
     * @MethodName generateToken
     * @Description 根据负载（payloads）中的用户实体信息（claims）生成令牌
     * @Author travis-wei
     * @Data 2023/4/3
     * @param claims
     * @Return java.lang.String
     **/
    private static String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
    }

    /**
     * @MethodName generateRefreshToken
     * @Description 根据用户ID、用户名、负载，生成刷新令牌（refreshtoken），有效期是令牌的 x 倍（jwtProperties.getMultipleRefreshToken()）
     * @Author travis-wei
     * @Data 2023/4/3
     * @param userId
     * @param username
     * @param payloads
     * @Return java.lang.String
     **/
    public static String generateRefreshToken(String userId, String username, Map<String,String> payloads) {
        Map<String, Object> claims = buildClaims(userId, username, payloads);
        return generateRefreshToken(claims);
    }

    /**
     * @MethodName generateRefreshToken
     * @Description 根据负载中的用户实体信息，生成刷新令牌（refreshtoken），有效期是令牌的 x 倍（jwtProperties.getMultipleRefreshToken()）
     * @Author travis-wei
     * @Data 2023/4/3
     * @param claims
     * @Return java.lang.String
     **/
    private static String generateRefreshToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getExpiration() * jwtProperties.getMultipleRefreshToken());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
    }

    /**
     * @MethodName buildClaims
     * @Description 构建负载（payloads）的用户实体信息（claims）
     * @Author travis-wei
     * @Data 2023/4/3
     * @param userId    用户ID
     * @param username  用户名
     * @param payloads	负载
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     **/
    private static Map<String, Object> buildClaims(String userId, String username, Map<String, String> payloads) {
        int payloadSizes = payloads == null? 0 : payloads.size();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("username", username);
        claims.put("created", new Date());

        if(payloadSizes > 0){
            claims.putAll(payloads);
        }
        return claims;
    }

    /**
     * @MethodName getUserIdFromRequest
     * @Description 从请求（request）中提取用户ID
     * @Author travis-wei
     * @Data 2023/4/3
     * @param request
     * @Return java.lang.String
     **/
    public static String getUserIdFromRequest(HttpServletRequest request) {
        return request.getHeader(JwtConstants.USER_ID);
    }

    /**
     * @MethodName getUserIdFromToken
     * @Description 从令牌中提取用户ID
     * @Author travis-wei
     * @Data 2023/4/3
     * @param token
     * @Return java.lang.String
     **/
    public static String getUserIdFromToken(String token) {
        String userId;
        try {
            Claims claims = getClaimsFromToken(token);
            // 等同于 userId = (String) claims.get("sub");
            userId = claims.getSubject();
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * @MethodName getUserNameFromToken
     * @Description 从令牌中提取用户名
     * @Author travis-wei
     * @Data 2023/4/3
     * @param token
     * @Return java.lang.String
     **/
    public static String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = (String) claims.get(JwtConstants.USER_NAME);
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * @MethodName getClaimsFromToken
     * @Description 从令牌中获取数据声明,验证 JWT 签名
     * @Author travis-wei
     * @Data 2023/4/3
     * @param token
     * @Return io.jsonwebtoken.Claims
     **/
    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e1) {
            claims = e1.getClaims();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * @MethodName getRefreshTokenByToken
     * @Description 根据token获取缓存中的refreshtoken
     * @Author travis-wei
     * @Data 2023/4/4
     * @param token
     * @Return java.lang.String
     **/
    private static String getRefreshTokenByToken(String token) {
        String userId = getUserIdFromToken(token);
        return (String)redisTemplate.opsForHash().get(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.REFRESH_TOKEN);
    }

    /**
     * @MethodName isTokenExistInCache
     * @Description 判断redis中是否有该令牌
     * @Author travis-wei
     * @Data 2023/4/3
     * @param token
     * @Return java.lang.Boolean｜true：存在；false：不存在
     **/
    public static Boolean isTokenExistInCache(String token) {
        String userId = getUserIdFromToken(token);
        String cacheToken = (String)redisTemplate.opsForHash().get(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.ACCESS_TOKEN);
        return cacheToken != null && cacheToken.equals(token);
    }

    /**
     * @MethodName isUserExistInCache
     * @Description 根据用户名，获取redis中的token信息
     * @Author travis-wei
     * @Data 2023/4/7
     * @param userId
     * @Return java.lang.Boolean
     **/
    public static String getCacheTokenByUserId(String userId) {
        return (String)redisTemplate.opsForHash().get(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.ACCESS_TOKEN);
    }

    /**
     * @MethodName isUserExistInCache
     * @Description 判断redis中是否存在该用户的token信息
     * @Author travis-wei
     * @Data 2023/4/7
     * @param
     * @Return java.lang.String
     **/
    public static Boolean isUserExistInCache(String userId) {
        String token = getCacheTokenByUserId(userId);
        return !StrUtil.isEmpty(token);
    }

    /**
     * @MethodName isTokenExpired
     * @Description 判断令牌是否过期
     * @Author travis-wei
     * @Data 2023/4/3
     * @param token
     * @Return java.lang.Boolean ｜ true：过期；false：未过期；
     **/
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            //验证 JWT 签名失败等同于令牌过期
            return true;
        }
    }


    /**
     * @MethodName isTokenCouldRefresh
     * @Description 判断令牌能否被刷新
     * @Author travis-wei
     * @Data 2023/4/4
     * @param token
     * @Return java.lang.Boolean
     **/
    public static Boolean isTokenCouldRefresh(String token) {
        // 如果令牌在redis中存在
        if (isTokenExistInCache(token)) {
            if (!jwtProperties.getOneRefreshToken()) {
                return true;
            }
            // 一次性刷新
            // 缓存令牌没有被弃用
            if (!TokenConstants.DEPRECATED.equals(getRefreshTokenByToken(token))) {
                return true;
            }
        }
        // 令牌在redis中不存在
        return false;
    }

    /**
     * @MethodName isOnceRefresh
     * @Description 是否为一次性刷新令牌策略
     * @Author travis-wei
     * @Data 2023/4/7
     * @param
     * @Return java.lang.Boolean
     **/
    public static Boolean isOnceRefresh() {
        return jwtProperties.getOneRefreshToken();
    }
}
