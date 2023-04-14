package com.travis.filesbottle.document.thread;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.EsDocument;
import com.travis.filesbottle.document.enums.FileTypeEnum;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName TaskPptConvertPDF
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
@Component
public class TaskPptConvertPDF implements Runnable, TaskFileConvertPDF{

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    private FileDocument fileDocument;
    private InputStream fileInputStream;

    public TaskPptConvertPDF() {
    }

    public TaskPptConvertPDF(FileDocument fileDocument, InputStream fileInputStream) {
        this.fileDocument = fileDocument;
        this.fileInputStream = fileInputStream;
    }

    @Override
    public InputStream convertFile() {
        InputStream inputStream = null;
        if (fileDocument.getDocContentType().equals(FileTypeEnum.PPT.getCode())) {
            inputStream = pptToPdf(fileInputStream);
        } else if (fileDocument.getDocContentType().equals(FileTypeEnum.PPTX.getCode())) {
            inputStream = pptxToPdf(fileInputStream);
        }
        return inputStream;
    }

    /**
     * @MethodName pptToPdf
     * @Description 将ppt文件转为pdf文件
     * @Author travis-wei
     * @Data 2023/4/13
     * @param inputStream
     * @Return java.io.InputStream
     **/
    private InputStream pptToPdf(InputStream inputStream) {
        Document document = null;
        PdfWriter pdfWriter = null;

        try {
            HSLFSlideShow hslfSlideShow = new HSLFSlideShow(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // 获取ppt页面
            Dimension dimension = hslfSlideShow.getPageSize();
            document = new Document();

            // pdfWrite实例
            pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();

            PdfPTable pdfPTable = new PdfPTable(1);
            List<HSLFSlide> hslfSlideList = hslfSlideShow.getSlides();

            for (HSLFSlide hslfSlide : hslfSlideList) {
                // 设置字体、解决中文乱码问题
                for (HSLFShape hslfShape : hslfSlide) {
                    HSLFTextShape textShape = (HSLFTextShape) hslfShape;
                    for (HSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
                        for (HSLFTextRun textRun : textParagraph.getTextRuns()) {
                            textRun.setFontFamily("宋体");
                        }
                    }
                }
                BufferedImage bufferedImage = new BufferedImage(((int) dimension.getWidth()), ((int) dimension.getHeight()), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                graphics2D.setPaint(Color.white);
                graphics2D.setFont(new Font("宋体", Font.PLAIN, 12));

                hslfSlide.draw(graphics2D);
                graphics2D.dispose();

                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(bufferedImage, null);
                image.scalePercent(50f);

                // 写入单元格中
                pdfPTable.addCell(new PdfPCell(image, true));
                document.add(image);
            }

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        } finally {
            if (document != null) {
                document.close();
            }
            if (pdfWriter != null) {
                pdfWriter.close();
            }
        }
    }

    /**
     * @MethodName pptxToPdf
     * @Description 将pptx文件转为pdf文件
     * @Author travis-wei
     * @Data 2023/4/13
     * @param inputStream
     * @Return java.io.InputStream
     **/
    private InputStream pptxToPdf(InputStream inputStream) {
        Document document = null;
        PdfWriter pdfWriter = null;

        try {
            XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // 获取ppt页面
            Dimension dimension = xmlSlideShow.getPageSize();
            document = new Document();

            // pdfWrite实例
            pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();

            PdfPTable pdfPTable = new PdfPTable(1);
            List<XSLFSlide> xslfSlideList = xmlSlideShow.getSlides();

            for (XSLFSlide xslfSlide : xslfSlideList) {
                // 设置字体、解决中文乱码问题
                for (XSLFShape xslfShape : xslfSlide) {
                    XSLFTextShape textShape = (XSLFTextShape) xslfShape;
                    for (XSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
                        for (XSLFTextRun textRun : textParagraph.getTextRuns()) {
                            textRun.setFontFamily("宋体");
                        }
                    }
                }
                BufferedImage bufferedImage = new BufferedImage(((int) dimension.getWidth()), ((int) dimension.getHeight()), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                graphics2D.setPaint(Color.white);
                graphics2D.setFont(new Font("宋体", Font.PLAIN, 12));

                xslfSlide.draw(graphics2D);
                graphics2D.dispose();

                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(bufferedImage, null);
                image.scalePercent(50f);

                // 写入单元格中
                pdfPTable.addCell(new PdfPCell(image, true));
                document.add(image);
            }

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        } finally {
            if (document != null) {
                document.close();
            }
            if (pdfWriter != null) {
                pdfWriter.close();
            }
        }
    }

    /**
     * @MethodName updateMysqlData
     * @Description 更新mysql数据
     * @Author travis-wei
     * @Data 2023/4/13
     * @param previewId
     * @Return void
     **/
    @Override
    public void updateMysqlData(String previewId) {
        UpdateWrapper<FileDocument> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(FileDocument.DOC_GRIDFS_ID, fileDocument.getDocGridfsId()).set(FileDocument.DOC_PREVIEW_ID, previewId);
        documentMapper.update(null, updateWrapper);
    }

    @Override
    public String uploadFileToEs() throws IOException {
        // no action
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

        return null;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run() {
        try {
            // 文件转换，ppt/pptx --> pdf流
            InputStream previewInputStream = convertFile();
            // 上传pdf文件到mongodb，如果预览pdf文件上传文件失败，会将异常抛出，在这里一起捕获
            String previewId = uploadPreviewFileToGridFs(previewInputStream);
            // 将预览文件的previewId设置到fileDocument中
            fileDocument.setDocPreviewId(previewId);
            // 更新mysql数据，主要是更新previewId
            updateMysqlData(previewId);
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入elasticsearch中
            uploadFileToEs();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
