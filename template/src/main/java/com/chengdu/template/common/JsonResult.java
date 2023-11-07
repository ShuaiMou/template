package com.chengdu.template.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class JsonResult<T> implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonResult.class);

    // 限制日志长度为 1024
    private static final int DEFAULT_MAX_LOG_LENGTH = 1024;

    public static final int DEFAULT_SUCCESS_CODE = 0;

    public static final int DEFAULT_ERROR_CODE = -1;

    /**
     * 默认可以对前端展示的错误 code
     */
    public static final int DEFAULT_POP_ERROR_CODE = 1;

    /**
     * 0 ok;
     */
    int code;

    T data;

    String info;

    public JsonResult() {
    }

    public JsonResult(int code, String info, T data) {
        this.code = code;
        this.data = data;
        this.info = info;
    }

    public static <T> JsonResult<T> success() {
        return success(null);
    }

    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> result = new JsonResult<T>(JsonResult.DEFAULT_SUCCESS_CODE, "操作成功", data);
        if (LOGGER.isDebugEnabled()) {
            String logString = result.toString();
            if (logString.length() > DEFAULT_MAX_LOG_LENGTH) {
                logString = logString.substring(0, DEFAULT_MAX_LOG_LENGTH);
            }
            LOGGER.debug(logString);
        }
        return result;
    }

    public static <T> JsonResult<T> popError(String info) {
        return new JsonResult<T>(JsonResult.DEFAULT_POP_ERROR_CODE, info, null);
    }

    public static <T> JsonResult<T> error(String info) {
        return new JsonResult<T>(JsonResult.DEFAULT_ERROR_CODE, info, null);
    }

    public static <T> JsonResult<T> error(int code, String info) {
        return new JsonResult<T>(code, info, null);
    }

    public static <T> JsonResult<T> error(int code, String info, T data) {
        return new JsonResult<T>(code, info, data);
    }

    public static <T> JsonResult<T> error(ErrorCode code) {
        return new JsonResult<T>(code.getValue(), code.getDesc(), null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

    public String toJsonString() {
        return JSONObject
                .toJSONString(this, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue,
                        SerializerFeature.DisableCircularReferenceDetect);
    }

}

