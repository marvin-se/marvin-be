package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.auth.LoginDTO;
import com.marvin.campustrade.data.dto.auth.RegisterRequest;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.entity.Users;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {
    UserResponse createUser(RegisterRequest request);
    Users  getCurrentUser();
}
