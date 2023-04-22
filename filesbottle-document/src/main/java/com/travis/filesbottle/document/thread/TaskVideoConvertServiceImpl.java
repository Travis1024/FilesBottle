package com.travis.filesbottle.document.thread;

import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.utils.ApplicationContextUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskVideoConvertServiceImpl
 * @Description 处理视频文件（使用ffmpeg进行视频文件切片）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/21
 */
public class TaskVideoConvertServiceImpl implements TaskConvertService{

    private String ffmpegFilePath;
    private FileDocument fileDocument;
    private MultipartFile multipartFile;
    private RestHighLevelClient restHighLevelClient;


    public TaskVideoConvertServiceImpl(FileDocument fileDocument, MultipartFile multipartFile, String ffmpegFilePath) {
        this.ffmpegFilePath = ffmpegFilePath;
        this.fileDocument = fileDocument;
        this.multipartFile = multipartFile;

        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
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
    public String uploadPreviewFileToGridFs(InputStream inputStream) {
        return null;
    }

    @Override
    public void run() {

    }
}
