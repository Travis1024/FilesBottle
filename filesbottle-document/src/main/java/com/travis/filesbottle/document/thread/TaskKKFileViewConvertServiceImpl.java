package com.travis.filesbottle.document.thread;

import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.utils.ApplicationContextUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskKKFileViewConvertServiceImpl
 * @Description 异步任务：kkfileview生成预览文件
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/17
 */
public class TaskKKFileViewConvertServiceImpl implements TaskConvertService{
    private FileDocument fileDocument;
    private InputStream fileInputStream;
    private RestHighLevelClient restHighLevelClient;
    private DocumentMapper documentMapper;
    private GridFsTemplate gridFsTemplate;

    public TaskKKFileViewConvertServiceImpl(FileDocument fileDocument, InputStream fileInputStream) {
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
