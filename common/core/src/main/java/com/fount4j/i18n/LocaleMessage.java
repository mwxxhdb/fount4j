package com.fount4j.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Morven
 */
@Component
@RequiredArgsConstructor
public class LocaleMessage {
    private final MessageSource messageSource;

    public String getMessage(String code) {
        var locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, null, locale);
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, null, LocaleContextHolder.getLocale());
    }
}
