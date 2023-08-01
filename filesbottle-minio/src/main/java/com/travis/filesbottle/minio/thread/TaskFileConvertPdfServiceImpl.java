package com.travis.filesbottle.minio.thread;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.entity.EsDocument;
import com.travis.filesbottle.minio.mapper.DocumentMapper;
import com.travis.filesbottle.minio.utils.ApplicationContextUtil;
import com.travis.filesbottle.minio.utils.MinioProperties;
import io.minio.MinioAsyncClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.local.office.utils.Lo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName TaskFileConvertPdfServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/31
 */
@Slf4j
public class TaskFileConvertPdfServiceImpl implements TaskConvertService {
    private Long fileSize;
    private Document document;
    private InputStream inputStream;

    private DocumentConverter converter;
    private MinioAsyncClient minioAsyncClient;
    private MinioProperties minioProperties;
    private DocumentMapper documentMapper;
    private RestHighLevelClient restHighLevelClient;

    public TaskFileConvertPdfServiceImpl(Long fileSize, Document document, InputStream inputStream) {
        this.fileSize = fileSize;
        this.document = document;
        this.inputStream = inputStream;

        this.converter = ApplicationContextUtil.getBean(DocumentConverter.class);
        this.minioAsyncClient = ApplicationContextUtil.getBean(MinioAsyncClient.class);
        this.minioProperties = ApplicationContextUtil.getBean(MinioProperties.class);
        this.documentMapper = ApplicationContextUtil.getBean(DocumentMapper.class);
        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
    }

    @Override
    public InputStream convertFile() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 使用流的方式转换为 PDF
        converter.convert(inputStream)
                .to(byteArrayOutputStream)
                .as(DefaultDocumentFormatRegistry.PDF)
                .execute();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    @Override
    public void updateMysqlData() {
        UpdateWrapper<Document> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(Document.DOC_MINIO_ID, document.getDocMinioId()).set(Document.DOC_PREVIEW_ID, document.getDocPreviewId());
        documentMapper.update(null, updateWrapper);
    }

    @Override
    public void uploadFileToEs() throws IOException {
        EsDocument esDocument = new EsDocument();
        esDocument.setMinioId(document.getDocMinioId());
        esDocument.setPreviewId(document.getDocPreviewId());
        esDocument.setFileName(document.getDocName());
        esDocument.setFileDescription(document.getDocDescription());
        // esDocument.setFileText();

        IndexRequest indexRequest = new IndexRequest(DocumentConstants.ES_DOCUMENT_NAME);
        indexRequest.id(esDocument.getMinioId());

        String jsonStr = JSONUtil.toJsonStr(esDocument);
        indexRequest.source(jsonStr, XContentType.JSON);

        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info(indexResponse.toString());
    }

    @Override
    public String uploadPreviewFileToMinio(InputStream previewInputStream) throws ExecutionException, InterruptedException, InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException {
        // 随机生成预览文件ID
        String previewId = IdUtil.randomUUID();
        // 向 minio 文件系统中上传文件
        CompletableFuture<ObjectWriteResponse> completableFuture = minioAsyncClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(previewId)
                        .stream(previewInputStream, fileSize, -1)
                        .contentType(document.getDocContentTypeText())
                        .build()
        );
        completableFuture.get();
        return previewId;
    }

    @GlobalTransactional
    @Override
    public void run() {
        try {
            // 文件转化，ppt/pptx ---> pdf流
            InputStream previewInputStream = convertFile();
            // 上传预览文件到 minio 文件系统中
            String minioPreviewId = uploadPreviewFileToMinio(previewInputStream);
            // 将预览文件的 minioPreviewId 设置到 document 中
            document.setDocPreviewId(minioPreviewId);
            // 更新 mysql 数据, 主要是更新 previewId
            updateMysqlData();
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到 ElasticSearch 中
            uploadFileToEs();

        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }
    }
}
