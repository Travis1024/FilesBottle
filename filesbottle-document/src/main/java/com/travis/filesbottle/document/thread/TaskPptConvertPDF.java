package com.travis.filesbottle.document.thread;

import com.travis.filesbottle.document.entity.FileDocument;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName TaskPptConvertPDF
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
public class TaskPptConvertPDF implements Runnable, TaskFileConvertPDF{

    private FileDocument fileDocument;

    public TaskPptConvertPDF(FileDocument fileDocument) {
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
