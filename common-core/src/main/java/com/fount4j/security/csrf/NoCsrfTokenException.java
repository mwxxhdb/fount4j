package com.fount4j.security.csrf;

public class NoCsrfTokenException extends Exception {
    public NoCsrfTokenException() {
    }

    public NoCsrfTokenException(String message) {
        super(message);
    }

    public NoCsrfTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
