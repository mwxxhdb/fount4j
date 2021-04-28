package com.fount4j.i18n;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @author Morven
 */
public class SaTokenLocaleResolver extends SessionLocaleResolver {
    public static final String ATTR_LANGUAGE = "locale";
    public static final String ATTR_TIMEZONE = "timezone";

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        var language = StpUtil.getSession().getString(ATTR_LANGUAGE);
        if (language == null) {
            return super.resolveLocaleContext(request);
        }

        var timezone = StpUtil.getSession().getString(ATTR_TIMEZONE);
        return new SimpleTimeZoneAwareLocaleContext(Locale.forLanguageTag(language), TimeZone.getTimeZone(timezone));
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        super.setLocaleContext(request, response, localeContext);
        if (!StpUtil.isLogin()) {
            return;
        }
        Optional.ofNullable(localeContext)
            .map(LocaleContext::getLocale)
            .map(Locale::getLanguage)
            .ifPresent(lan -> StpUtil.getSession().set(ATTR_LANGUAGE, lan));
    }
}
