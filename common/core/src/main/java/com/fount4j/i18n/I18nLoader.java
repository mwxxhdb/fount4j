package com.fount4j.i18n;

import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.fount4j.i18n.RedisMessageSource.CACHE_KEY;
import static com.fount4j.i18n.RedisMessageSource.createMessageKey;

/**
 * @author Morven
 */
@Slf4j
@Component
public class I18nLoader {

    @Value(I18nConfig.VALUE_SUPPORTED_LOCALES)
    private String supportedLocales;

    private final HashOperations<String, String, String> hashOperations;

    @Autowired
    public I18nLoader(@Qualifier("hashStringOperations") HashOperations<String, String, String> hashOperations) {
        this.hashOperations = hashOperations;
    }

    @PostConstruct
    public void loadI18nMessages() {
        Arrays.stream(supportedLocales.split(","))
            .map(lang -> lang.replace('-', '_'))
            .forEach(language -> {
                log.debug("Loading i18n_{}.properties", language);
                var res = new ClassPathResource("i18n_" + language + ".properties");
                if (!res.exists()) {
                    log.warn("i18n_{}.properties does not exit", language);
                    return;
                }
                Props.getProp(res.getPath(), StandardCharsets.UTF_8).forEach((k, v) ->
                    hashOperations.putIfAbsent(CACHE_KEY, createMessageKey((String) k, language), v.toString())
                );
            });
    }

}
