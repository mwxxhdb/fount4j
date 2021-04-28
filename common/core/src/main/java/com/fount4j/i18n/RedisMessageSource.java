package com.fount4j.i18n;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.data.redis.core.HashOperations;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Morven
 */
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
        var format = Optional.ofNullable(hashOperations.get(CACHE_KEY, createMessageKey(code, lang + "_" + country)))
            .or(() -> Optional.ofNullable(hashOperations.get(CACHE_KEY, createMessageKey(code, lang))))
            .orElseGet(() -> hashOperations.get(CACHE_KEY, createMessageKey(code, defaultLanguage)));
        return new MessageFormat(Objects.requireNonNullElse(format, code), locale);
    }

    public static String createMessageKey(String code, String language) {
        return code + LANGUAGE_SEPARATOR + language;
    }
}
