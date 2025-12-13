package com.marvin.campustrade.data.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Pattern(
            regexp = "^[1-9][0-9]{9}$",
            message = "Phone number must be 10 digits and cannot start with 0"
    )
    private String phoneNumber;
    private String description;
}
