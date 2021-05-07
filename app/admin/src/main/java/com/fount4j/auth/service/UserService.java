package com.fount4j.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.fount4j.auth.entity.User;
import com.fount4j.auth.repository.UserRepository;
import com.fount4j.base.bo.BaseResult;
import com.fount4j.i18n.LocaleMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${fount4j.admin.auto-init:false}")
    private boolean autoInitAdmin;
    @Value("${fount4j.admin.username:admin}")
    private String adminUsername;
    @Value("${fount4j.admin.password:admin}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final LocaleMessage localeMessage;

    public boolean initAdmin() {
        long userCount = userRepository.count();
        if (userCount > 0) return false;
        if (!autoInitAdmin) {
            return true;
        }
        var admin = new User();
        admin.setUsername(adminUsername);
        admin.setSalt(IdUtil.fastUUID());
        admin.setPassword(SecureUtil.md5(adminPassword + admin.getSalt()));
        userRepository.save(admin);
        return false;
    }

    public BaseResult<Void> initAdmin(String username, String email, String password, String confirmPassword) {
        if (!Objects.equals(password, confirmPassword)) {
            return BaseResult.fail(localeMessage.getMessage("auth.ui.form.rule.confirmPassword.diff"));
        }
        var admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setSalt(IdUtil.fastUUID());
        admin.setPassword(SecureUtil.md5(password + admin.getSalt()));
        userRepository.save(admin);
        return BaseResult.success();
    }

    public BaseResult<Void> loginWithPassword(String username, String password) {
        var optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            if (Objects.equals(SecureUtil.md5(password + user.getSalt()), user.getPassword())) {
                StpUtil.setLoginId(username);
                StpUtil.getSession().set(User.class.getName(), user);
                return BaseResult.success();
            }
        }
        return BaseResult.fail(localeMessage.getMessage("auth.login.user-not-exists-or-invalid-password"));
    }

}
