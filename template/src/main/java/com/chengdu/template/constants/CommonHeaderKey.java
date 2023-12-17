package com.chengdu.template.constants;

import org.apache.commons.lang3.StringUtils;

public final class CommonHeaderKey {

    private CommonHeaderKey() {
        throw new RuntimeException("Do not try to make an instance");
    }

    /**
     * app 发送的请求头中应包含下面四个字段
     **/
    public static final String OS_TYPE = "os-type";

    public static final String OS_VERSION = "os-version";

    public static final String CLIENT_TYPE = "client-type";

    public static final String CLIENT_VERSION = "client-version";

    /**
     * apiVersion
     */
    public static final String API_VERSION = "version";

    /**
     * 认证
     */
    public static final String AUTHORIZATION = "Authorization";

    public static final String TOKEN = "TOKEN";

    public static final String STAMP = "Stamp";

    public static final String HOST = "host";

    /**
     * Content-Type
     */
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    public static final String ACCEPT = "Accept";

    /**
     * 跳过校验时间戳
     */
    public static final String SKIP_CHECK_STAMP = "Skip-Check-Stamp";

    /**
     * 跳过校验 token
     */
    public static final String SKIP_CHECK_LOGIN = "Skip-Check-Login";

    public static final String[] CORS_ALLOW_HEADERS = {
            OS_TYPE,
            OS_VERSION,
            CLIENT_TYPE,
            CLIENT_VERSION,
            API_VERSION,
            AUTHORIZATION,
            TOKEN,
            STAMP,
            CONTENT_TYPE,
            ACCEPT
    };

    public static String makeCORSAllowHeader() {
        return StringUtils.join(CORS_ALLOW_HEADERS, ",");
    }

}
