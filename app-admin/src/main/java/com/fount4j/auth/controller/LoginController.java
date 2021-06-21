package com.fount4j.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fount4j.auth.exception.IllegalRedirectException;
import com.fount4j.auth.service.AuthService;
import com.fount4j.auth.service.UserService;
import com.fount4j.base.http.IController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


/**
 * @author Morven
 */
@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController implements IController {
    private static final String PAGE_LOGIN = "login";
    private static final String PAGE_INDEX = "index";
    private static final String PAGE_INIT_ADMIN = "create-admin";
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/login.view")
    public String loginPage(@RequestParam(required = false) String redirect) throws IllegalRedirectException {
        if (StpUtil.isLogin()) {
            if (Objects.nonNull(redirect)) {
                if (authService.validateRedirectUrl(redirect)) {
                    return redirect(redirect);
                } else {
                    throw new IllegalRedirectException("Not a valid redirect URL");
                }
            }
            return redirect(PAGE_INDEX);
        }
        if (userService.initAdmin()) {
            return PAGE_INIT_ADMIN;
        }
        return PAGE_LOGIN;
    }

    @PostMapping("/login")
    public String loginAction(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        log.info("Login with: {username: {}, password: {}}", username, password);
        var result = userService.loginWithPassword(username, password);
        if (result.isSuccess()) {
            return redirect(PAGE_INDEX);
        }
        setFlashErrorMessage(redirectAttributes, result.getMessage());
        return redirect(PAGE_LOGIN);
    }

    @PostMapping("/admin")
    public String initAdmin(@RequestParam String username, @RequestParam String email,
                            @RequestParam String password, @RequestParam String confirmPassword,
                            HttpServletRequest request) {
        var result = userService.initAdmin(username, email, password, confirmPassword);
        if (result.isSuccess()) {
            return redirect(PAGE_LOGIN);
        }
        setErrorMessage(request, result.getMessage());
        if (userService.initAdmin()) {
            return PAGE_INIT_ADMIN;
        }
        return redirect(PAGE_LOGIN);
    }

    @GetMapping("/index.view")
    public String index() {
        StpUtil.checkLogin();
        return PAGE_INDEX;
    }

    @GetMapping("/logout")
    public String logout() {
        StpUtil.logout();
        return redirect("login.view");
    }

}
