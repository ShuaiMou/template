/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.constants;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

/**
 * 公用常量类
 */
public final class CommonConstants {

    public static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {
            };

    public static final String INSTANCE_INIT_ERROR_HINT = "Do not try to make an instance";

    public static final int ZERO = 0;

    public static final Long LONG_ZERO = 0L;

    public static final int ONE = 1;

    public static final int TWO = 2;

    public static final int THREE = 3;

    public static final int TEN = 10;

    public static final Long DEFAULT_USER_ID = 0L;

    public static final Long DEFAULT_USER_EXT_ID = 0L;

    public static final Long DEFAULT_COUNT = 0L;

    public static final Double DEFAULT_RATE = 0.00;

    public static final int NEGATIVE_ONE = -1;

    public static final int NEGATIVE_TWO = -2;

    public static final int MONEY_SCALE = 2;

    public static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * 超时时间
     */
    public static final Integer HTTP_TIMEOUT = 20000;

    public static final String COMMA = ",";

    public static final String COMMA_CN = "，";

    public static final String DOT = ".";

    public static final String DOT_CN = "。";

    public static final String DASH = "-";

    public static final String SLASH = "/";

    public static final String UNDERLINE = "_";

    public static final String STAR = "*";

    public static final String COLON = ":";

    public static final String COLON_CN = "：";

    public static final String SEMICOLON = ";";

    public static final String SEMICOLON_CN = "；";

    public static final String SPACE = " ";

    public static final String ENTER_COMPILE = "\\\n";

    public static final String ENTER = "\n";

    public static final String AND = "&";

    public static final String MATCH_CHARACTER = "%";

    public static final String STOP_MARK = "、";

    public static final String BLANK_STR = "";

    public static final String GREATER_THAN_SIGN = ">";

    public static final String LESS_THAN_SIGN = "<";

    public static final String TILDE_EN_SIGN = "~";

    public static final String EQUAL_SIGN = "=";

    public static final String BACKSLASH = "\\";


    /**
     * https://{host}{uri}
     */
    public static final String HTTPS_TEMPLATE = "https://%s%s";

    public static final String HTTP_TEMPLATE = "http://%s%s";

}
