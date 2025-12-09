package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.auth.*;
import com.marvin.campustrade.data.entity.Users;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {
    UserResponse createUser(RegisterRequest request);
    Users  getCurrentUser();
    void verifyUser(VerifyRequest request);
    void generateResetEmail(ForgotPasswordRequest request);
    void changePassword(ChangePassword request);
    void verifyResetCode(VerifyRequest request);
}
