package com.marvin.campustrade.data.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;
    private String fullName;
    private String profilePicUrl;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private Long universityId;
}
