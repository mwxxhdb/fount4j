package com.fount4j.ui;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UIWebMvcConfig implements WebMvcConfigurer {
    public static final String STATIC_RESOURCE_PATTERN = "/static/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(STATIC_RESOURCE_PATTERN).addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**").excludePathPatterns(STATIC_RESOURCE_PATTERN);
    }
}
