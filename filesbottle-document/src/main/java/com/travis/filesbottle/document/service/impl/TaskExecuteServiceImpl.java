package com.travis.filesbottle.document.service.impl;

import com.travis.filesbottle.common.dubboservice.ffmpeg.DubboFfmpegService;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.service.TaskExecuteService;
import com.travis.filesbottle.document.thread.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jodconverter.core.DocumentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName TaskExecuteServiceImpl
 * @Description 异步任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
@Service
public class TaskExecuteServiceImpl implements TaskExecuteService {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    public DocumentConverter documentConverter;

    @DubboReference
    public DubboFfmpegService dubboFfmpegService;

    /**
     * 从配置文件中获取文件前缀信息
     */
    @Value("${kkfileview.project.urlprefix}")
    private String kkProjectUrlPrefix;
    @Value("${ffmpeg.filepath}")
    private String ffmpegFilePath;


    /**
     * @param fileDocument
     * @param file
     * @MethodName generatePreviewFile
     * @Description 异步处理文件预览、更新mysql数据、更新elasticSearch
     * @Author travis-wei
     * @Data 2023/4/12
     * @Return void
     **/
    @Override
    public void generatePreviewFile(FileDocument fileDocument, InputStream inputStream, MultipartFile file) {

        TaskConvertService taskConvertService;

        // 获取文件类型码
        Short type = fileDocument.getDocFileTypeCode();
        // 根据文件类型码判断需要执行的异步任务
        if (type >= 1 && type <= 200) {
            taskConvertService = new TaskFileConvertPdfServiceImpl(fileDocument, inputStream);
        } else if (type >= 401 && type <= 600){
            taskConvertService = new TaskKKFileViewConvertServiceImpl(fileDocument, file, kkProjectUrlPrefix);
        } else if (type >= 351 && type <= 400) {
            // 处理视频文件
            taskConvertService = new TaskVideoConvertServiceImpl(fileDocument, file, ffmpegFilePath);
        } else {
            taskConvertService = new TaskNoNeedConvertServiceImpl(fileDocument, inputStream);
        }

        threadPoolExecutor.execute(taskConvertService);
    }
}
