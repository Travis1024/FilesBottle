package com.travis.filesbottle.common.utils;

import com.travis.filesbottle.common.enums.BizCodeEnum;

/**
 * @ClassName BizCodeUtil
 * @Description Biz状态码工具包
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/7
 */
public class BizCodeUtil {

    /**
     * @MethodName isCodeSuccess
     * @Description 判断状态码最后三位是不是200
     * @Author travis-wei
     * @Data 2023/4/7
     * @param code 五位状态码
     * @Return java.lang.Boolean
     **/
    public static Boolean isCodeSuccess(Integer code) {
        int temp = getThreeCode(code);
        return temp == BizCodeEnum.SUCCESS.getCode();
    }

    /**
     * @MethodName isCodeSuccess
     * @Description 判断状态码最后三位是不是200
     * @Author travis-wei
     * @Data 2023/4/7
     * @param bizCodeEnum
     * @Return java.lang.Boolean
     **/
    public static Boolean isCodeSuccess(BizCodeEnum bizCodeEnum) {
        return isCodeSuccess(bizCodeEnum.getCode());
    }

    /**
     * @MethodName getThreeCode
     * @Description 获取状态码的最后三位
     * @Author travis-wei
     * @Data 2023/4/7
     * @param code
     * @Return java.lang.Integer
     **/
    public static Integer getThreeCode(Integer code) {
        return code % 1000;
    }

    /**
     * @MethodName getThreeCode
     * @Description 获取状态码的最后三位
     * @Author travis-wei
     * @Data 2023/4/7
     * @param bizCodeEnum
     * @Return java.lang.Integer
     **/
    public static Integer getThreeCode(BizCodeEnum bizCodeEnum) {
        return getThreeCode(bizCodeEnum.getCode());
    }
}
