package com.travis.filesbottle.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travis.filesbottle.common.constant.TokenConstants;
import com.travis.filesbottle.common.dubboservice.auth.DubboCheckJwtAuthService;
import com.travis.filesbottle.common.dubboservice.auth.bo.DubboAuthUser;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.BizCodeUtil;
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

    @DubboReference
    private DubboCheckJwtAuthService dubboCheckJwtAuthService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        /**
         * 通过此方法修改ServerHttpRequest的属性信息。
         * mutate():返回一个构建器来改变这个请求的属性
         */
        ServerHttpRequest.Builder mutate = serverHttpRequest.mutate();
        String requestUrl = serverHttpRequest.getURI().getPath();

        // 跳过对允许路径请求的 token 检查。包括登录请求，因为登录请求是没有 token 的，是来申请 token 的。
        for (String path : ALLOW_PATH) {
            if (requestUrl.startsWith(path)) return chain.filter(exchange);
        }

        // 从HTTP请求头中获取JWT令牌
        String token = getToken(serverHttpRequest);
        // 判断token令牌是否为空
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, serverHttpResponse, BizCodeEnum.TOKEN_MISSION);
        }

        // 通过Dubbo远程对获取的token进行鉴权
        R<DubboAuthUser> jwtAuthResult = dubboCheckJwtAuthService.checkJwtAuth(token);

        // 根据鉴权失败原因返回相应的相应信息
        if (!BizCodeUtil.isCodeSuccess(jwtAuthResult.getCode())) {
            if (BizCodeUtil.getThreeCode(jwtAuthResult.getCode()).equals(BizCodeEnum.TOKEN_CHECK_FAILED.getCode())) {
                return unauthorizedResponse(exchange, serverHttpResponse, BizCodeEnum.TOKEN_CHECK_FAILED);
            }else if (BizCodeUtil.getThreeCode(jwtAuthResult.getCode()).equals(BizCodeEnum.TOKEN_EXPIRED.getCode())) {
                return unauthorizedResponse(exchange, serverHttpResponse, BizCodeEnum.TOKEN_EXPIRED);
            }else {
                /**
                 * 前端需要判断该响应码，更新请求头中的token信息。如果前端没有更新token，之后继续使用旧的token发送请求，则会提示token已过期
                 */
                return unauthorizedResponse(exchange, serverHttpResponse, BizCodeEnum.TOKEN_REFRESH);
            }
        }
        String userId = jwtAuthResult.getData().getUserId();
        String username = jwtAuthResult.getData().getUserName();
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
     * @MethodName urlEncode
     * @Description 对内容进行编码
     * @Author travis-wei
     * @Data 2023/4/7
     * @param str
     * @Return java.lang.String
     **/
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, String.valueOf(StandardCharsets.UTF_8));
        }
        catch (UnsupportedEncodingException e) {
            return StrUtil.EMPTY;
        }
    }

    /**
     * @MethodName getToken
     * @Description 从http request请求头中提取token
     * @Author travis-wei
     * @Data 2023/4/7
     * @param request
     * @Return java.lang.String
     **/
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }

    /**
     * @MethodName unauthorizedResponse
     * @Description 将 JWT 鉴权失败的消息响应给客户端
     * @Author travis-wei
     * @Data 2023/4/7
     * @param exchange
     * @param serverHttpResponse
     * @param bizCodeEnum
     * @Return reactor.core.publisher.Mono<java.lang.Void>
     **/
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, ServerHttpResponse serverHttpResponse, BizCodeEnum bizCodeEnum) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 指定编码，否则在浏览器中会出现中文乱码
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        R<?> responseResult = R.error(BizCodeEnum.MOUDLE_GATEWAY, bizCodeEnum);
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
