package com.travis.filesbottle.document.service;

import com.travis.filesbottle.document.entity.FileDocument;

import java.io.InputStream;

/**
 * @ClassName TaskExecuteService
 * @Description 异步任务实现接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface TaskExecuteService {
    void generatePreviewFile(FileDocument fileDocument, InputStream inputStream);
}
