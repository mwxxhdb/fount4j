package com.fount4j.security.csrf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CsrfConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(csrfTokenInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
    }

    @Bean
    public CsrfInterceptor csrfTokenInterceptor() {
        return new CsrfInterceptor();
    }
}
