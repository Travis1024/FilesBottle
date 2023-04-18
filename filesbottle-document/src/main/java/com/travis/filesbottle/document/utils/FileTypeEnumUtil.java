package com.travis.filesbottle.document.utils;

import com.travis.filesbottle.document.enums.FileTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FileTypeEnumUtil
 * @Description FileTypeEnum工具类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/17
 */
public class FileTypeEnumUtil {
    private static Map<String, FileTypeEnum> typeEnumMap = new HashMap<>();

    static {
        for (FileTypeEnum value : FileTypeEnum.values()) {
            typeEnumMap.put(value.getSuffix(), value);
        }
    }

    /**
     * @MethodName getFileTypeBySuffix
     * @Description 根据后缀返回类型
     * @Author travis-wei
     * @Data 2023/4/17
     * @param suffix
     * @Return com.travis.filesbottle.document.enums.FileTypeEnum   如果没有找到对应的后缀则返回FileTypeEnum.ERROR;
     **/
    public static FileTypeEnum getFileTypeBySuffix(String suffix) {
        FileTypeEnum typeEnum = typeEnumMap.get(suffix);
        if (typeEnum == null) return FileTypeEnum.ERROR;
        return typeEnum;
    }

    /**
     * @MethodName getCodeBySuffix
     * @Description 根据后缀返回类型码
     * @Author travis-wei
     * @Data 2023/4/17
     * @param suffix
     * @Return short 如果没有找到对应的后缀则返回FileTypeEnum.ERROR.getCode() --> -1;
     **/
    public static short getCodeBySuffix(String suffix) {
        FileTypeEnum typeEnum = getFileTypeBySuffix(suffix);
        if (typeEnum == null) return FileTypeEnum.ERROR.getCode();
        return typeEnum.getCode();
    }

    /**
     * @MethodName judgeSupportType
     * @Description 判断传入的后缀是否存在于FileTypeEnum中
     * @Author travis-wei
     * @Data 2023/4/17
     * @param suffix
     * @Return boolean
     **/
    public static boolean judgeSupportType(String suffix) {
        FileTypeEnum typeEnum = getFileTypeBySuffix(suffix);
        return typeEnum != FileTypeEnum.ERROR;
    }

    /**
     * @MethodName judgeNeedAsyncTask
     * @Description 判断该后缀的文件是否需要执行异步任务
     * @Author travis-wei
     * @Data 2023/4/17
     * @param suffix
     * @Return boolean
     **/
    public static boolean judgeNeedAsyncTask(String suffix) {
        short codeBySuffix = getCodeBySuffix(suffix);
        if (codeBySuffix == -1 || codeBySuffix == 0 || (codeBySuffix >= 201 && codeBySuffix <= 400) || (codeBySuffix >= 601 && codeBySuffix <= 1000)) {
            return false;
        }
        return true;
    }
}
