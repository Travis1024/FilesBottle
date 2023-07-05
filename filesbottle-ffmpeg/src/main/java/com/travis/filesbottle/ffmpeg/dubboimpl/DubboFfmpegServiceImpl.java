package com.travis.filesbottle.ffmpeg.dubboimpl;

import com.travis.filesbottle.common.dubboservice.auth.DubboCreateOnceTokenService;
import com.travis.filesbottle.common.dubboservice.ffmpeg.DubboFfmpegService;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.ffmpeg.controller.VideoController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

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

    @DubboReference
    private DubboCreateOnceTokenService dubboCreateOnceTokenService;

    @Override
    public String getVideoUrl(String sourceId, String userId) {
        // TODO dubbo远程调用auth模块生成 token， 需要请求生成一次性 token (存到 redis 中)
        R<?> onceTokenR = dubboCreateOnceTokenService.createOnceToken(sourceId, userId);
        if (!R.checkSuccess(onceTokenR)) return null;
        String token = (String) onceTokenR.getData();
        return "http://" + customIp + ":" + port + contextPath + "/hlsvideo/video?sourceId=" + sourceId + "&token=" + token;
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
    public R<?> getHandleUrl() {
        return R.success("http://" + customIp + ":" + port + contextPath + "/hlsvideo/handle");
    }
}
