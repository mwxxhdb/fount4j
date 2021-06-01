package com.fount4j.security.csrf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SetCsrfToken {
    boolean value() default true;

    int expireInSeconds() default 0;

    String headerName() default "";

    String parameterName() default "";
}
