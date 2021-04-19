package com.fount4j.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fount4j.core.bo.BaseResult;
import com.fount4j.security.csrf.InvalidCsrfTokenException;
import com.fount4j.security.csrf.NoCsrfTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionResolver {
    private final ObjectMapper objectMapper;

    @ExceptionHandler({NoCsrfTokenException.class, InvalidCsrfTokenException.class})
    public ModelAndView handleCsrfException(HttpServletRequest request, HttpServletResponse response) {
        if (isApiCall(request)) {
            try {
                objectMapper.writeValue(response.getWriter(), BaseResult.fail("Csrf failed"));
                return null;
            } catch (IOException e) {
                log.error("Fail to handle CSRF exceptions", e);
            }
        }
        return new ModelAndView("/403");
    }

    private boolean isApiCall(HttpServletRequest request) {
        if (request.getHeader("Accept").contains("json")) {
            return true;
        }
        String header = request.getHeader("X-Request-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }
}
