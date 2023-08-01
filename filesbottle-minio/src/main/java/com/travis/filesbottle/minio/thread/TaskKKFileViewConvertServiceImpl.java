package com.travis.filesbottle.minio.thread;

import cn.hutool.json.JSONUtil;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.entity.EsDocument;
import com.travis.filesbottle.minio.utils.ApplicationContextUtil;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName TaskKKFileViewConvertServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/1
 */
@Slf4j
public class TaskKKFileViewConvertServiceImpl implements TaskConvertService {

    private Long fileSize;
    private Document document;
    private InputStream inputStream;
    private String kkProjectUrlPrefix;

    private RestTemplate restTemplate;
    private RestHighLevelClient restHighLevelClient;

    public TaskKKFileViewConvertServiceImpl(Long fileSize, Document document, InputStream inputStream, String kkProjectUrlPrefix) {
        this.fileSize = fileSize;
        this.document = document;
        this.inputStream = inputStream;
        this.kkProjectUrlPrefix = kkProjectUrlPrefix;

        this.restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
    }

    /**
     * @MethodName convertFile
     * @Description kkFileView 文件上传任务
     * @Author travis-wei
     * @Data 2023/8/1
     * @param
     * @Return java.io.InputStream
     **/
    @Override
    public InputStream convertFile() throws Exception {
        // 将文件流转为 MultipartFile
        MultipartFile multipartFile = new MockMultipartFile(document.getDocMinioId(), document.getDocMinioId(), document.getDocContentTypeText(), inputStream);

        // 向 kkFileView 服务器发送数据
        LinkedMultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("file", multipartFile.getResource());
        String fileUploadUrl = kkProjectUrlPrefix + "fileUpload";
        String result = restTemplate.postForObject(fileUploadUrl, multiValueMap, String.class);
        log.info(result);
        return null;
    }

    @Override
    public void updateMysqlData() {
        // no action
        // 由 kkFileview 服务器修改数据库中的预览 url 信息
        // 此处不向mysql文件数据中插入预览的url信息，因为提供kkFileView的服务器前缀可能会发生变化
    }

    @Override
    public void uploadFileToEs() throws IOException {
        EsDocument esDocument = new EsDocument();
        esDocument.setMinioId(document.getDocMinioId());
        // esDocument.setPreviewId();
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
        // no action
        return null;
    }

    @Override
    public void run() {
        try {
            // 修改文件名称，并将文件上传到 kkFileview 中
            convertFile();
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到 ElasticSearch 中
            uploadFileToEs();

        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }
    }
}
