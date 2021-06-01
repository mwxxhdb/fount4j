package com.fount4j.i18n;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author Morven
 */
@Configuration
public class I18nConfig {
    public static final String VALUE_SUPPORTED_LOCALES = "${fount4j.i18n.supported-locales:en,zh,sv}";

    @Bean
    public LocaleResolver localeResolver(@Value(VALUE_SUPPORTED_LOCALES) String supportedLocales) {
        var localeResolver = new SaTokenLocaleResolver();
        localeResolver.setSupportedLocales(Arrays.stream(supportedLocales.split(","))
            .map(Locale::forLanguageTag)
            .collect(Collectors.toList()));
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    @Bean
    public MessageSource messageSource(@Qualifier("hashStringOperations") HashOperations<String, String, String> operations) {
        return new RedisMessageSource(operations);
    }
}
