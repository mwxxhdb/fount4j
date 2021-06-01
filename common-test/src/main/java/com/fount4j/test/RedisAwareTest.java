package com.fount4j.test;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public interface RedisAwareTest {
    @Container
    GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis")).withExposedPorts(6379);

    class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "spring.redis.host=" + redis.getHost(),
                "spring.redis.port=" + redis.getFirstMappedPort()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
