/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.common;

public abstract class AccessLogContext {

    private static ThreadLocal<AccessLog.AccessLogBuilder> CONTEXT = new ThreadLocal<>();

    public static AccessLog.AccessLogBuilder getLogBuilder() {
        AccessLog.AccessLogBuilder log = CONTEXT.get();
        if (log == null) {
            log = AccessLog.builder().startTime(System.currentTimeMillis()).logType("NORMAL");
            CONTEXT.set(log);
        }
        return log;
    }

    public static void setLogBuilder(AccessLog.AccessLogBuilder log) {
        CONTEXT.set(log);
    }

    public static void remove() {
        CONTEXT.remove();
    }

}
