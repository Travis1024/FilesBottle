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

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName TaskNoNeedConvertServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/2
 */
@Slf4j
public class TaskNoNeedConvertServiceImpl implements TaskConvertService {

    private Document document;
    private RestHighLevelClient restHighLevelClient;

    public TaskNoNeedConvertServiceImpl(Document document) {
        this.document = document;
        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
    }

    @Override
    public InputStream convertFile() throws Exception {
        // no action
        return null;
    }

    @Override
    public void updateMysqlData() {
        // no action
    }

    @Override
    public void uploadFileToEs() throws IOException {
        EsDocument esDocument = new EsDocument();
        esDocument.setMinioId(document.getDocMinioId());
        esDocument.setFileName(document.getDocName());
        esDocument.setFileDescription(document.getDocDescription());

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
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到 ElasticSearch 中
            uploadFileToEs();
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}
