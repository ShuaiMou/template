/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.web.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 时间戳认证
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckTimestamp {

    /**
     * 参数key
     *
     * @return
     */
    String name() default "stamp";

    /**
     * 是否必传
     * @return
     */
    boolean required() default false;

    /**
     * 允许的时间差
     *
     * @return
     */
    long before() default 5 * 60 * 1000;

    /**
     * 允许的时间差
     *
     * @return
     */
    long after() default 5 * 60 * 1000;

    /**
     * 是否只接受参数形式传递，不接收header形式传递
     *
     * @return
     */
    boolean parameterOnly() default false;
}
