/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.chengdu.template.configuration;

import com.chengdu.template.web.filter.AccessLogFilter;
import com.chengdu.template.web.filter.ContentCachingRequestWrapperFilter;
import com.chengdu.template.web.interceptor.LoginInterceptor;
import com.chengdu.template.web.interceptor.TimestampInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludeLoginPathPatterns = new ArrayList<>();
        excludeLoginPathPatterns.add("/doc.html");
        excludeLoginPathPatterns.add("/swagger-resources/**");
        excludeLoginPathPatterns.add("/error");
        excludeLoginPathPatterns.add("/webjars/**");
        excludeLoginPathPatterns.add("/user/login");
        excludeLoginPathPatterns.add("/user/register");

        registry.addInterceptor(new TimestampInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(excludeLoginPathPatterns);

    }

    @Bean
    public FilterRegistrationBean<AccessLogFilter> filterRegistrationBean() {
        FilterRegistrationBean<AccessLogFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new AccessLogFilter());
        bean.addUrlPatterns("/*");
        bean.setName("accessLogFilter");
        bean.setOrder(2);	// 值越小，优先级越高
        return bean;
    }

    @Bean
    public FilterRegistrationBean<ContentCachingRequestWrapperFilter> contentCachingRequestWrapperFilterRegistration() {
        FilterRegistrationBean<ContentCachingRequestWrapperFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContentCachingRequestWrapperFilter());
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

}
