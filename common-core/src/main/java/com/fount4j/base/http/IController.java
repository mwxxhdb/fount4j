package com.fount4j.base.http;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Morven
 */
public interface IController {
    String ATTR_ERROR_MESSAGE = "ERROR_MESSAGE";

    /**
     * Set error message to request attribute
     *
     * @param request servlet request
     * @param message message object
     */
    default void setErrorMessage(HttpServletRequest request, Object message) {
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
    }

    /**
     * Set flash scoped message
     *
     * @param redirectAttributes redirect attributes
     * @param message            message object
     */
    default void setFlashErrorMessage(RedirectAttributes redirectAttributes, Object message) {
        redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, message);
    }

    /**
     * Redirect to an url or view
     *
     * @param urlOrView url or view name
     * @return redirect: + url or view name
     */
    default String redirect(String urlOrView) {
        return "redirect:" + urlOrView;
    }
}
