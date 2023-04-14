package com.travis.filesbottle.document.service.impl;

import cn.hutool.core.util.IdUtil;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.enums.FileTypeEnum;
import com.travis.filesbottle.document.service.TaskExecuteService;
import com.travis.filesbottle.document.thread.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName TaskExecuteServiceImpl
 * @Description 异步任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
@Service
public class TaskExecuteServiceImpl implements TaskExecuteService {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * @MethodName generatePreviewFile
     * @Description 异步处理文件预览、更新mysql数据、更新elasticSearch
     * @Author travis-wei
     * @Data 2023/4/12
     * @param fileDocument
     * @Return void
     **/
    @Override
    public void generatePreviewFile(FileDocument fileDocument, InputStream inputStream) {
        TaskFileConvertPDF taskFileConvertPDF;
        Byte type = fileDocument.getDocContentType();
        if (type.equals(FileTypeEnum.DOC.getCode()) || type.equals(FileTypeEnum.DOCX.getCode())) {
            taskFileConvertPDF = new TaskWordConvertPDF(fileDocument, inputStream);
        } else if (type.equals(FileTypeEnum.PPT.getCode()) || type.equals(FileTypeEnum.PPTX.getCode())) {
            taskFileConvertPDF = new TaskPptConvertPDF(fileDocument, inputStream);
        } else if (type.equals(FileTypeEnum.XLS.getCode()) || type.equals(FileTypeEnum.XLSX.getCode())) {
            taskFileConvertPDF = new TaskXlsConvertPDF(fileDocument, inputStream);
        } else {
            taskFileConvertPDF = new TaskNoNeedConvertPDF(fileDocument, inputStream);
        }

        threadPoolExecutor.execute(taskFileConvertPDF);
    }
}
