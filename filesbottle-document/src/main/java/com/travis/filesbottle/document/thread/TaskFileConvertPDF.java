package com.travis.filesbottle.document.thread;

/**
 * @ClassName TaskFileConvertPDF
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface TaskFileConvertPDF extends Runnable {

    public void convertFile();

    public void updateMysqlData();

    public void uploadFileToEs();
}
