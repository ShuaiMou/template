package com.chengdu.template.web.interceptor;

import com.chengdu.template.constants.CommonHeaderKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${skip.check.enable:false}")
    private boolean skipEnable;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        log.info("此次请求类型为 {}, 请求路径为 {}", request.getMethod(), request.getRequestURL());

        String skipCheckLogin = request.getHeader(CommonHeaderKey.SKIP_CHECK_LOGIN);
        log.info("skip check skipEnable: {}, skipCheckLogin: {}", skipEnable, skipCheckLogin);

        boolean skip = false;
        // 测试环境跳过检查
        if (skipEnable && StringUtils.isNotBlank(skipCheckLogin)) {
            skip = true;
            log.info("=============测试环境跳过检查=================");
        }

        return skip || checkLogin(request, response);
    }

    protected boolean checkLogin(HttpServletRequest request, HttpServletResponse response) {
//        request.setAttribute("user", user);

        return true;
    }

}
