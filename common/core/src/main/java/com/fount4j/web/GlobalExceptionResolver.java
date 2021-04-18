package com.fount4j.web;

import com.fount4j.security.csrf.InvalidCsrfTokenException;
import com.fount4j.security.csrf.NoCsrfTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionResolver {
    @ExceptionHandler({NoCsrfTokenException.class, InvalidCsrfTokenException.class})
    public ResponseEntity test(NoCsrfTokenException exception, HttpServletRequest request) {
        log.info("----------------- {}", request.getContentType());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ModelAndView("403"));
    }
}
