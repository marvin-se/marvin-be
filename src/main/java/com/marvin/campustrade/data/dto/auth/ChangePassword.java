package com.marvin.campustrade.data.dto.auth;

import com.marvin.campustrade.constants.RequestType;
import jakarta.validation.constraints.NotBlank;
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
    private String newPassword;
    @NotBlank(message = "Confirmation password can not be null!")
    private String confirmNewPassword;
    private RequestType type;

}
