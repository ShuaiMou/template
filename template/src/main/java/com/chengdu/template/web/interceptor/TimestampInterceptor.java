/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.web.interceptor;

import com.chengdu.template.common.ErrorCode;
import com.chengdu.template.common.JsonResult;
import com.chengdu.template.common.SystemCode;
import com.chengdu.template.utils.InterceptorHelper;
import com.chengdu.template.utils.ServletResponseHelper;
import com.chengdu.template.web.auth.CheckTimestamp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

/**
 * timestamp 验证
 */
public class TimestampInterceptor implements HandlerInterceptor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 如果是swagger请求直接通过
        if (InterceptorHelper.validateEnvironment(request)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        CheckTimestamp checkTimestamp = AnnotatedElementUtils.findMergedAnnotation(method, CheckTimestamp.class);

        if (checkTimestamp == null) {
            checkTimestamp =
                    AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), CheckTimestamp.class);
        }

        if (checkTimestamp != null && checkTimestamp.required()) {
            logger.info("timestamp key ==> {}", checkTimestamp.name());
            String stamp = request.getParameter(checkTimestamp.name());
            if (StringUtils.isBlank(stamp) && !checkTimestamp.parameterOnly()) {
                stamp = request.getHeader(checkTimestamp.name());
            }
            logger.info("timestamp ==> {}", stamp);
            if (StringUtils.isNotBlank(stamp)) {
                try {
                    long current = System.currentTimeMillis();
                    long timestamp = Long.valueOf(stamp);
                    logger.info("pars stamp: {}, current: {}, rightful：[{}, {}]", stamp, current,
                            current - checkTimestamp.before(), current + checkTimestamp.after());
                    if (timestamp >= current - checkTimestamp.before()
                            && timestamp <= current + checkTimestamp.after()) {
                        logger.info("stamp 合法，验证通过");
                        return true;
                    }
                } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                }
            }

            logger.warn("stamp 不合法，验证不通过");
            handleFailures(request, response, handler);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    protected void handleFailures(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.warn("timestamp 校验失败...");
        ErrorCode code = SystemCode.TIMESTAMP_ERROR;
        String result = JsonResult.error(code.getValue(), code.getDesc()).toJsonString();
        ServletResponseHelper.response(response, MimeTypeUtils.APPLICATION_JSON_VALUE, result);
    }

}
