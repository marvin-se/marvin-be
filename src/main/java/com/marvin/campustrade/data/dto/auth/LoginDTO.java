package com.marvin.campustrade.data.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    // static so no need for setters
    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,
            @NotBlank(message = "Password is required")
            String password
    ) {}

    public record LoginResponse(
            String token,
            UserResponse user
    ){}
}
