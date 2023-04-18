package com.travis.filesbottle.document.thread;

import com.travis.filesbottle.document.entity.FileDocument;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @ClassName TaskXlsConvertPDF
 * @Description 异步执行将xls文件转换成pdf文件（已弃用）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Deprecated
@Slf4j
public class TaskXlsConvertPDF implements Runnable, TaskConvertService {

    private FileDocument fileDocument;
    private InputStream fileInputStream;

    public TaskXlsConvertPDF(FileDocument fileDocument, InputStream fileInputStream) {
        this.fileDocument = fileDocument;
        this.fileInputStream = fileInputStream;
    }

    @Override
    public InputStream convertFile() {
        return null;
    }

    @Override
    public void updateMysqlData(String previewId) {

    }

    @Override
    public void uploadFileToEs() {
    }

    @Override
    public String uploadPreviewFileToGridFs(InputStream inputStream) {
        return null;
    }

    @Override
    public void run() {
        try {

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
