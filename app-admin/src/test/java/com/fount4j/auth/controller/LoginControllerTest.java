package com.fount4j.auth.controller;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.fount4j.auth.entity.User;
import com.fount4j.auth.repository.UserRepository;
import com.fount4j.auth.service.AuthService;
import com.fount4j.test.RedisAwareTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.fount4j.test.TestProperties.DISABLE_CONSUL;
import static com.fount4j.test.TestProperties.LOG_TEST_CONTAINERS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    DISABLE_CONSUL,
    LOG_TEST_CONTAINERS,
    "fount4j.admin.auto-init=false"
})
@ContextConfiguration(initializers = RedisAwareTest.Initializer.class)
class LoginControllerTest implements RedisAwareTest {
    private static final String LOGIN_URI = "/auth/login.view";
    MockMvc mockMvc;
    StpLogic stpLogic = mock(StpLogic.class);
    @Autowired
    LoginController loginController;
    @Autowired
    UserRepository userRepository;
    @MockBean
    AuthService authService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        StpUtil.stpLogic = stpLogic;
    }

    @Test
    @DisplayName("Access login should return init admin page when needed")
    void accessLoginShouldReturnInitAdminPageWhenNeeded() throws Exception {
        userRepository.deleteAll();
        when(stpLogic.isLogin()).thenReturn(false);
        mockMvc.perform(get(LOGIN_URI))
            .andExpect(status().isOk())
            .andExpect(view().name("create-admin"));
    }

    @Test
    @DisplayName("Access login should return login page after admin is created")
    void accessLoginShouldReturnLoginPageAfterAdminIsCreated() throws Exception {
        var admin = new User();
        admin.setUsername("admin");
        admin.setEmail("a@a.a");
        admin.setSalt(IdUtil.fastUUID());
        admin.setPassword(SecureUtil.md5("1" + admin.getSalt()));
        userRepository.save(admin);
        mockMvc.perform(get(LOGIN_URI))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("Access login page should return index page after already login")
    void accessLoginShouldReturnIndexPageAfterAlreadyLogin() throws Exception {
        when(stpLogic.isLogin()).thenReturn(true);
        mockMvc.perform(get(LOGIN_URI))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:index"));
    }

    @Test
    @DisplayName("Access login should redirect to expected page")
    void accessLoginShouldRedirectToExpectedPage() throws Exception {
        when(stpLogic.isLogin()).thenReturn(true);
        when(authService.validateRedirectUrl(anyString())).thenReturn(true);
        mockMvc.perform(get(LOGIN_URI).param("redirect", "https://google.com"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Access login with invalid redirect URL should return forbidden status")
    void accessLoginWithInvalidRedirectUrlShouldReturnForbiddenStatus() throws Exception {
        when(stpLogic.isLogin()).thenReturn(true);
        when(authService.validateRedirectUrl(anyString())).thenReturn(false);
        mockMvc.perform(get(LOGIN_URI).param("redirect", "https://google.com"))
            .andExpect(status().isForbidden());
    }


}