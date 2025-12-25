package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.auth.*;
import com.marvin.campustrade.data.dto.user.*;
import com.marvin.campustrade.data.entity.Users;

public interface UserService {
    UserResponse createUser(RegisterRequest request);
    Users  getCurrentUser();
    void verifyUser(VerifyRequest request);
    void generateResetEmail(ForgotPasswordRequest request);
    void changePassword(ChangePassword request);
    void verifyResetCode(VerifyRequest request);
    UserResponse getCurrentProfile();
    UserResponse editProfile(EditProfileRequest request);
    void deleteProfile();
    ProfileResponse getUser(String id);
    BlockResponse blockUser(String id);
    BlockResponse unblockUser(String id);
    SalesResponseDTO getSalesHistory();
    PurchaseResponseDTO getPurchaseHistory();
    void resendVerificationEmail(ResendVerificationCodeDTO request);

    //hilal filter testi silebilirsiniz
    UserResponse findActiveUserByEamil(String email);
    UserResponse findInActiveUserByEmail(String email);
}
