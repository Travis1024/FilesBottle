package com.travis.filesbottle.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travis.filesbottle.common.constant.TokenConstants;
import com.travis.filesbottle.common.dubboservice.auth.DubboJwtPropertiesService;
import com.travis.filesbottle.common.dubboservice.auth.DubboJwtUtilsService;
import com.travis.filesbottle.common.dubboservice.auth.bo.JwtProperties;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName CheckJwtAuthFilter
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/6
 */
@Configuration
@Slf4j
public class CheckJwtAuthFilter implements GlobalFilter, Ordered {

    // 设置不需要鉴权的URL路径
    public static final List<String> ALLOW_PATH = new ArrayList<>(Arrays.asList("/api/auth/sso/login", "/knife4j", "/api/member/druid"));
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";
    public static final String FROM_SOURCE = "from-source";

    private JwtProperties jwtProperties = null;

    @DubboReference
    private DubboJwtPropertiesService dubboJwtPropertiesService;
    @DubboReference
    private DubboJwtUtilsService dubboJwtUtilsService;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        /**
         * 通过此方法修改ServerHttpRequest的属性信息
         */
        ServerHttpRequest.Builder mutate = serverHttpRequest.mutate();
        String requestUrl = serverHttpRequest.getURI().getPath();

        // 跳过对允许路径请求的 token 检查。包括登录请求，因为登录请求是没有 token 的，是来申请 token 的。
        for (String path : ALLOW_PATH) {
            if (requestUrl.startsWith(path)) return chain.filter(exchange);
        }

        // 通过Dubbo远程获取jwtproperties配置信息
        jwtProperties = dubboJwtPropertiesService.getJwtProperties();

        // 从 HTTP 请求头中获取 JWT 令牌
        String token = getToken(serverHttpRequest);
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, serverHttpResponse, BizCodeEnum.TOKEN_MISSION);
        }

        // 对Token解签名，并验证Token是否过期
        boolean isJwtNotValid = dubboJwtUtilsService.isTokenExpired(token);
        if(isJwtNotValid){
            return unauthorizedResponse(exchange, serverHttpResponse, BizCodeEnum.TOKEN_EXPIRED);
        }

        // 验证 token 里面的 userId 是否为空
        String userId = dubboJwtUtilsService.getUserIdFromToken(token);
        String username = dubboJwtUtilsService.getUserNameFromToken(token);
        if (StrUtil.isEmpty(userId)) {
            return unauthorizedResponse(exchange, serverHttpResponse, BizCodeEnum.TOKEN_CHECK_FAILED);
        }

        // 设置用户信息到请求
        addHeader(mutate, USER_ID, userId);
        addHeader(mutate, USER_NAME, username);
        // 内部请求来源参数清除
        removeHeader(mutate, FROM_SOURCE);
        removeHeader(mutate, TokenConstants.AUTHENTICATION);
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    private void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    /**
     * 内容编码
     *
     * @param str 内容
     * @return 编码后的内容
     */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, String.valueOf(StandardCharsets.UTF_8));
        }
        catch (UnsupportedEncodingException e) {
            return StrUtil.EMPTY;
        }
    }

    /**
     * 获取请求token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(jwtProperties.getHeader());
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }

    /**
     * 将 JWT 鉴权失败的消息响应给客户端
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, ServerHttpResponse serverHttpResponse, BizCodeEnum bizCodeEnum) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 指定编码，否则在浏览器中会出现中文乱码
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        R<?> responseResult = R.error(BizCodeEnum.MOUDLE_GATEWAY, bizCodeEnum);
//        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONStringWithDateFormat(responseResult, JSON.DEFFAULT_DATE_FORMAT).getBytes(StandardCharsets.UTF_8));
        DataBuffer dataBuffer = null;
        try {
            dataBuffer = serverHttpResponse.bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(responseResult));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return serverHttpResponse.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
