package com.fount4j.security.csrf;

import lombok.Getter;

public class InvalidCsrfTokenException extends Exception {
    @Getter
    private final String token;

    public InvalidCsrfTokenException(String token) {
        this.token = token;
    }

    public InvalidCsrfTokenException(String token, String message) {
        super(message);
        this.token = token;
    }

    public InvalidCsrfTokenException(String token, String message, Throwable cause) {
        super(message, cause);
        this.token = token;
    }

    public InvalidCsrfTokenException(String token, Throwable cause) {
        super(cause);
        this.token = token;
    }
}
