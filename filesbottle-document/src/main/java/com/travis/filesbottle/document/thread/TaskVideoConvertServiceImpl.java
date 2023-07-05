package com.travis.filesbottle.document.thread;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.common.dubboservice.ffmpeg.DubboFfmpegService;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.EsDocument;
import com.travis.filesbottle.document.service.DocumentService;
import com.travis.filesbottle.document.utils.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskVideoConvertServiceImpl
 * @Description 处理视频文件（使用ffmpeg进行视频文件切片）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/21
 */
@Slf4j
public class TaskVideoConvertServiceImpl implements TaskConvertService{

    private String ffmpegFilePath;
    private FileDocument fileDocument;
    private MultipartFile multipartFile;
    private RestHighLevelClient restHighLevelClient;
    private RestTemplate restTemplate;

    private DubboFfmpegService dubboFfmpegService;



    public TaskVideoConvertServiceImpl(FileDocument fileDocument, MultipartFile multipartFile, String ffmpegFilePath) {
        this.ffmpegFilePath = ffmpegFilePath;
        this.fileDocument = fileDocument;
        this.multipartFile = multipartFile;

        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
        this.restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
        this.dubboFfmpegService = ApplicationContextUtil.getBean(DubboFfmpegService.class);
    }

    @Override
    public InputStream convertFile() throws Exception {
        // 获取源文件文件名称
        String originalFilename = multipartFile.getOriginalFilename();
        // 创建临时文件
        File tempFile = new File(originalFilename);
        // 复制 MultipartFile 文件到临时文件 File 中
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), tempFile);
        // 修改文件名称
        tempFile = FileUtil.rename(tempFile, fileDocument.getDocGridfsId(), true, true);
        InputStream inputStream = new FileInputStream(tempFile);
        // 将 File 文件转为 MultipartFile
        multipartFile = new MockMultipartFile(tempFile.getName(), tempFile.getName(), null, inputStream);
        // 删除临时 File 文件
        tempFile.delete();


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
        if (!StrUtil.isEmpty(result) && !fileDocument.getDocGridfsId().equals(result)) {
            throw new Exception("FFMPEG 视频上传，切片失败！");
        }
        return null;
    }

    @Override
    public void updateMysqlData(String previewId) {
        // no action
        // 此处不向mysql文件数据中插入视频在线预览的url信息，因为提供ffmpeg的服务器前缀可能会发生变化
    }

    @Override
    public void uploadFileToEs() throws IOException {
        EsDocument esDocument = new EsDocument();
        esDocument.setGridFsId(fileDocument.getDocGridfsId());
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
        // no action
        return null;
    }

    @Override
    public void run() {
        try {
            // 向 ffmpeg 服务器上传视频文件
            convertFile();
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到 elasticsearch 中
            uploadFileToEs();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
