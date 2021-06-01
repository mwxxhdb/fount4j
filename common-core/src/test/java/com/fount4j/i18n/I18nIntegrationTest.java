package com.fount4j.i18n;

import com.fount4j.cache.RedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Locale;

import static com.fount4j.test.TestProperties.DISABLE_CONSUL;
import static com.fount4j.test.TestProperties.LOG_TEST_CONTAINERS;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    DISABLE_CONSUL,
    LOG_TEST_CONTAINERS
})
@ContextConfiguration(initializers = I18nIntegrationTest.Initializer.class)
class I18nIntegrationTest {
    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis")).withExposedPorts(6379);
    private static final String URI_MESSAGE = "/i18n/message";
    private static final String URI_MESSAGE_WITH_ARGS = "/i18n/message/{arg}";
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void englishWithoutArgs() {
        var resp = restTemplate.getForEntity(URI_MESSAGE, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("English");
    }

    @Test
    void swedishWithoutArgs() {
        var headers = new HttpHeaders();
        headers.setAcceptLanguage(Locale.LanguageRange.parse("sv"));
        var entity = new HttpEntity<>(headers);
        var resp = restTemplate.exchange(URI_MESSAGE, HttpMethod.GET, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("Hallå");
    }

    @Test
    void chineseWithoutArgs() {
        var headers = new HttpHeaders();
        headers.setAcceptLanguage(Locale.LanguageRange.parse("zh-CN"));
        var entity = new HttpEntity<>(headers);
        var resp = restTemplate.exchange(URI_MESSAGE, HttpMethod.GET, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("中文内容");
    }

    @Test
    void englishWithArgs() {
        var resp = restTemplate.getForEntity(URI_MESSAGE + "/5", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("There is 5 animal(s)");
    }

    @Test
    void swedishWithArgs() {
        var headers = new HttpHeaders();
        headers.setAcceptLanguage(Locale.LanguageRange.parse("sv-SE"));
        var entity = new HttpEntity<>(headers);
        var resp = restTemplate.exchange(URI_MESSAGE + "/fem", HttpMethod.GET, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("Jag är fem år gammal");
    }

    @Test
    void chineseWithArgs() {
        var headers = new HttpHeaders();
        headers.setAcceptLanguage(Locale.LanguageRange.parse("zh-CN"));
        var entity = new HttpEntity<>(headers);
        var resp = restTemplate.exchange(URI_MESSAGE + "/五", HttpMethod.GET, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("我今年五岁了");
    }

    @RestController
    @SpringBootApplication
    @Import(RedisConfig.class)
    public static class TestApplication {
        @Autowired
        LocaleMessage localeMessage;

        @GetMapping(URI_MESSAGE)
        public String messageWithoutArg() {
            return localeMessage.getMessage("test.i18n.content");
        }

        @GetMapping(URI_MESSAGE_WITH_ARGS)
        public String messageWithArg(@PathVariable String arg) {
            return localeMessage.getMessage("test.i18n.args", arg);
        }
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