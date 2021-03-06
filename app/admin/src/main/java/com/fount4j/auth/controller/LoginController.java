package com.fount4j.auth.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/auth")
public class LoginController {
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        String redirect = request.getParameter("redirect");
        if (StpUtil.isLogin()) {
            if (Objects.nonNull(redirect)) {
                return "redirect:" + redirect;
            }
            return "redirect:index";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginAction(@RequestParam String username, @RequestParam String password) {
        log.info("Login with: {username: {}, password: {}}", username, password);
        StpUtil.setLoginId(username);
        return "redirect:index";
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
