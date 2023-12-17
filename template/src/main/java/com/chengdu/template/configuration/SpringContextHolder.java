/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("SpringContextHolder")
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext CTX;

    public static <T> T getBean(Class<T> clazz) {
        return CTX.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return CTX.getBean(name, clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.CTX = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return CTX;
    }

    public static List<String> getActivateProfiles() {
        return new ArrayList<>(Arrays.asList(CTX.getEnvironment().getActiveProfiles()));
    }
}