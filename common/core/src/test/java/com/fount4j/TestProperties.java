package com.fount4j;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

public class TestProperties {
    public static final String DISABLE_CONSUL = "spring.cloud.consul.enabled=false";
    public static final String LOG_TEST_CONTAINERS = "logging.level.org.testcontainers=WARN";
}
