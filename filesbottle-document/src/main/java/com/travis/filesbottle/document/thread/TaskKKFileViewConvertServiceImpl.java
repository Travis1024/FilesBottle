package com.travis.filesbottle.document.thread;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.EsDocument;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.utils.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.jodconverter.core.office.OfficeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TaskKKFileViewConvertServiceImpl
 * @Description 异步任务：kkfileview生成预览文件
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/17
 */
@Slf4j
public class TaskKKFileViewConvertServiceImpl implements TaskConvertService{

    /**
     * 从配置文件中获取文件前缀信息
     */
    private String kkProjectUrlPrefix;
    private FileDocument fileDocument;
    private MultipartFile multipartFile;

    private RestHighLevelClient restHighLevelClient;
    private RestTemplate restTemplate;

    public TaskKKFileViewConvertServiceImpl(FileDocument fileDocument, MultipartFile multipartFile, String kkProjectUrlPrefix) {
        this.kkProjectUrlPrefix = kkProjectUrlPrefix;
        this.fileDocument = fileDocument;
        this.multipartFile = multipartFile;

        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
        this.restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
    }

    /**
     * @MethodName convertFile
     * @Description kKFileView的文件上传任务
     * @Author travis-wei
     * @Data 2023/4/19
     * @param
     * @Return java.io.InputStream
     **/
    @Override
    public InputStream convertFile() throws Exception {
        // 获取源文件文件名称
        String originalFilename = multipartFile.getOriginalFilename();
        // 创建临时文件
        File tempFile = new File(originalFilename);
        // 复制MultipartFile文件到临时文件File中
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), tempFile);
        // 修改文件名称
        tempFile = FileUtil.rename(tempFile, fileDocument.getDocGridfsId(), true, true);
        InputStream inputStream = new FileInputStream(tempFile);
        // 将File文件转为MultipartFile
        multipartFile = new MockMultipartFile(tempFile.getName(), tempFile.getName(), null, inputStream);
        // 删除临时的File文件
        tempFile.delete();

        // 向kkFileView服务器发送数据
        LinkedMultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("file", multipartFile.getResource());
        String fileUploadUrl = kkProjectUrlPrefix + "fileUpload";
        String result = restTemplate.postForObject(fileUploadUrl, multiValueMap, String.class);
        log.info(result);
        return null;
    }

    @Override
    public void updateMysqlData(String previewId) {
        // no action
        // 此处不向mysql文件数据中插入预览的url信息，因为提供kkFileView的服务器前缀可能会发生变化
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
        // no action
        return null;
    }

    @Override
    public void run() {
        try {
            // 修改文件名称，并将文件上传到kKFileView中
            convertFile();
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到elasticsearch中
            uploadFileToEs();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
