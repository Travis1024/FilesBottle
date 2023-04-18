package com.travis.filesbottle.document.thread;

import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.utils.ApplicationContextUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.jodconverter.core.office.OfficeException;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskNoNeedConvertServiceImpl
 * @Description 异步任务：对不支持在线预览，或者源文件即可在线预览的文件进行处理
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/17
 */
public class TaskNoNeedConvertServiceImpl implements TaskConvertService{

    private FileDocument fileDocument;
    private InputStream fileInputStream;
    private RestHighLevelClient restHighLevelClient;
    private DocumentMapper documentMapper;
    private GridFsTemplate gridFsTemplate;

    public TaskNoNeedConvertServiceImpl(FileDocument fileDocument, InputStream fileInputStream) {
        this.fileDocument = fileDocument;
        this.fileInputStream = fileInputStream;

        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
        this.documentMapper = ApplicationContextUtil.getBean(DocumentMapper.class);
        this.gridFsTemplate = ApplicationContextUtil.getBean(GridFsTemplate.class);
    }

    @Override
    public InputStream convertFile() throws OfficeException {
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
