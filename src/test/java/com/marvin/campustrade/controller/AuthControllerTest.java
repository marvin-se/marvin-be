package com.marvin.campustrade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.campustrade.constants.RequestType;
import com.marvin.campustrade.data.dto.auth.*;
import com.marvin.campustrade.security.AuthTokenFilter;
import com.marvin.campustrade.service.AuthenticationService;
import com.marvin.campustrade.service.EmailService;
import com.marvin.campustrade.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = AuthTokenFilter.class
                )
        }
)
@Import(AuthControllerTestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private EmailService emailService;


    @Test
    void register_shouldReturnOk_whenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "test@mail.com",
                "password123",
                "ITU",
                "5551234567"
        );

        UserResponse response = new UserResponse(
                1L,
                "Test User",
                "test@mail.com",
                "5551234567",
                10L,
                "Istanbul Technical University",
                null,
                LocalDateTime.now(),
                true
        );

        Mockito.when(userService.createUser(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@mail.com"))
                .andExpect(jsonPath("$.universityName")
                        .value("Istanbul Technical University"))
                .andExpect(jsonPath("$.isActive").value(true));
    }


    @Test
    void register_shouldFail_whenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "invalid-email",
                "password123",
                "ITU",
                null
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("email: Invalid email format"));
    }


    @Test
    void verify_shouldReturnSuccessMessage() throws Exception {
        VerifyRequest request = new VerifyRequest("test@mail.com", "123456");

        mockMvc.perform(post("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Your account has been verified! You can now log in."
                ));

        Mockito.verify(userService).verifyUser(Mockito.any());
    }


    @Test
    void resendVerification_shouldReturnSuccessMessage() throws Exception {
        ResendVerificationCodeDTO request =
                new ResendVerificationCodeDTO("test@mail.com");

        mockMvc.perform(post("/auth/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Your verification token has been resend!"
                ));

        Mockito.verify(userService).resendVerificationEmail(Mockito.any());
    }


    @Test
    void forgotPassword_shouldReturnSuccessMessage() throws Exception {
        ForgotPasswordRequest request =
                new ForgotPasswordRequest("test@mail.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Reset link is sent to your e-mail!"
                ));

        Mockito.verify(userService).generateResetEmail(Mockito.any());
    }


    @Test
    void verifyResetCode_shouldReturnSuccessMessage() throws Exception {
        VerifyRequest request = new VerifyRequest("test@mail.com", "654321");

        mockMvc.perform(post("/auth/verify-reset-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Code verified. You may now reset your password."
                ));

        Mockito.verify(userService).verifyResetCode(Mockito.any());
    }

    @Test
    void changePassword_shouldReturnOk_whenRequestIsValid() throws Exception {
        ChangePassword request = new ChangePassword(
                "test@mail.com",
                "reset-token",
                "oldPassword123",
                "newPassword123",
                "newPassword123",
                RequestType.CHANGE_PASSWORD
        );

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Your password has been changed!"));

        Mockito.verify(userService).changePassword(Mockito.any());
    }

    @Test
    void changePassword_shouldFail_whenNewPasswordTooShort() throws Exception {
        ChangePassword request = new ChangePassword(
                "test@mail.com",
                "token",
                "oldPassword",
                "123",
                "123",
                RequestType.CHANGE_PASSWORD
        );

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).changePassword(Mockito.any());
    }


    @Test
    void login_shouldReturnTokenAndUser() throws Exception {
        LoginDTO.LoginRequest request =
                new LoginDTO.LoginRequest("test@mail.com", "password123");

        UserResponse userResponse = new UserResponse(
                1L,
                "Test User",
                "test@mail.com",
                "5551234567",
                10L,
                "Istanbul Technical University",
                null,
                LocalDateTime.now(),
                true
        );

        LoginDTO.LoginResponse response =
                new LoginDTO.LoginResponse("jwt-token", userResponse);

        Mockito.when(authService.login(
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@mail.com"))
                .andExpect(jsonPath("$.user.fullName").value("Test User"))
                .andExpect(jsonPath("$.user.isActive").value(true));
    }

}
