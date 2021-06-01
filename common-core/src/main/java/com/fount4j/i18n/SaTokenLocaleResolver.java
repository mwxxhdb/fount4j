package com.fount4j.i18n;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Morven
 */
public class SaTokenLocaleResolver extends AcceptHeaderLocaleResolver {
    public static final String ATTR_LANGUAGE = "locale";
    public static final String ATTR_TIMEZONE = "timezone";


    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String language = null;
        String timezone = null;
        if (StpUtil.isLogin()) {
            language = StpUtil.getSession().getString(ATTR_LANGUAGE);
            timezone = StpUtil.getSession().getString(ATTR_TIMEZONE);
        }
        if (language == null || timezone == null) {
            return super.resolveLocale(request);
        }
        return Locale.forLanguageTag(language);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (!StpUtil.isLogin()) {
            return;
        }
        Optional.ofNullable(locale)
            .map(Locale::getLanguage)
            .ifPresent(lan -> StpUtil.getSession().set(ATTR_LANGUAGE, lan));
    }
}
