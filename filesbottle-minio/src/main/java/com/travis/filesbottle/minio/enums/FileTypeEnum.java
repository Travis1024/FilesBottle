package com.travis.filesbottle.minio.enums;

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
     * 201  —  350   -->  支持返回源文件流，进行在线预览
     * 351  —  400   -->  视频文件：支持使用ffmpeg进行切片（m3u8+ts）,进行在线播放，前端可以考虑使用video.js
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

    /*---------------- [201 - 350]  -->  支持返回源文件流，进行在线预览 --------------------- */
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


    /*---------------- [351 - 400]  -->  视频文件：支持ffmpeg进行视频切片，进行在线播放 --------------------- */
    // 视频文件
    MP4((short) 351, "mp4"), // ffmpeg可以直接进行切片
    AVI((short) 352, "avi"), // ffmpeg可以直接进行切片
    MOV((short) 353, "mov"), // 以下视频格式：ffmpeg将格式转为mp4，然后进行切片
    FLV((short) 354, "flv"),
    MKV((short) 355, "mkv"),
    WMV((short) 356, "wmv"),
    ASF((short) 357, "asf"),
    RMVB((short) 358, "rmvb"),


    /*---------------- [401 - 600]  -->  使用kkfileview提供在线预览----------------------- */
    XMIND((short) 401, "xmind"),    // 软件模型文件        // y
    BPMN((short) 402, "bpmn"),      // 工作流文件         // w
    EML((short) 403, "eml"),        // 邮件文件          // w
    EPUB((short) 404, "epub"),      // 图书文档          // w
    OBJ((short) 405, "obj"),        // 3D模型文件        // w
    SSS_3DS((short) 406, "3ds"),                        // w
    STL((short) 407, "stl"),                            // w
    PLY((short) 408, "ply"),                            // w
    GLTF((short) 409, "gltf"),                          // w
    GLB((short) 410, "glb"),                            // w
    OFF((short) 411, "off"),                            // w
    SSS_3DM((short) 412, "3dm"),                        // w
    FBX((short) 413, "fbx"),                            // w
    DAE((short) 414, "dae"),                            // w
    WRL((short) 415, "wrl"),                            // w
    SSS_3MF((short) 416, "3mf"),                        // w
    IFC((short) 417, "ifc"),                            // w
    BREP((short) 418, "brep"),                          // w
    STEP((short) 419, "step"),                          // w
    IGES((short) 420, "iges"),                          // w
    FCSTD((short) 421, "fcstd"),                        // w
    BIM((short) 422, "bim"),                            // w
    DWG((short) 423, "dwg"),        // CAD模型文件        // w
    DXF((short) 424, "dxf"),        // CAD模型文件        // w
    MD((short) 425, "md"),          // markdown文本      // y
    TIF((short) 426, "tif"),        // 图信息模型文件      // w
    TIFF((short) 427, "tiff"),      // 图信息模型文件      // w
    TGA((short) 428, "tga"),        // 图像格式文件       // w
    SVG((short) 429, "svg"),        // 矢量图像格式文件    // w
    ZIP((short) 430, "zip"),        // 压缩包文件         // w
    RAR((short) 431, "rar"),                            // w
    JAR((short) 432, "jar"),                            // w
    TAR((short) 433, "tar"),                            // w
    GZIP((short) 434, "gzip"),                          // w
    SSS_7Z((short) 435, "7z"),                          // w
    // 音频文件、mp3、wav已经进行测试，可以满足基本需求
    MP3((short) 436, "mp3"),
    WAV((short) 437, "wav"),


    /*---------------- [601 - 1000]  -->  不支持在线预览--------------------------------- */
    DLL((short) 601, "dll"),
    DAT((short) 602, "dat"),
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
