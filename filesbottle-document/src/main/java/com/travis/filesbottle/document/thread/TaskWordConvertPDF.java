package com.travis.filesbottle.document.thread;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontProvider;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.EsDocument;
import com.travis.filesbottle.document.enums.FileTypeEnum;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.utils.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName TaskWordConvertPDF
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
public class TaskWordConvertPDF implements Runnable, TaskFileConvertPDF{

    private FileDocument fileDocument;
    private InputStream fileInputStream;
    private RestHighLevelClient restHighLevelClient;
    private DocumentMapper documentMapper;
    private GridFsTemplate gridFsTemplate;

    public TaskWordConvertPDF(FileDocument fileDocument, InputStream fileInputStream) {
        this.fileDocument = fileDocument;
        this.fileInputStream = fileInputStream;

        this.restHighLevelClient = ApplicationContextUtil.getBean(RestHighLevelClient.class);
        this.documentMapper = ApplicationContextUtil.getBean(DocumentMapper.class);
        this.gridFsTemplate = ApplicationContextUtil.getBean(GridFsTemplate.class);
    }

    @Override
    public InputStream convertFile() {
        InputStream inputStream = null;
        if (fileDocument.getDocContentType().equals(FileTypeEnum.DOC.getCode())) {
            inputStream = docToPdf(fileInputStream);
        } else if (fileDocument.getDocContentType().equals(FileTypeEnum.DOCX.getCode())) {
            inputStream = docxToPdf(fileInputStream);
        }
        return inputStream;
    }

    /**
     * @MethodName docToPdf
     * @Description 将doc文件转换成pdf文件
     * @Author travis-wei
     * @Data 2023/4/16
     * @param inputStream
     * @Return java.io.InputStream
     **/
    private InputStream docToPdf(InputStream inputStream) {
        byte[] resultBytes;

        try {
            // 步骤一：先将doc转换成html字符串
            String html = docToHtml(inputStream);
            // 步骤二：再将html字符串规范化
            html = formatHtml(html);
            // 步骤三：最后将html转为pdf
            resultBytes = htmlToPdf(html);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(resultBytes);
    }

    /**
     * @MethodName docToHtml
     * @Description doc文件转换为html文件
     * @Author travis-wei
     * @Data 2023/4/16
     * @param inputStream
     * @Return java.lang.String
     **/
    private String docToHtml(InputStream inputStream) throws Exception {
        String content = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            HWPFDocument hwpfDocument = new HWPFDocument(inputStream);

            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            wordToHtmlConverter.setPicturesManager((bytes, pictureType, s, v, v1) -> null);
            wordToHtmlConverter.processDocument(hwpfDocument);
            Document htmlConverterDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlConverterDocument);

            byteArrayOutputStream = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult(byteArrayOutputStream);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.transform(domSource, streamResult);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (byteArrayOutputStream != null) {
                content = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
                byteArrayOutputStream.close();
            }
        }
        return content;
    }

    /**
     * @MethodName formatHtml
     * @Description 使用jsoup规范化html
     * @Author travis-wei
     * @Data 2023/4/16
     * @param html
     * @Return java.lang.String
     **/
    private String formatHtml(String html) {
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        // 去除过大的宽度
        String style = document.attr("style");

        if (StrUtil.isEmpty(style) && style.contains("width")) {
            document.attr("style", "");
        }
        Elements divs = document.select("div");
        for (Element div : divs) {
            String divStyle = div.attr("style");
            if (StrUtil.isEmpty(divStyle) && divStyle.contains("width")) {
                div.attr("style", "");
            }
        }
        // jsoup生成闭合标签
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return document.html();
    }

    private byte[] htmlToPdf(String html) throws IOException {
        com.itextpdf.text.Document document = null;
        PdfWriter pdfWriter = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] resultBytes = null;

        try {
            // 创建html输入字节流
            byteArrayInputStream = new ByteArrayInputStream(html.getBytes());

            document = new com.itextpdf.text.Document();
            byteArrayOutputStream = new ByteArrayOutputStream();

            // pdfWriter实例
            pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();
            pdfWriter.open();

            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, byteArrayInputStream, StandardCharsets.UTF_8, new FontProvider() {
                @Override
                public boolean isRegistered(String s) {
                    return false;
                }

                @Override
                public Font getFont(String s, String s1, boolean b, float size, int style, BaseColor baseColor) {
                    Font font = null;
                    try {
                        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
                        font = new Font(baseFont, size, style, baseColor);
                        font.setColor(baseColor);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return font;
                }
            });
            document.close();
            pdfWriter.close();
            resultBytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (pdfWriter != null) {
                pdfWriter.close();
            }
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }
        return resultBytes;
    }



    private InputStream docxToPdf(InputStream inputStream) {
        return null;
    }


    @Override
    public void updateMysqlData(String previewId) {
        UpdateWrapper<FileDocument> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(FileDocument.DOC_GRIDFS_ID, fileDocument.getDocGridfsId()).set(FileDocument.DOC_PREVIEW_ID, previewId);
        documentMapper.update(null, updateWrapper);
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
            // 上传pdf文件到mongodb，如果预览pdf文件上传失败，会将异常抛出，在这里一起捕获
            String previewId = uploadPreviewFileToGridFs(previewInputStream);
            // 将预览文件的previewId设置到fileDocument中
            fileDocument.setDocPreviewId(previewId);
            // 更新mysql数据，主要是更新previewId
            updateMysqlData(previewId);
            // 将可供检索的文件信息（文件名称、文件描述、文件内容待做）插入到elasticsearch中
            uploadFileToEs();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
