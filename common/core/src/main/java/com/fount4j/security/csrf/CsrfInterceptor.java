package com.fount4j.security.csrf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.fount4j.security.csrf.CsrfToken.ATTRIBUTE_NAME;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@Slf4j
public class CsrfInterceptor implements HandlerInterceptor {
    private static final Map<String, CsrfToken> CACHE = new HashMap<>(100);

    @Value("${fount4j.security.csrf.expireInSeconds:86400}")
    private int defaultExpireInSeconds;
    @Value("${fount4j.security.csrf.headerName:X-CSRF-TOKEN}")
    private String defaultHeaderName;
    @Value("${fount4j.security.csrf.parameterName:_csrf}")
    private String defaultParameterName;

    @Autowired
    private CsrfTokenMatcher matcher;

    @Override
    @SuppressWarnings("java:S3516")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;

        HandlerMethod method = (HandlerMethod) handler;
        CheckCsrfToken config = method.getMethodAnnotation(CheckCsrfToken.class);
        if (config == null) config = method.getBean().getClass().getAnnotation(CheckCsrfToken.class);

        if (config != null && !config.value()) return true;
        if (config == null && matcher.doNotNeedToVerify(request, response, method)) return true;

        String headerName = ofNullable(config).map(CheckCsrfToken::headerName).filter(not(String::isEmpty)).orElse(defaultHeaderName);
        String token = request.getHeader(headerName);
        if (token == null) {
            String parameterName = ofNullable(config).map(CheckCsrfToken::parameterName).filter(not(String::isEmpty)).orElse(defaultParameterName);
            token = request.getParameter(parameterName);
        }

        if (token == null) throw new NoCsrfTokenException();

        cleanCache();

        CsrfToken csrfToken = CACHE.remove(token);
        if (csrfToken == null) throw new InvalidCsrfTokenException(token);
        if (!request.getSession().getId().equals(csrfToken.getSessionId())) {
            throw new InvalidCsrfTokenException(token, "Session id not match");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!(handler instanceof HandlerMethod)) return;

        HandlerMethod method = (HandlerMethod) handler;
        SetCsrfToken config = method.getMethodAnnotation(SetCsrfToken.class);
        if (config == null) {
            config = method.getBean().getClass().getAnnotation(SetCsrfToken.class);
        }
        if (config != null && !config.value()) return;
        if (config == null && matcher.doNotNeedToSet(request, response, method, modelAndView)) return;

        cleanCache();

        var token = UUID.randomUUID().toString();
        var csrfToken = new CsrfToken.CsrfTokenBuilder()
            .token(UUID.randomUUID().toString())
            .expireTime(ofNullable(config)
                .map(SetCsrfToken::expireInSeconds)
                .map(seconds -> System.currentTimeMillis() + seconds * 1000)
                .orElseGet(() -> System.currentTimeMillis() + defaultExpireInSeconds * 1000L))
            .headerName(ofNullable(config).map(SetCsrfToken::headerName).filter(not(String::isEmpty)).orElse(defaultHeaderName))
            .parameterName(ofNullable(config).map(SetCsrfToken::parameterName).filter(not(String::isEmpty)).orElse(defaultParameterName))
            .sessionId(request.getSession().getId())
            .build();
        CACHE.put(token, csrfToken);
        request.setAttribute(ATTRIBUTE_NAME, csrfToken);
    }

    private void cleanCache() {
        var now = System.currentTimeMillis();
        CACHE.values().stream()
            .filter(token -> now > token.getExpireTime())
            .forEach(token -> CACHE.remove(token.getToken()));
    }
}
