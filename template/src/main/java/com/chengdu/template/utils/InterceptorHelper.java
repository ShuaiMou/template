package com.chengdu.template.utils;

import com.chengdu.template.configuration.SpringContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public abstract class InterceptorHelper {

    /**
     * 线上环境
     */
    public static final String PRODUCTION_ENV = "prod";

    /**
     * 沙盒环境
     */
    public static final String SANDBOX_ENV = "sandbox";

    /**
     * dev 环境
     */
    public static final String DEVELOP_ENV = "dev";

    /**
     * swagger
     */
    public static final String SWAGGER_ENV = "swagger";

    /**
     * header ticket
     */
    public static final String HEADER_TICKET = "ticket";



    /**
     * @param request
     * @description 判断请求是否是否需要拦截
     */
    public static boolean validateEnvironment(HttpServletRequest request) {
        List<String> profiles = SpringContextHolder.getActivateProfiles();
        if (CollectionUtils.isEmpty(profiles)) {
            log.error("该环境未找到profile变量");
            return false;
        }

        return !profiles.contains(PRODUCTION_ENV) && !profiles.contains(SANDBOX_ENV)
                && (SWAGGER_ENV.equals(request.getHeader(HEADER_TICKET)) || profiles.contains(DEVELOP_ENV));
    }
}
