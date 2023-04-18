package com.travis.filesbottle.document.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.Serializable;

/**
 * @ClassName DownloadDocument
 * @Description 文档下载信息及预览流数据信息
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/14
 */
@Data
public class DownloadDocument implements Serializable {

    @ApiModelProperty("文档mongo管理的gridfs ID")
    private String docGridfsId;

    @ApiModelProperty("提供预览文档的ID")
    private String docPreviewId;

    @ApiModelProperty("文档名称")
    private String docName;

    @ApiModelProperty("文档大小")
    private Double docSize;

    @ApiModelProperty("文档类型")
    private String docContentTypeText;

    @ApiModelProperty("文档后缀")
    private String docSuffix;

    @ApiModelProperty("文件类型码")
    private Short docFileTypeCode;

    @ApiModelProperty("文档描述")
    private String docDescription;

    @ApiModelProperty("文件字节流数据")
    private byte[] bytes;

    @ApiModelProperty("kkFileView文件预览URL")
    private String previewUrl;

}
