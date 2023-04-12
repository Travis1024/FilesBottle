package com.travis.filesbottle.document.thread;

import com.travis.filesbottle.document.entity.FileDocument;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName TaskXlsConvertPDF
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
public class TaskXlsConvertPDF implements Runnable, TaskFileConvertPDF{

    private FileDocument fileDocument;

    public TaskXlsConvertPDF(FileDocument fileDocument) {
        this.fileDocument = fileDocument;
    }

    @Override
    public void convertFile() {

    }

    @Override
    public void updateMysqlData() {

    }

    @Override
    public void uploadFileToEs() {

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
