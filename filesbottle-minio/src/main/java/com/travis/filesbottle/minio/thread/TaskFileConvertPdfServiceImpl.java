package com.travis.filesbottle.minio.thread;

import com.travis.filesbottle.minio.entity.Document;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskFileConvertPdfServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/31
 */
public class TaskFileConvertPdfServiceImpl implements TaskConvertService {
    public TaskFileConvertPdfServiceImpl(Document document, InputStream inputStream) {

    }

    @Override
    public InputStream convertFile() throws Exception {
        return null;
    }

    @Override
    public void updateMysqlData(String previewId) {

    }

    @Override
    public void uploadFileToEs() throws IOException {

    }

    @Override
    public void run() {

    }
}
