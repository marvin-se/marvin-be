package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.auth.LoginDTO;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.UserMapper;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.AuthenticationService;
import com.marvin.campustrade.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    public LoginDTO.LoginResponse login(String email, String password) {
        Users user = userRepository.findByEmailWithUniversity(email).orElse(null);

        if(user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            String jwtToken = jwtUtils.generateToken(email);

            UserResponse userResponse = userMapper.toResponse(user);

            return new LoginDTO.LoginResponse(jwtToken, userResponse);
        }
        throw new RuntimeException("Invalid email or password");
    }
}
