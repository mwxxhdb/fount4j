package com.fount4j.security.csrf;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CsrfToken {
    public static final String ATTRIBUTE_NAME = "_csrf";
    public static final String HEADER_META_NAME = "_csrf_header";
    public static final String DEFAULT_HEADER_NAME = "X-CSRF-TOKEN";

    private final String token;
    private final long expireTime;
    private final String headerName;
    private final String parameterName;
}
