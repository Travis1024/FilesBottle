package com.travis.filesbottle.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.sun.net.httpserver.HttpExchange;
import com.travis.filesbottle.auth.utils.JwtTokenUtil;
import com.travis.filesbottle.common.dubboservice.document.DubboDocEncKeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * @ClassName FfmpegAuthController
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/5
 */
@Slf4j
@Api(tags = "视频文件鉴权Controller")
@RestController
@RequestMapping("/ffmpeg")
public class FfmpegAuthController {

    private static final String keyPrefix = "jwt:hlsToken:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @DubboReference
    private DubboDocEncKeyService dubboDocEncKeyService;

    @ApiOperation(value = "获取视频文件加密的 key")
    @GetMapping("/getkey")
    public void getVideoEncKey(@RequestParam("gridFsId") String gridFsId, @RequestParam("token") String token, HttpServletRequest request, HttpExchange httpExchange) throws IOException {

        // 判断 redis 是否含有该 token
        if (Boolean.FALSE.equals(redisTemplate.boundSetOps(keyPrefix + gridFsId).isMember(token))) {
            return;
        } else {
            // 移除 token
            redisTemplate.boundSetOps(keyPrefix + gridFsId).remove(token);
        }

        // 验证 token
        String userIdFromToken = JwtTokenUtil.getUserIdFromToken(token);
        if (StrUtil.isEmpty(userIdFromToken) || !userIdFromToken.equals(request.getHeader("userId"))) return;

        if (JwtTokenUtil.isTokenExpired(token)) {
            return;
        }

        // 查询文件 key，并返回
        String docEncKey = dubboDocEncKeyService.getDocEncKey(gridFsId);
        if (StrUtil.isEmpty(docEncKey)) return;

        // ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(docEncKey.getBytes());
        // try {
        //     IoUtil.copy(byteArrayInputStream, response.getOutputStream());
        // } catch (Exception e) {
        //     log.error(e.toString());
        // }
        byte[] keyBytes = docEncKey.getBytes();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, keyBytes.length);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(keyBytes);
        responseBody.close();
    }
}
