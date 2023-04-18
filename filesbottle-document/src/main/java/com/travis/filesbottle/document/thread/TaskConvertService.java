package com.travis.filesbottle.document.thread;

import org.jodconverter.core.office.OfficeException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskFileConvertPDF
 * @Description 异步执行文件转换预览文件的接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface TaskConvertService extends Runnable {

    public InputStream convertFile() throws OfficeException;

    public void updateMysqlData(String previewId);

    public void uploadFileToEs() throws IOException;

    public String uploadPreviewFileToGridFs(InputStream inputStream);
}
