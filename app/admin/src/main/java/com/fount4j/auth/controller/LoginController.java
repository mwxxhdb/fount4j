package com.fount4j.auth.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.fount4j.auth.service.UserService;
import com.fount4j.base.http.IController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController implements IController {
    private static final String PAGE_LOGIN = "login";
    private static final String PAGE_INIT_ADMIN = "init-admin";
    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        String redirect = request.getParameter("redirect");
        if (StpUtil.isLogin()) {
            if (Objects.nonNull(redirect)) {
                return "redirect:" + redirect;
            }
            return "redirect:index";
        }
        if (userService.initAdmin()) {
            return PAGE_INIT_ADMIN;
        }
        return PAGE_LOGIN;
    }

    @PostMapping("/init")
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

    @PostMapping("/login")
    public String loginAction(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        log.info("Login with: {username: {}, password: {}}", username, password);
        var result = userService.loginWithPassword(username, password);
        if (result.isSuccess()) {
            return "redirect:index";
        }
        setErrorMessage(request, result.getMessage());
        return PAGE_LOGIN;
    }

    @GetMapping("/index")
    public String index() {
        StpUtil.checkLogin();
        log.info("Login user: {}", StpUtil.getLoginId());
        return "index";
    }

    @GetMapping("/logout")
    public String logout() {
        StpUtil.logout();
        return "redirect:login";
    }

    @GetMapping("/p1")
    @ResponseBody
    @SaCheckPermission("p1")
    public String p1() {
        return "p1";
    }

    @GetMapping("/p2")
    @ResponseBody
    @SaCheckPermission("p2")
    public String p2() {
        return "p2";
    }

    @GetMapping("/r1")
    @ResponseBody
    @SaCheckRole("r1")
    public String r1() {
        return "r1";
    }
}
