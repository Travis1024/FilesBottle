package com.travis.filesbottle.common.enums;

import cn.hutool.core.lang.Pair;
import lombok.Getter;

/**
 * @ClassName ServeCodeEnum
 * @Description 业务状态枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/31
 */
@Getter
public enum BizCodeEnum {

    /**
     * 业务状态码设定规则：
     *
     * 一共5位，共分为两段
     *
     * 第一段：（2位）10-99（业务模块分类）---- moudleCode
     * 10：未知模块（默认）
     * 11：网关模块（gateway）
     * 12：用户模块（member）
     * 13：搜索模块（search）
     * 14：报表模块（report）
     * 15：微信模块（wxm）
     * 16：系统模块（system）
     * 17：权限认证模块（auth）
     * 18：文档模块（document）
     *
     * 第二段：（3位）000-999（业务处理状态分类）---- bizCode
     *
     */
    /**
     * ======================================== 第一段Enum =========================================
     */
    /**
     * 业务模块分类
     */
    MOUDLE_UNKNOW(10, "未知模块 --> "),
    MOUDLE_GATEWAY(11, "网关模块 --> "),
    MOUDLE_MEMBER(12, "用户模块 --> "),
    MOUDLE_SEARCH(13, "搜索模块 --> "),
    MOUDLE_REPORT(14, "报表模块 --> "),
    MOUDLE_WXM(15, "微信模块 --> "),
    MOUDLE_SYSTEM(16, "系统模块 --> "),
    MOUDLE_AUTH(17, "权限认证模块 --> "),
    MOUDLE_DOCUMENT(18, "文档模块 --> "),

    /**
     * ======================================== 第二段Enum =========================================
     */
    /**
     * 处理成功
     */
    SUCCESS(200, "请求处理成功"),
    /**
     * 客户端错误
     */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "账号未登陆"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "请求未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),
    LOCKED(423, "请求失败, 请稍后重试"), // 不允许并发请求，阻塞中
    TOO_MANY_REQUESTS(429, "请求过于频繁, 请稍后重试"),
    /**
     * 服务端错误
     */
    INTERNAL_SERVER_ERROR(500, "系统异常"),
    /**
     * 自定义服务错误
     */
    TOKEN_CHECK_FAILED(901, "token 验证失败"),
    TOKEN_EXPIRED(902, "token 已过期"),
    TOKEN_REFRESH(903, "token 可以刷新"),
    TOKEN_MISSION(904, "token 缺失"),

    FILE_NOT_SUPPORT_PREVIEW(905, "该文件类型不支持在线预览！"),

    UNKNOW(999, "未知错误");


    private int code;
    private String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @MethodName buildErrorCode
     * @Description 业务错误状态码构建（规则一）
     * @Author travis-wei
     * @Data 2023/4/1
     * @param moudleCode	错误模块码 eg:10
     * @param bizCode	错误类型码 eg:404
     * @Return cn.hutool.core.lang.Pair<java.lang.Integer,java.lang.String>
     **/
    public static Pair<Integer, String> buildErrorCode(BizCodeEnum moudleCode, BizCodeEnum bizCode) {
        int code = moudleCode.getCode() * 1000 + bizCode.getCode();
        return new Pair<>(code, moudleCode.getMessage() + bizCode.getMessage());
    }


    /**
     * @MethodName buildErrorCode
     * @Description 业务错误状态码构建（规则二）
     * @Author travis-wei
     * @Data 2023/4/1
     * @param bizCode   错误类型 eg:404
     * @Return cn.hutool.core.lang.Pair<java.lang.Integer,java.lang.String>
     **/
    public static Pair<Integer, String> buildErrorCode(BizCodeEnum bizCode) {
        int code = BizCodeEnum.MOUDLE_UNKNOW.getCode() * 1000 + bizCode.getCode();
        return new Pair<>(code, BizCodeEnum.MOUDLE_UNKNOW.getMessage() + bizCode.getMessage());
    }

    /**
     * @MethodName buildSuccessCode
     * @Description 业务成功状态码构建（规则一）
     * @Author travis-wei
     * @Data 2023/4/1
     * @param moudleCode 成功模块码 eg:11 (网关模块)
     * @Return cn.hutool.core.lang.Pair<java.lang.Integer,java.lang.String>
     **/
    public static Pair<Integer, String> buildSuccessCode(BizCodeEnum moudleCode) {
        int code = moudleCode.getCode() * 1000 + BizCodeEnum.SUCCESS.getCode();
        return new Pair<>(code, moudleCode.getMessage() + BizCodeEnum.SUCCESS.getMessage());
    }

    /**
     * @MethodName buildSuccessCode
     * @Description 业务成功状态码构建（规则二）
     * @Author travis-wei
     * @Data 2023/4/1
     * @param
     * @Return cn.hutool.core.lang.Pair<java.lang.Integer,java.lang.String>
     **/
    public static Pair<Integer, String> buildSuccessCode() {
        int code = BizCodeEnum.MOUDLE_UNKNOW.getCode() * 1000 + BizCodeEnum.SUCCESS.getCode();
        return new Pair<>(code, BizCodeEnum.MOUDLE_UNKNOW.getMessage() + BizCodeEnum.SUCCESS.getMessage());
    }

}
