package com.fount4j.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * @author Morven
 */
@Configuration
public class I18nConfig {
    @Bean
    public LocaleResolver localeResolver() {
        SaTokenLocaleResolver localeResolver = new SaTokenLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }
}
