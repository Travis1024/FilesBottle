package com.travis.filesbottle.document.enums;

import lombok.Getter;

/**
 * @ClassName DocStateEnum
 * @Description 文档状态枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/10
 */
@Getter
public enum DocStateEnum {
    /**
     * 建立索引时的等待状态，默认都是等待状态
     */
    WAITE(0),
    /**
     * 进行中的状态
     */
    ON_PROCESS(1),
    /**
     * 成功状态
     */
    SUCCESS(2),
    /**
     * 失败状态
     */
    FAIL(3);

    private Integer code;

    DocStateEnum(int code) {
        this.code = code;
    }
}
