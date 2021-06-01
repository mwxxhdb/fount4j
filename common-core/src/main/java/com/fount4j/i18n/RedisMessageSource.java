package com.fount4j.i18n;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Morven
 */
@Component
@SuppressWarnings("java:S3252")
public class RedisMessageSource extends AbstractMessageSource {
    public static final String CACHE_KEY = "i18n";
    private static final String LANGUAGE_SEPARATOR = "|";

    @Value("${fount4j.i18n.defaultLanguage:en}")
    private String defaultLanguage;
    private final HashOperations<String, String, String> hashOperations;

    public RedisMessageSource(@Qualifier("hashStringOperations") HashOperations<String, String, String> hashOperations) {
        this.hashOperations = hashOperations;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        var lang = locale.getLanguage();
        var country = locale.getCountry();
        if (StrUtil.isNotEmpty(country)) {
            var value = hashOperations.get(createKey(lang + "_" + country), code);
            if (value != null) return new MessageFormat(value, locale);
        }

        var value = hashOperations.get(createKey(lang), code);
        if (value != null) return new MessageFormat(value, locale);

        value = hashOperations.get(createKey(defaultLanguage), code);
        return new MessageFormat(Objects.requireNonNullElse(value, code), locale);
    }

    public Map<String, String> allMessages(Locale locale) {
        Map<String, String> result = new HashMap<>();
        var lang = locale.getLanguage();
        var country = locale.getCountry();
        if (StrUtil.isNotEmpty(country)) {
            result.putAll(hashOperations.entries(createKey(lang + "_" + country)));
        }
        hashOperations.entries(createKey(lang)).forEach(result::putIfAbsent);
        hashOperations.entries(createKey(defaultLanguage)).forEach(result::putIfAbsent);
        return result;
    }

    public Map<String, String> allMessages() {
        return allMessages(LocaleContextHolder.getLocale());
    }

    public static String createKey(String language) {
        return CACHE_KEY + LANGUAGE_SEPARATOR + language;
    }
}
