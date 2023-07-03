package com.travis.filesbottle.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.travis.filesbottle.auth.config.JwtPropertiesConfiguration;
import com.travis.filesbottle.auth.utils.JwtTokenUtil;
import com.travis.filesbottle.common.constant.JwtConstants;
import com.travis.filesbottle.common.constant.TokenConstants;
import com.travis.filesbottle.common.dubboservice.auth.bo.DubboAuthUser;
import com.travis.filesbottle.common.dubboservice.member.DubboUserInfoService;
import com.travis.filesbottle.common.dubboservice.member.bo.DubboMemberUser;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName JwtAuthController
 * @Description 单点登录鉴权模块
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/3
 */
@Slf4j
@Api(tags = "单点登录Controller")
@RestController
@RequestMapping("/sso")
public class JwtAuthController {

    @Autowired
    private JwtPropertiesConfiguration jwtProperties;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @DubboReference
    private DubboUserInfoService dubboUserInfoService;

//    @ApiOperation(value = "测试dubbo")
//    @PostMapping("/testdubbo")
//    public R<?> testDubbo(@RequestParam(name = "userid") String userId) {
//        DubboMemberUser userBasicInfo = dubboUserInfoService.getUserBasicInfo(userId);
//        return R.success(userBasicInfo);
//    }


    @ApiOperation(value = "nginx鉴权请求服务")
    @GetMapping("/check")
    public void nginxAuth(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        log.info("token:" + token);
        if (StrUtil.isEmpty(token)) {
            response.setStatus(401);
            return;
        }
        // 验证 token 里面的 userId 是否为空
        String userId = JwtTokenUtil.getUserIdFromToken(token);
        if (StrUtil.isEmpty(userId)) {
            response.setStatus(401);
            return;
        }
        // 对Token解签名，并验证Token是否过期
        boolean isJwtNotValid = JwtTokenUtil.isTokenExpired(token);

        // 如果token已经过期，判断token能够被刷新
        if(isJwtNotValid){
            response.setStatus(401);
            return;
        }
        response.setStatus(200);
    }


    /**
     * @MethodName login
     * @Description 使用用户名密码换 JWT 令牌
     * @Author travis-wei
     * @Data 2023/4/6
     * @param userId
     * @param password
     * @Return com.travis.filesbottle.common.utils.R<?>
     **/
    @ApiOperation(value = "单点登录-登录接口")
    @PostMapping("/login")
    public R<?> login(@RequestParam("userid") String userId, @RequestParam("password") String password){

        // 如果用户名和密码为空
        if(StrUtil.isEmpty(userId) || StrUtil.isEmpty(password)){
            return R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "用户名和密码不能为空!");
        }

        // 首先判断用户是否已经登录
        if (JwtTokenUtil.isUserExistInCache(userId)) {
            // 判断该用户的token是否已经过期
            String cacheToken = JwtTokenUtil.getCacheTokenByUserId(userId);
            if (!JwtTokenUtil.isTokenExpired(cacheToken)) {
                return R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "该用户已经登录，请勿重复登录!");
            }
        }

        // 根据 userId 去数据库查找该用户  (远程调用)
        DubboMemberUser userBasicInfo = dubboUserInfoService.getUserBasicInfo(userId);
        if(userBasicInfo != null){
            // 将数据库的加密密码与用户明文密码做比对
            boolean isAuthenticated = passwordEncoder.matches(password, userBasicInfo.getUserPassword());
            // 如果密码匹配成功
            if(isAuthenticated){
                // 通过 JwtTokenUtil 生成 JWT 令牌和刷新令牌
                Map<String, Object> tokenMap = JwtTokenUtil.generateTokenAndRefreshToken(userId, userBasicInfo.getUserName());
                return R.success(tokenMap);
            }
            // 如果密码匹配失败
            return R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "用户名或密码错误!");
        }
        // 如果未找到用户
        return R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "用户不存在,请检查用户ID!");
    }

    /**
     * @MethodName refreshToken
     * @Description 刷新JWT令牌,用旧的令牌换新的令牌
     * @Author travis-wei
     * @Data 2023/4/3
     * @param token 旧的token令牌，不是refreshtoken
     * @Return reactor.core.publisher.Mono<com.travis.filesbottle.common.utils.R<?>>
     **/
    @ApiOperation(value = "后台为客户端提供的主动刷新令牌接口")
    @GetMapping("/refreshtoken")
    public Mono<R<?>> refreshToken(@RequestHeader("${filesbottle.jwt.header}") String token){
        // 删除token的前缀"Bearer "
        if (!StrUtil.isEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replace(TokenConstants.PREFIX, "");
        }

        // 判断token为空
        if (StrUtil.isEmpty(token)) {
            return Mono.create(callback -> callback.success(
                    R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "token为空,请检查!")
            ));
        }

        // 验证 token 里面的 userId 是否为空
        String userId = JwtTokenUtil.getUserIdFromToken(token);
        String username = JwtTokenUtil.getUserNameFromToken(token);
        if (StrUtil.isEmpty(userId)) {
            return Mono.create(callback -> callback.success(
                    R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "token验证失败!")
            ));
        }

        // 对Token解签名，并验证Token是否过期
        boolean isJwtNotValid = JwtTokenUtil.isTokenExpired(token);
        if(isJwtNotValid){
            // token过期，判断redis中是否还有token,如果有的话证明符合刷新条件
            if (!JwtTokenUtil.isTokenExistInCache(token)) {
                return Mono.create(callback -> callback.success(
                        R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "刷新失败!已超过刷新期限!")
                ));
            }
        }

        // 当为"一次性刷新"时，对refreshToken进行判断
        if (jwtProperties.getOneRefreshToken()) {
            String refreshToken = (String) redisTemplate.opsForHash().get(JwtConstants.JWT_CACHE_KEY + userId, JwtConstants.REFRESH_TOKEN);
            if (StrUtil.isEmpty(refreshToken) || refreshToken.equals(TokenConstants.DEPRECATED)) {
                return Mono.create(callback -> callback.success(
                        R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "刷新失败!已经进行过刷新!")
                ));
            }
        }

        // 到这一步表明：token未过期 或者 在refreshToken时间范围内
        // 一次性令牌策略的处理逻辑
        if (jwtProperties.getOneRefreshToken()) {
            Map<String, Object> tokenMap = JwtTokenUtil.onceRefreshToken(token);
            return Mono.create(callback -> callback.success(
                    R.success("令牌刷新成功，注意一次性刷新策略！", tokenMap)
            ));
        }
        // 永久性令牌策略的处理逻辑
        JwtTokenUtil.cacheDeleteToken(userId);
        Map<String, Object> newTokenMap = JwtTokenUtil.generateTokenAndRefreshToken(userId, username);
        return Mono.create(callback -> callback.success(
                R.success("令牌刷新成功！", newTokenMap)
        ));

    }

    /**
     * 登出，删除 redis 中的 accessToken 和 refreshToken
     * 只保证 refreshToken 不能使用，accessToken 还是能使用的。
     * 如果用户拿到了之前的 accessToken，则可以一直使用到过期，但是因为 refreshToken 已经无法使用了，所以保证了 accessToken 的时效性。
     * 下次登录时，需要重新获取新的 accessToken 和 refreshToken，这样才能利用 refreshToken 进行续期。
     */
    @PostMapping("/logout")
    public Mono<R<?>> logout(@RequestParam("userid") String userid){

        boolean logoutResult = JwtTokenUtil.cacheDeleteToken(userid);
        if (!logoutResult) {
            return Mono.create(callback -> callback.success(
                    R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.BAD_REQUEST, "用户退出失败!")
            ));
        }

        return Mono.create(callback -> callback.success(
                R.success("用户成功退出!")
        ));
    }


}
