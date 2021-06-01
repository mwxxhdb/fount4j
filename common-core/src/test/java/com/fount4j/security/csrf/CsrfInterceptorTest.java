package com.fount4j.security.csrf;

import com.fount4j.base.bo.TwoResults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.fount4j.test.TestProperties.DISABLE_CONSUL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    DISABLE_CONSUL
})
class CsrfInterceptorTest {
    static class Routes {
        private static final String SET_DEFAULT_CSRF = "/get/test";
        private static final String SET_CUSTOM_CSRF = "/get/custom";
        private static final String NOT_SET_CSRF = "/get/no-csrf";
        private static final String CHECK_CSRF = "/post/check-csrf";
        private static final String NOT_CHECK_CSRF = "/post/not-check-csrf";
    }

    private static final String CUSTOM_HEADER = "X-CUSTOM-CSRF";
    private static final String CUSTOM_PARAM = "_custom_csrf";
    private static final String PARAMETER = "parameter";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void when_NoAnnotation_then_SetDefaultCsrfToken() {
        var bodyAndDoc = requestAndParseBody(Routes.SET_DEFAULT_CSRF);
        assertTokenInMetaTags(bodyAndDoc, CsrfToken.DEFAULT_HEADER_NAME, CsrfToken.ATTRIBUTE_NAME);
    }

    @Test
    void when_HaveAnnotation_then_SetCustomizedCsrfToken() {
        var bodyAndDoc = requestAndParseBody(Routes.SET_CUSTOM_CSRF);
        assertTokenInMetaTags(bodyAndDoc, CUSTOM_HEADER, CUSTOM_PARAM);
    }

    @Test
    void when_DisableInAnnotation_then_ShouldNotSetCsrfToken() {
        var bodyAndDoc = requestAndParseBody(Routes.NOT_SET_CSRF);
        var body = bodyAndDoc.getFirst();
        var doc = bodyAndDoc.getSecond();
        assertThat(doc.select("meta[name='" + CsrfToken.ATTRIBUTE_NAME + "']").isEmpty()).withFailMessage(body).isTrue();
        assertThat(doc.select("meta[name='" + CsrfToken.HEADER_META_NAME + "']").isEmpty()).withFailMessage(body).isTrue();
        assertThat(doc.select("input[type='hidden']").isEmpty()).withFailMessage(body).isTrue();
    }

    @Test
    void when_ProvideValidCsrfToken_then_ShouldAcceptRequest() {
        var resp = testRestTemplate.getForEntity(Routes.SET_DEFAULT_CSRF, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = resp.getBody();
        assertThat(body).isNotNull();
        var doc = Jsoup.parse(body);
        var token = getMetaContent(doc, CsrfToken.ATTRIBUTE_NAME);
        resp = testRestTemplate.postForEntity(Routes.CHECK_CSRF, createFormEntity(Map.of(PARAMETER, "ShouldAcceptRequest", CsrfToken.ATTRIBUTE_NAME, token)), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void when_NotProvideCsrfToken_then_ShouldNotAcceptRequest() {
        var resp = testRestTemplate.postForEntity(Routes.CHECK_CSRF, createFormEntity(Map.of(PARAMETER, "shouldNotAcceptRequest")), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void when_ProvideInvalidCsrfToken_then_ShouldNotAcceptRequest() {
        var resp = testRestTemplate.getForEntity(Routes.SET_DEFAULT_CSRF, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = resp.getBody();
        assertThat(body).isNotNull();
        var doc = Jsoup.parse(body);
        var token = getMetaContent(doc, CsrfToken.ATTRIBUTE_NAME);
        resp = testRestTemplate.postForEntity(Routes.CHECK_CSRF, createFormEntity(Map.of(PARAMETER, "ShouldNotAcceptRequest", CsrfToken.ATTRIBUTE_NAME, token + "makeTokenInvalid")), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpEntity<MultiValueMap<String, String>> createFormEntity(Map<String, String> data) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        data.forEach(map::add);
        return new HttpEntity<>(map, headers);
    }

    private void assertTokenInMetaTags(TwoResults<String, Document> bodyAndDoc, String expectedHeaderName, String paramName) {
        var body = bodyAndDoc.getFirst();
        var doc = bodyAndDoc.getSecond();
        var token = getMetaContent(doc, CsrfToken.ATTRIBUTE_NAME);
        assertThat(token).withFailMessage(body).isNotNull();
        assertThat(token.length()).withFailMessage(body).isGreaterThan(20);
        var tokenHeader = getMetaContent(doc, CsrfToken.HEADER_META_NAME);
        assertThat(tokenHeader).withFailMessage(body).isEqualTo(expectedHeaderName);
        var tokenInParam = doc.selectFirst("input[name='" + paramName + "']").val();
        assertThat(tokenInParam).withFailMessage(body).isEqualTo(token);
    }

    private TwoResults<String, Document> requestAndParseBody(String uri) {
        var resp = testRestTemplate.getForEntity(uri, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = resp.getBody();
        assertThat(body).isNotNull();
        var doc = Jsoup.parse(body);
        return new TwoResults<>(body, doc);
    }

    private String getMetaContent(Document doc, String name) {
        return doc.selectFirst("meta[name='" + name + "']").attr("content");
    }

    @Controller
    @SpringBootApplication
    public static class TestApplication {
        private static final String TEMPLATE = "csrf";

        @GetMapping(Routes.SET_DEFAULT_CSRF)
        public String setDefaultCsrf() {
            return TEMPLATE;
        }

        @SetCsrfToken(headerName = CUSTOM_HEADER, parameterName = CUSTOM_PARAM)
        @GetMapping(Routes.SET_CUSTOM_CSRF)
        public String setCustomCsrf() {
            return TEMPLATE;
        }

        @SetCsrfToken(false)
        @GetMapping(Routes.NOT_SET_CSRF)
        public String notSetToken() {
            return TEMPLATE;
        }

        @PostMapping(Routes.CHECK_CSRF)
        public String checkCsrfToken(@RequestParam String parameter, HttpServletRequest request) {
            request.setAttribute(PARAMETER, parameter);
            return TEMPLATE;
        }

        @CheckCsrfToken(parameterName = CUSTOM_PARAM, headerName = CUSTOM_HEADER)
        @PostMapping
        public String checkCustomizedCsrf(@RequestParam String parameter, HttpServletRequest request) {
            request.setAttribute(PARAMETER, parameter);
            return TEMPLATE;
        }

        @CheckCsrfToken(false)
        @PostMapping(Routes.NOT_CHECK_CSRF)
        public String notCheckCsrf(@RequestParam String parameter, HttpServletRequest request) {
            request.setAttribute(PARAMETER, parameter);
            return TEMPLATE;
        }
    }
}