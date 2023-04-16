package com.travis.filesbottle.document.thread;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskFileConvertPDF
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface TaskFileConvertPDF extends Runnable {

    public InputStream convertFile();

    public void updateMysqlData(String previewId);

    public void uploadFileToEs() throws IOException;

    public String uploadPreviewFileToGridFs(InputStream inputStream);
}
