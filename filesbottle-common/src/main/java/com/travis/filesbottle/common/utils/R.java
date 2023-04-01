package com.travis.filesbottle.common.utils;

import com.travis.filesbottle.common.enums.BizCodeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName R
 * @Description 通用返回类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/1
 */
@Data
@Accessors(chain = true)
public class R<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 返回数据（常见的为String）
     */
    private T data;
    /**
     * 错误提示，用户可阅读
     */
    private String message;

    public static R success() {
        return new R().setCode(BizCodeEnum.buildSuccessCode().getKey()).setMessage(BizCodeEnum.buildSuccessCode().getValue());
    }

    public static R success(String message) {
        return new R().setCode(BizCodeEnum.buildSuccessCode().getKey()).setMessage(message);
    }

    public static <T> R success(T data) {
        return new R();
    }

    public static R error(Integer code) {
        return new R();
    }
}
