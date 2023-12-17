/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.utils;

import com.chengdu.template.common.AccessLog;
import com.chengdu.template.common.AccessLogContext;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;

public abstract class ServletResponseHelper {

    private static Logger logger = LoggerFactory.getLogger(ServletResponseHelper.class);

    public static void response(HttpServletResponse response, String contentType, String text) {

        PrintWriter out;
        response.setCharacterEncoding("UTF-8");
        if (contentType != null) {
            response.setContentType(contentType);
        }
        response.setStatus(HttpStatus.OK.value());
        AccessLog.AccessLogBuilder builder = AccessLogContext.getLogBuilder();
        builder.responseContent(text).status(response.getStatus());
        try {
            out = response.getWriter();
            out.print(text);
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
