package com.travis.filesbottle.document.enums;

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
     * 定义类型码范围：
     * -1   -->   （返回结果时）处理此枚举类不存在的类型
     * 0    -->   （存入文件时）处理此枚举类不存在的类型
     * ------------------------------------------
     * 1   —  200    -->  支持转为pdf进行在线预览
     * 201  —  400   -->  支持返回源文件流，进行在线预览
     * 401  —  600   -->  使用kkfileview提供在线预览
     * 601 —  1000   -->  不支持在线预览
     */
    /*---------------------------------------------------------------------------- */
    ERROR((short) -1, "error"),
    UNKNOW((short) 0, "unknow"),

    /*--------------- [1 - 200]  -->  支持转为pdf进行在线预览 ------------------------ */
    XLS((short) 1, "xls"),
    XLSX((short) 2, "xlsx"),
    DOC((short) 3, "doc"),
    DOCX((short) 4, "docx"),
    PPT((short) 5, "ppt"),
    PPTX((short) 6, "pptx"),
    TEXT((short) 7,"txt"),
    ODT((short) 8, "odt"),
    OTT((short) 9, "ott"),
    SXW((short) 10, "sxw"),
    RTF((short) 11, "rtf"),
    WPD((short) 12, "wpd"),
    SXI((short) 13, "xsi"),
    ODS((short) 14, "ods"),
    OTS((short) 15, "ots"),
    SXC((short) 16, "sxc"),
    CSV((short) 17, "csv"),
    TSV((short) 18, "tsv"),
    ODP((short) 19, "odp"),
    OTP((short) 20, "otp"),

    /*---------------- [201 - 400]  -->  支持返回源文件流，进行在线预览 --------------------- */
    PDF((short) 201, "pdf"),
    HTML((short) 202, "html"),
    // 图像文件
    PNG((short) 203, "png"),
    JPEG((short) 204, "jpeg"),
    JPG((short) 205, "jpg"),
    // 程序文件
    PY((short) 211, "py"),
    JAVA((short) 212, "java"),
    CPP((short) 213, "cpp"),
    C((short) 214, "c"),
    XML((short) 215, "xml"),
    PHP((short) 216, "php"),
    JS((short) 217, "js"),
    JSON((short) 218, "json"),
    CSS((short) 219, "css"),
    // 视频文件
    MP4((short) 220, "mp4"),
    AVI((short) 221, "avi"),
    MOV((short) 222, "mov"),


    /*---------------- [401 - 600]  -->  使用kkfileview提供在线预览----------------------- */
    XMIND((short) 401, "xmind"),
    BPMN((short) 402, "bpmn"),
    EML((short) 403, "eml"),
    EPUB((short) 404, "epub"),
    OBJ((short) 405, "obj"),
    SSS_3DS((short) 406, "3ds"),
    STL((short) 407, "stl"),
    PLY((short) 408, "ply"),
    GLTF((short) 409, "gltf"),
    GLB((short) 410, "glb"),
    OFF((short) 411, "off"),
    SSS_3DM((short) 412, "3dm"),
    FBX((short) 413, "fbx"),
    DAE((short) 414, "dae"),
    WRL((short) 415, "wrl"),
    SSS_3MF((short) 416, "3mf"),
    IFC((short) 417, "ifc"),
    BREP((short) 418, "brep"),
    STEP((short) 419, "step"),
    IGES((short) 420, "iges"),
    FCSTD((short) 421, "fcstd"),
    BIM((short) 422, "bim"),
    DWG((short) 423, "dwg"),
    DXF((short) 424, "dxf"),
    MD((short) 425, "md"),
    TIF((short) 426, "tif"),
    TIFF((short) 427, "tiff"),
    TGA((short) 428, "tga"),
    SVG((short) 429, "svg"),
    ZIP((short) 430, "zip"),
    RAR((short) 431, "rar"),
    JAR((short) 432, "jar"),
    TAR((short) 433, "tar"),
    GZIP((short) 434, "gzip"),
    SSS_7Z((short) 435, "7z"),


    /*---------------- [601 - 1000]  -->  不支持在线预览--------------------------------- */
    DLL((short) 601, "dll"),
    EXE((short) 1000, "exe");


    /**
     * 文件类型码
     */
    private final short code;
    /**
     * 文件后缀
     */
    private final String suffix;


    FileTypeEnum(short code, String suffix) {
        this.code = code;
        this.suffix = suffix;
    }
}
