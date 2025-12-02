package com.marvin.campustrade.data.dto.auth;

import com.marvin.campustrade.data.entity.University;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        University universityName,
        String profilePicUrl
) {}
