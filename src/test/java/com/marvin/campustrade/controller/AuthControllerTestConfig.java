package com.marvin.campustrade.controller;

import com.marvin.campustrade.service.AuthenticationService;
import com.marvin.campustrade.service.EmailService;
import com.marvin.campustrade.service.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthControllerTestConfig {

    @Bean
    UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    AuthenticationService authenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }

    @Bean
    EmailService emailService() {
        return Mockito.mock(EmailService.class);
    }
}
