package com.marvin.campustrade.data.dto.user;

public record ProfileResponse (
    String fullName,
    Long universityId,
    String universityName,
    String profilePicUrl,
    String description
){}

