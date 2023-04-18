package com.travis.filesbottle.document.thread;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.EsDocument;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.utils.ApplicationContextUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.*;

/**
 * @ClassName TaskFileConvertPdfServiceImpl
 * @Description 异步执行将文件转换成pdf预览文件的实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/17
 */
@Slf4j
public class TaskFileConvertPdfServiceImpl implements TaskConvertService {

    private FileDocument fileDocument;
    private InputStream fileInputStream;
    private RestHighLevelClient restHighLevelClient;
    private DocumentMapper documentMapper;
    private GridFsTemplate gridFsTemplate;
    private DocumentConverter converter;

    public TaskFileConvertPdfServiceImpl(FileDocument fileDocument, InputStream fileInputStream) {
        this.fileDocument = fileDocument;
        this.fileInputStream = fileInputStream;

        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
        this.documentMapper = ApplicationContextUtil.getBean(DocumentMapper.class);
        this.gridFsTemplate = ApplicationContextUtil.getBean(GridFsTemplate.class);
        this.converter = ApplicationContextUtil.getBean(DocumentConverter.class);
    }

    @Override
    public InputStream convertFile() throws OfficeException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 使用流的方式转换为PDF
        converter.convert(fileInputStream)
                .to(byteArrayOutputStream)
                .as(DefaultDocumentFormatRegistry.PDF)
                .execute();

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    @Override
    public void updateMysqlData(String previewId) {
        UpdateWrapper<FileDocument> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(FileDocument.DOC_GRIDFS_ID, fileDocument.getDocGridfsId()).set(FileDocument.DOC_PREVIEW_ID, previewId);
        documentMapper.update(null, updateWrapper);
    }

    @Override
    public void uploadFileToEs() throws IOException {
        EsDocument esDocument = new EsDocument();
        esDocument.setGridFsId(fileDocument.getDocGridfsId());
        esDocument.setPreviewId(fileDocument.getDocPreviewId());
        esDocument.setFileName(fileDocument.getDocName());
        esDocument.setFileDescription(fileDocument.getDocDescription());
        // TODO 内容的elasticSearch最后做
        // esDocument.setFileText();

        IndexRequest indexRequest = new IndexRequest(DocumentConstants.ES_DOCUMENT_NAME);
        indexRequest.id(esDocument.getGridFsId());

        String jsonStr = JSONUtil.toJsonStr(esDocument);
        indexRequest.source(jsonStr, XContentType.JSON);

        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info(indexResponse.toString());
    }

    @Override
    public String uploadPreviewFileToGridFs(InputStream inputStream) {
        try {
            // 随机生成previewId
            String previewId = IdUtil.simpleUUID();
            // 向mongo中上传文件
            gridFsTemplate.store(inputStream, previewId, DocumentConstants.PDF_CONTENT_TYPE);
            return previewId;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @GlobalTransactional
    @Override
    public void run() {
        try {
            // 文件转换，ppt/pptx --> pdf流
            InputStream previewInputStream = convertFile();
            // 上传pdf文件到mongodb，如果预览pdf文件上传失败，会将异常抛出，在这里一起捕获
            String previewId = uploadPreviewFileToGridFs(previewInputStream);
            // 将预览文件的previewId设置到fileDocument中
            fileDocument.setDocPreviewId(previewId);
            // 更新mysql数据，主要是更新previewId
            updateMysqlData(previewId);
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到elasticsearch中
            uploadFileToEs();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
