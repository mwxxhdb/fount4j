package com.fount4j.i18n;

import com.fount4j.cache.RedisConfig;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.fount4j.TestProperties.DISABLE_CONSUL;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    DISABLE_CONSUL
})
@ContextConfiguration(initializers = I18nLoaderTest.Initializer.class)
class I18nLoaderTest {
    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis")).withExposedPorts(6379);

    @Test
    void test() {
        System.out.println("--------------------------------");
    }

    @Controller
    @SpringBootApplication
    @Import(RedisConfig.class)
    public static class TestApplication {
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "spring.redis.host=" + redis.getHost(),
                "spring.redis.port=" + redis.getFirstMappedPort()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}