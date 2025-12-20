package com.marvin.campustrade.data.dto.auth;

import com.marvin.campustrade.constants.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassword {
    private String email;
    private String token;
    private String oldPassword;
    @NotBlank(message = "New password can not be null!")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
    @NotBlank(message = "Confirmation password can not be null!")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String confirmNewPassword;
    private RequestType type;

}
