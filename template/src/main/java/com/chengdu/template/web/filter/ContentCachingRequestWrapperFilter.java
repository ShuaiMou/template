/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.web.filter;

import com.chengdu.template.web.filter.wrapper.ContentCachingRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.io.IOException;

/**
 * 解决RequestBody只能读取一次的问题
 * 顺序需要设置第一顺序
 */
public class ContentCachingRequestWrapperFilter implements Filter {

    private static final String MULTIPART_TYPE = "multipart/form-data";
    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            if (isFormPost(request)) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(new ContentCachingRequestWrapper(request), response);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean isFormPost(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)) {
            return false;
        }

        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }
        return (contentType.contains(FORM_CONTENT_TYPE) || contentType.contains(MULTIPART_TYPE));
    }

}
