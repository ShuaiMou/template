/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.common;

import java.util.HashMap;
import java.util.Map;

public abstract class ThreadLocalContextHolder {

    private static final ThreadLocal<Map<String, Object>> THREADLOCAL_CONTEXT = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }

    };

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     */
    public static Object getValue(String key) {
        if (THREADLOCAL_CONTEXT.get() == null) {
            return null;
        }
        return THREADLOCAL_CONTEXT.get().get(key);
    }

    /**
     * 存储
     *
     * @param key
     * @param value
     * @return
     */
    public static Object setValue(String key, Object value) {
        Map<String, Object> cacheMap = THREADLOCAL_CONTEXT.get();
        if (cacheMap == null) {
            cacheMap = new HashMap<>();
            THREADLOCAL_CONTEXT.set(cacheMap);
        }
        return cacheMap.put(key, value);
    }

    /**
     * 根据key移除值
     *
     * @param key
     */
    public static void removeValue(String key) {
        Map<String, Object> cacheMap = THREADLOCAL_CONTEXT.get();
        if (cacheMap != null) {
            cacheMap.remove(key);
        }
    }


    /**
     * 重置
     */
    public static void clear() {
        if (THREADLOCAL_CONTEXT.get() != null) {
            THREADLOCAL_CONTEXT.get().clear();
        }
    }

}
