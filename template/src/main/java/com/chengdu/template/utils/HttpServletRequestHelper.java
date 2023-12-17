/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.utils;

import com.chengdu.template.common.ClientType;
import com.chengdu.template.constants.CommonHeaderKey;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public final class HttpServletRequestHelper {

    private static final String HTTPS = "https";

    /**
     * 获取RequestBody数据
     *
     * @param request 最好是 ContentCachingRequestWrqpper
     * @return
     * @throws IOException
     */
    public static String getRequestBody(HttpServletRequest request) throws IOException {
        return StreamUtils.copyToString(request.getInputStream(), getCharset(request));
    }

    /**
     * 获取请求体的字符编码
     */
    public static Charset getCharset(HttpServletRequest request) {
        String charset = request.getCharacterEncoding();
        if (charset != null && Charset.isSupported(charset)) {
            return Charset.forName(charset);
        }
        return StandardCharsets.UTF_8;
    }

    public static boolean isPostJson(HttpServletRequest request) {
        String contentType = request.getContentType();
        return (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE) &&
                HttpMethod.POST.matches(request.getMethod()));
    }

    public static boolean isHttps() {
        long beginTime = System.currentTimeMillis();
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String scheme = request.getScheme();
            String url = request.getRequestURL().toString();
            String httpsTag = request.getHeader("https-tag");
            String sslHeader = request.getHeader("x-ssl-header");

            log.info("scheme : {},url : {}, httpsTag:{}, sslHeader:{}", scheme, url, httpsTag, sslHeader);
            if ((!sslHeader.isEmpty() && "1".equals(sslHeader))) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("is https cost {} ms。", endTime - beginTime);
        }
        return Boolean.FALSE;
    }

    public static boolean isUrlEncodedFormBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType);
    }

    public static boolean isJsonBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    public static ClientType getFromClientType(HttpServletRequest request) {
        String headerVal = request.getHeader(CommonHeaderKey.CLIENT_TYPE);
        // 如果请求中没有，暂时认为是 null，后续有需求改成 OTHER
        if (headerVal == null) {
            return null;
        }

        return ClientType.getByDescription(headerVal);
    }
}
