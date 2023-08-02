package com.travis.filesbottle.minio.thread;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.common.dubboservice.ffmpeg.DubboFfmpegService;
import com.travis.filesbottle.common.utils.R;
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
 * @ClassName TaskVideoConvertServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/2
 */
@Slf4j
public class TaskVideoConvertServiceImpl implements TaskConvertService {

    private Document document;
    private InputStream inputStream;
    private DubboFfmpegService dubboFfmpegService;
    private RestTemplate restTemplate;
    private RestHighLevelClient restHighLevelClient;

    public TaskVideoConvertServiceImpl(Document document, InputStream inputStream) {
        this.document = document;
        this.inputStream = inputStream;
        this.dubboFfmpegService = ApplicationContextUtil.getBean(DubboFfmpegService.class);
        this.restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
    }

    @Override
    public InputStream convertFile() throws Exception {
        // 将文件流转为 MultipartFile
        MultipartFile multipartFile = new MockMultipartFile(document.getDocMinioId() + "." + document.getDocSuffix(), document.getDocMinioId() + "." + document.getDocSuffix(), null, inputStream);
        // 向 ffmpeg 服务器发送请求信息，上传视频文件
        LinkedMultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("file", multipartFile.getResource());

        // 通过 dubbo 远程获取 ffmpeg 服务器上传视频文件的 URL 地址
        R<?> handleUrlResult = dubboFfmpegService.getHandleUrl();
        if (!R.checkSuccess(handleUrlResult)) {
            throw new RuntimeException("获取 ffmpeg 上传视频文件 URL 地址失败！");
        }
        String handleUrl = (String) handleUrlResult.getData();
        log.info(handleUrl);

        String result = restTemplate.postForObject(handleUrl, multiValueMap, String.class);
        if (!StrUtil.isEmpty(result) && !document.getDocMinioId().equals(result)) {
            throw new RuntimeException("FFMPEG 视频上传，切片失败！");
        }
        return null;
    }

    @Override
    public void updateMysqlData() {
        // no action
        // 无需更新预览 url，视频预览 url 需要携带一次性 token，需要在请求视频预览时，同时获取一次性 token
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
            // 向 ffmpeg 服务器上传视频文件
            convertFile();
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到 ElasticSearch 中
            uploadFileToEs();
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }
    }
}
