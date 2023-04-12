package com.travis.filesbottle.document.service.impl;

import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.enums.FileTypeEnum;
import com.travis.filesbottle.document.service.TaskExecuteService;
import com.travis.filesbottle.document.thread.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName TaskExecuteServiceImpl
 * @Description 异步任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Service
public class TaskExecuteServiceImpl implements TaskExecuteService {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    private TaskFileConvertPDF taskFileConvertPDF;


    /**
     * @MethodName generatePreviewFile
     * @Description 异步处理文件预览、更新mysql数据、更新elasticSearch
     * @Author travis-wei
     * @Data 2023/4/12
     * @param fileDocument
     * @Return void
     **/
    @Override
    public void generatePreviewFile(FileDocument fileDocument) {
        Byte type = fileDocument.getDocContentType();
        if (type.equals(FileTypeEnum.DOC.getCode()) || type.equals(FileTypeEnum.DOCX.getCode())) {
            taskFileConvertPDF = new TaskWordConvertPDF(fileDocument);
        } else if (type.equals(FileTypeEnum.PPT.getCode()) || type.equals(FileTypeEnum.PPTX.getCode())) {
            taskFileConvertPDF = new TaskPptConvertPDF(fileDocument);
        } else if (type.equals(FileTypeEnum.XLS.getCode()) || type.equals(FileTypeEnum.XLSX.getCode())) {
            taskFileConvertPDF = new TaskXlsConvertPDF(fileDocument);
        } else {
            taskFileConvertPDF = new TaskNoNeedConvertPDF(fileDocument);
        }

        threadPoolExecutor.execute(taskFileConvertPDF);
    }
}
