package com.fount4j.base.http;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

public interface IController {
    String ATTR_ERROR_MESSAGE = "ERROR_MESSAGE";

    default void setErrorMessage(HttpServletRequest request, Object message) {
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
    }

    default void setFlashErrorMessage(RedirectAttributes redirectAttributes, Object message) {
        redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, message);
    }

    default String redirect(String uri) {
        return "redirect:" + uri;
    }
}
