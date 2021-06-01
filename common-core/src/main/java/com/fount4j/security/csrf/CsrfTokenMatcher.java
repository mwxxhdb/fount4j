package com.fount4j.security.csrf;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Component
public class CsrfTokenMatcher {
    protected static final Set<String> IGNORED_REQUEST_METHODS = Set.of("GET", "HEAD");

    public boolean doNotNeedToVerify(HttpServletRequest request, HttpServletResponse response, HandlerMethod method) {
        return IGNORED_REQUEST_METHODS.contains(request.getMethod());
    }

    public boolean doNotNeedToSet(HttpServletRequest request, HttpServletResponse response, HandlerMethod method, ModelAndView modelAndView) {
        var status = response.getStatus();
        return status >= 300 && status < 400;
    }
}
