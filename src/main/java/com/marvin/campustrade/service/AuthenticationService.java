package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.auth.LoginDTO;

public interface AuthenticationService {
    LoginDTO.LoginResponse login(String email, String password);
}
