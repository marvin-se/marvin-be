package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.auth.*;
import com.marvin.campustrade.service.AuthenticationService;
import com.marvin.campustrade.service.EmailService;
import com.marvin.campustrade.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody VerifyRequest request) {
        userService.verifyUser(request);
        return ResponseEntity.ok("Your account has been verified! You can now log in.");
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resendVerificationEmail(@Valid @RequestParam String email) {
        userService.resendVerificationEmail(email);
        return ResponseEntity.ok("Your verification token has been resend!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.generateResetEmail(request);
        return ResponseEntity.ok("Reset link is sent to your e-mail!");
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<String> verifyResetCode(@RequestBody VerifyRequest request) {
        userService.verifyResetCode(request);
        return ResponseEntity.ok("Code verified. You may now reset your password.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePassword request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Your password has been changed!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO.LoginResponse> login(@Valid @RequestBody LoginDTO.LoginRequest request) {
        LoginDTO.LoginResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}
