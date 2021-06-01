package com.fount4j.auth.controller;

import com.fount4j.test.RedisAwareTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.fount4j.test.TestProperties.DISABLE_CONSUL;
import static com.fount4j.test.TestProperties.LOG_TEST_CONTAINERS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    DISABLE_CONSUL,
    LOG_TEST_CONTAINERS
})
@ContextConfiguration(initializers = RedisAwareTest.Initializer.class)
class LoginControllerTest implements RedisAwareTest {
    @Autowired
    LoginController loginController;
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    void test() throws Exception {
        mockMvc.perform(get("/auth/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("init-admin"));
    }
}