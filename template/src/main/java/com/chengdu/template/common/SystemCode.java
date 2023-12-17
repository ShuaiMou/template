/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.common;

public enum SystemCode implements ErrorCode {

    /**
     * 未知异常
     */
    UNKNOWN(-10000, "系统异常"),

    /**
     * 时间戳校验异常
     */
    TIMESTAMP_ERROR(-10001, "非法请求"),

    /**
     * token非法
     */
    TOKEN_ERROR(-10002, "非法请求"),

    /**
     * 参数异常
     */
    PARAMETER_ERROR(-10003, "参数不合法"),

    /**
     * Sign非法
     */
    SIGN_ERROR(-10004, "非法请求"),

    /**
     * 未登录
     */
    NOT_LOGIN_ERROR(-10005, "未登录"),

    /**
     * License非法
     */
    LICENSE_ERROR(-10006, "非法请求"),

    /**
     * request method非法
     */
    REQUEST_METHOD_ERROR(-10007, "非法请求方法"),

    /**
     * 第三方服务请求异常
     */
    THIRD_PARTY_INVOCATION_ERROR(-10008, "第三方服务请求异常"),
    ;

    final int value;
    final String desc;

    SystemCode(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
