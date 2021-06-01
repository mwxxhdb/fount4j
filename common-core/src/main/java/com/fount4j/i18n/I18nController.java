package com.fount4j.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/i18n")
@RequiredArgsConstructor
public class I18nController {
    private final RedisMessageSource redisMessageSource;

    @GetMapping("/messages.js")
    public void javascript(HttpServletResponse response) throws IOException {
        var js = new StringBuilder("const i18n = { get: function (code, ...args) {" +
            " let format = this.messages[code] || code;" +
            " for (let i = 0; i < args.length; i++) {" +
            " format = format.replace('{' + i + '}', args[i]); }" +
            " return format;" +
            " }," +
            "messages: {");
        var messages = redisMessageSource.allMessages();
        messages.forEach((code, format) -> js.append("'").append(code).append("': '").append(format.replace("'", "\\'")).append("',"));
        js.append("}};");
        response.setContentType("application/javascript");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(js.toString());
    }
}
