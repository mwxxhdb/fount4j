package com.fount4j.test;

import lombok.experimental.UtilityClass;

/**
 * @author Morven
 */
@UtilityClass
public class TestProperties {
    public static final String DISABLE_CONSUL = "spring.cloud.consul.enabled=false";
    public static final String LOG_TEST_CONTAINERS = "logging.level.org.testcontainers=WARN";
}
