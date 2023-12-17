/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Builder
@Data
@ToString(includeFieldNames = true)
public class AccessLog {

    // 日志类型, 正常or异常
    private String logType = "NORMAL";

    // 请求开始时间
    private long startTime;

    // 来源ip
    private String clientIp;

    // 用户code
    private String userCode;

    // 请求uri
    private String uri;

    // http method
    private String httpMethod;

    // 服务名称
    private String serviceName;

    // 方法名称
    private String methodName;

    // header
    private Map<String, String> headers;

    // 方法参数
    private Object[] methodParameters;

    // 请求参数
    private String requestParameters;

    // 请求体
    private String requestBody;

    // 异常信息
    private String exceptionMessage;

    // 详细异常信息
    private String exceptionInfo;

    // 返回内容
    private String responseContent;

    // 扩展数据
    private Map<String, String> ext;

    // client api 版本
    private Double clientVersion;

    // server api 版本
    private Double serverVersion;

    // http 状态码
    private Integer status;

    // 耗时
    private long cost;

    public String toJsonString() {
        return JSONObject.toJSONString(this,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.IgnoreNonFieldGetter);
    }

}
