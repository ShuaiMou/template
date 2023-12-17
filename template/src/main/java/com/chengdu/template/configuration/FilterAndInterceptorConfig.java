package com.chengdu.template.configuration;

import com.chengdu.template.web.filter.AccessLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterAndInterceptorConfig {

    @Bean
    public FilterRegistrationBean<AccessLogFilter> filterRegistrationBean() {
        FilterRegistrationBean<AccessLogFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new AccessLogFilter());
        bean.addUrlPatterns("/*");
        bean.setName("accessLogFilter");
        bean.setOrder(1);	// 值越小，优先级越高
        return bean;
    }

}
