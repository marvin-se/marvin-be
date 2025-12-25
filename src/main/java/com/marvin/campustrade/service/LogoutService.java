package com.marvin.campustrade.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface LogoutService {
    void logout(HttpServletRequest request);
}
