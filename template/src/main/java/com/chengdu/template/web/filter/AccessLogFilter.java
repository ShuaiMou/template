package com.chengdu.template.web.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chengdu.template.common.AccessLog;
import com.chengdu.template.common.AccessLogContext;
import com.chengdu.template.common.ThreadLocalContextHolder;
import com.chengdu.template.constants.WebConstants;
import com.chengdu.template.utils.HttpServletRequestHelper;
import com.chengdu.template.utils.IPUtils;
import com.chengdu.template.web.filter.wrapper.ContentCachingRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.*;

import static org.springframework.http.HttpMethod.HEAD;

public class AccessLogFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static String HOSTNAME;

    /**
     * 日志黑名单，类似心跳，监控或者敏感信息等日志不需要记入日志中
     */
    public static List<String> LOG_BLACK_LIST = Arrays.asList(
            "/actuator/health", "/actuator/info", "/actuator"
    );

    public static final String RPC_REQ_ID = "reqID";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            // 不需要处理
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {


        MDC.put("hostname", HOSTNAME);
        AccessLog.AccessLogBuilder builder = AccessLogContext.getLogBuilder();
        Enumeration<String> names = request.getParameterNames();;
        List<String> keys = new ArrayList<>();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            keys.add(name);
        }

        // 排序
        Collections.sort(keys);
        StringBuilder parsb = new StringBuilder();
        Map<String, String[]> parsMap = new HashMap<>();

        for (String name : keys) {
            if (parsb.length() > 0) {
                parsb.append("&");
            }
            String[] values = request.getParameterValues(name);
            if (values == null) {
                values = new String[]{""};
            }
            for (String value : values) {
                parsb.append(name).append("=").append(value);
            }
            parsMap.put(name, values);
        }

        ThreadLocalContextHolder.setValue(WebConstants.REQUEST_PARAMETERS, parsb.toString());

        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, httpServletRequest.getHeader(name));

        }

        String requestBody = null;
        if (request instanceof ContentCachingRequestWrapper) {
            requestBody = HttpServletRequestHelper.getRequestBody(request);
        }

        builder.clientIp(IPUtils.getIpAddress(httpServletRequest)).httpMethod(httpServletRequest.getMethod())
                .uri(URLDecoder.decode(httpServletRequest.getRequestURI()))
                .headers(headers)
                .requestBody(requestBody)
                .requestParameters(toString(parsMap));

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            builder.logType("ERROR").exceptionMessage(e.getMessage());
            StringWriter sw = null;
            PrintWriter pw = null;

            try {
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                logger.info("pw: {}", pw);
                e.printStackTrace(pw);
                builder.exceptionInfo(sw.toString());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                try {
                    if (sw != null) {
                        sw.close();
                    }
                    if (pw != null) {
                        pw.close();
                    }
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
            throw e;
        } finally {
            long now = System.currentTimeMillis();
            long cost = now - builder.build().getStartTime();
            builder.cost(cost);
            if (needRecord(httpServletRequest)) {
                logger.info(builder.build().toJsonString() + " " + cost + "ms");
            }
            AccessLogContext.remove();
            ThreadLocalContextHolder.clear();
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
        System.out.println("destroy方法执行");
    }

    public String toString(Map<String, String[]> map) {
        return JSONObject.toJSONString(map, SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
    }

    private boolean needRecord(HttpServletRequest request) {
        if (LOG_BLACK_LIST.contains(request.getServletPath())) {
            return false;
        }
        if (request.getMethod().equals(HEAD.name())) {
            return false;
        }
        return true;
    }
}
