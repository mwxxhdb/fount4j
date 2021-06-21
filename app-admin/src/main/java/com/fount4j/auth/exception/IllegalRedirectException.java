package com.fount4j.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Morven 2021-06-21
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class IllegalRedirectException extends Exception {
    public IllegalRedirectException(String message) {
        super(message);
    }

    public IllegalRedirectException(String message, Throwable cause) {
        super(message, cause);
    }
}
