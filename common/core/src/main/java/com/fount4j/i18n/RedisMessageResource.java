package com.fount4j.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.data.redis.core.HashOperations;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Morven
 */
@RequiredArgsConstructor
public class RedisMessageResource extends AbstractMessageSource {
    public static final String CACHE_KEY = "i18n";
    private static final String LANGUAGE_SEPARATOR = "|";

    @Value("fount4j.i18n.defaultLanguage")
    private String defaultLanguage;
    private final HashOperations<String, String, String> hashOperations;

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String lang = locale.getLanguage();
        String format = hashOperations.get(CACHE_KEY, createMessageKey(code, lang));
        if (format == null && !lang.equals(defaultLanguage)) {
            format = hashOperations.get(CACHE_KEY, createMessageKey(code, defaultLanguage));
        }
        return new MessageFormat(Objects.requireNonNullElse(format, code), locale);
    }

    public static String createMessageKey(String code, String language) {
        return code + LANGUAGE_SEPARATOR + language;
    }
}
