package com.marvin.campustrade.data.dto.auth;

import com.marvin.campustrade.data.entity.University;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        Long universityId,
        String universityName,
        String profilePicUrl,
        LocalDateTime createdAt,
        Boolean isActive
) {
}

