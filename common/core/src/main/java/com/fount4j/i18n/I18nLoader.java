package com.fount4j.i18n;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import static com.fount4j.i18n.RedisMessageResource.CACHE_KEY;
import static com.fount4j.i18n.RedisMessageResource.createMessageKey;

/**
 * @author Morven
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class I18nLoader {
    private static final Pattern I18N_FILENAME_PATTERN = Pattern.compile("i18n_[a-z]{2,3}(_[A-Z]{2,3})?.properties");

    @Value("${fount4j.i18n.supported-languages:zh_CN,en_US}")
    private String supportedLanguages;

    private final HashOperations<String, String, Object> hashOperations;

    @PostConstruct
    public void loadI18nMessages() throws IOException {
        Arrays.stream(supportedLanguages.split(","))
            .forEach(language -> {
                log.debug("Loading i18n_{}.properties", language);
                var res = new ClassPathResource("i18n_" + language + ".properties");
                if (!res.exists()) {
                    log.warn("i18n_{}.properties does not exit", language);
                    return;
                }
                var props = new Properties();
                try {
                    props.load(res.getInputStream());
                    props.forEach((k, v) -> hashOperations.putIfAbsent(CACHE_KEY, createMessageKey((String) k, language), v));
                } catch (IOException e) {
                    log.error("Loading i18n_{}.properties failed", language, e);
                }
            });
    }

}
