package com.travis.filesbottle.document.enums;

import lombok.Data;
import lombok.Getter;

/**
 * @ClassName FileTypeEnum
 * @Description 文档类型枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/10
 */
@Getter
public enum FileTypeEnum {

    /**
     * 未知类型，该类型不支持预览
     */
    UNKNOW((byte) 0, "unknow"),
    /**
     * PDF document
     */
    PDF((byte) 1, "pdf"),

    /**
     * xls
     */
    XLS((byte) 2, "xls"),

    /**
     * xlsx
     */
    XLSX((byte) 3, "xlsx"),

    /**
     * doc
     */
    DOC((byte) 4, "doc"),

    /**
     * docx
     */
    DOCX((byte) 5, "docx"),

    /**
     * ppt
     */
    PPT((byte) 6, "ppt"),

    /**
     * pptx
     */
    PPTX((byte) 7, "pptx"),

    /**
     * markdown
     */
    MD((byte) 8, "markdown"),

    /**
     * png
     */
    PNG((byte) 9, "png"),

    /**
     * jpeg
     */
    JPEG((byte) 10, "jpeg"),

    /**
     * jpg
     */
    JPG((byte) 11, "jpg"),

    /**
     * txt
     */
    TEXT((byte) 12,"txt");


    /**
     * 文档类型码
     */
    private final Byte code;
    /**
     * 文档类型
     */
    private final String fileType;

    FileTypeEnum(Byte code, String fileType) {
        this.code = code;
        this.fileType = fileType;
    }

    /**
     * @MethodName getCodeByFileType
     * @Description 根据文档类型获取文档枚举类型码
     * @Author travis-wei
     * @Data 2023/4/11
     * @param fileType
     * @Return java.lang.Integer  0:该文档类型未知
     **/
    public static Byte getCodeByFileType(String fileType) {
        for (FileTypeEnum value : FileTypeEnum.values()) {
            if (value.getFileType().equals(fileType)) return value.getCode();
        }
        return 0;
    }

    /**
     * @MethodName getFileTypeByCode
     * @Description 根据文档枚举类型码获取文档类型
     * @Author travis-wei
     * @Data 2023/4/11
     * @param code
     * @Return java.lang.String null-该类型码不存在
     **/
    public static String getFileTypeByCode(Integer code) {
        for (FileTypeEnum value : FileTypeEnum.values()) {
            if (value.getCode().equals(code)) return value.getFileType();
        }
        return null;
    }


    /**
     * @MethodName judgeSupportType
     * @Description 判断文件的类型是否是支持预览的类型
     * @Author travis-wei
     * @Data 2023/4/11
     * @param suffix
     * @Return boolean
     **/
    public static boolean judgeSupportType(String suffix) {
        for (FileTypeEnum value : FileTypeEnum.values()) {
            if (suffix.equals(value.getFileType())) return true;
        }
        return false;
    }

}
