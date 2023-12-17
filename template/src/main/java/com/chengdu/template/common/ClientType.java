package com.chengdu.template.common;

import org.apache.commons.lang3.StringUtils;

/**
 * 登录的端类型
 */
public enum ClientType {

    /**
     * 其他登录端类型
     */
    OTHER(0, "OTHER"),

    /**
     * 用于 native 加载 H5 的场景
     */
    H5(1, "H5"),

    /**
     * 安卓端登录
     */
    ANDROID(2, "Android"),

    /**
     * iOS 端登录
     */
    IOS(3, "iOS"),

    /**
     * WEB 登录
     */
    WEB(4, "WEB"),

    /**
     * 微信公众号登录
     */
    WX(5, "WX"),


    ;

    final int value;
    final String desc;

    ClientType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static ClientType getByVal(Integer value) {
        if (value == null) {
            return OTHER;
        }

        for (ClientType item : values()) {
            if (item.value == value) {
                return item;
            }
        }

        return OTHER;
    }

    public static ClientType getByDescription(String description) {
        if (StringUtils.isBlank(description)) {
            return OTHER;
        }

        for (ClientType item : values()) {
            if (item.desc.equals(description)) {
                return item;
            }
        }

        return OTHER;
    }
}
