package com.marvin.campustrade.data.dto.auth;

import com.marvin.campustrade.data.entity.University;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        University university,
        String profilePicUrl,
        LocalDateTime createdAt,
        Boolean isActive
) {}
