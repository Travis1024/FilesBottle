package com.travis.filesbottle.document.thread;

import com.travis.filesbottle.document.entity.FileDocument;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @ClassName TaskNoNeedConvertPDF
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
public class TaskNoNeedConvertPDF implements Runnable, TaskFileConvertPDF{

    private FileDocument fileDocument;
    private InputStream fileInputStream;

    public TaskNoNeedConvertPDF(FileDocument fileDocument, InputStream fileInputStream) {
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
    public String uploadFileToEs() {
        return null;
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
