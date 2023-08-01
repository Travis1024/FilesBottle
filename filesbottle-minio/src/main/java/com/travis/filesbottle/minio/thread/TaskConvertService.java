package com.travis.filesbottle.minio.thread;

import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName TaskFileConvertPDF
 * @Description 异步执行文件转换预览文件的接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface TaskConvertService extends Runnable {

    public InputStream convertFile() throws Exception;

    public void updateMysqlData();

    public void uploadFileToEs() throws IOException;

    public String uploadPreviewFileToMinio(InputStream previewInputStream) throws ExecutionException, InterruptedException, InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException;
}
