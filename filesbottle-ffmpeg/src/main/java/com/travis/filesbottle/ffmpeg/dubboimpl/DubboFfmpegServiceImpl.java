package com.travis.filesbottle.ffmpeg.dubboimpl;

import com.travis.filesbottle.common.dubboservice.ffmpeg.DubboFfmpegService;
import com.travis.filesbottle.ffmpeg.controller.VideoController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Objects;

/**
 * @ClassName DubboFfmpegServiceImpl
 * @Description Dubbo ffmpeg服务实现
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/24
 */
@Slf4j
@DubboService
public class DubboFfmpegServiceImpl implements DubboFfmpegService {

    @Value("${ffmpeg.filepath}")
    private String videoFilePath;
    @Value("${custom.ip}")
    private String customIp;
    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public String getVideoUrl(String sourceId) {
        return "http://" + customIp + ":" + port + contextPath + "/hlsvideo/video?sourceId=" + sourceId;
    }

    @Override
    public boolean deleteVideo(String sourceId) {
        if (VideoController.deleteDirectory(videoFilePath + sourceId)) {
            log.info("文件删除成功：{}!", videoFilePath + sourceId);
            return true;
        }
        return false;
    }

    @Override
    public String getHandleUrl() {
        return "http://" + customIp + ":" + port + contextPath + "/hlsvideo/handle";
    }
}
