package com.fount4j.auth.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class PermissionService implements StpInterface {
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPermissionList(Object loginId, String loginKey) {
        var session = StpUtil.getSession(true);
        List<String> permission = session.getModel("Permission", List.class);
        if (permission != null) {
            log.info("Get permission from session");
            return permission;
        }
        log.info("Get permission for {} {}", loginId, loginKey);
        if ("admin".equals(loginId)) {
            permission = Arrays.asList("p1", "p2");
            session.set("Permission", permission);
            return permission;
        }
        if ("user1".equals(loginId)) {
            return Collections.singletonList("p1");
        }
        return Collections.singletonList("p2");
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        log.info("Get roles for {} {}", loginId, loginKey);
        if ("admin".equals(loginId)) {
            return Arrays.asList("r1", "r2", "r3");
        }
        if ("user1".equals(loginId)) {
            return Collections.singletonList("r1");
        }
        return Collections.singletonList("r2");
    }
}
