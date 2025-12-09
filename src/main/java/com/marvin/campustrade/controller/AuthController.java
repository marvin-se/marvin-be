package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.auth.LoginDTO;
import com.marvin.campustrade.data.dto.auth.RegisterRequest;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.service.AuthenticationService;
import com.marvin.campustrade.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final UserService userService;
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO.LoginResponse> login(@Valid @RequestBody LoginDTO.LoginRequest request) {
        LoginDTO.LoginResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}
