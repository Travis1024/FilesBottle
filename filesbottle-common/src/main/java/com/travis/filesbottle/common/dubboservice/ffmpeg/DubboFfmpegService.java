package com.travis.filesbottle.common.dubboservice.ffmpeg;

import com.travis.filesbottle.common.utils.R;

/**
 * @ClassName DubboFfmpegService
 * @Description Dubbo ffmpeg模块服务接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/24
 */
public interface DubboFfmpegService {

    /**
     * 根据 sourceId 获取视频预览路径
     * @param sourceId
     * @return
     */
    String getVideoUrl(String sourceId, String userId);

    /**
     * 根据 sourceId 删除视频切片文件
     * @param sourceId
     * @return
     */
    boolean deleteVideo(String sourceId);

    /**
     * 获取上传视频文件的 URL 地址
     * @return
     */
    R<?> getHandleUrl();

}
