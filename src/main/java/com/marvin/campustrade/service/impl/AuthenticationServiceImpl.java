package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.constants.TokenType;
import com.marvin.campustrade.data.dto.auth.LoginDTO;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.entity.Token;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.UserMapper;
import com.marvin.campustrade.exception.AuthenticationLoginException;
import com.marvin.campustrade.repository.TokenRepository;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.AuthenticationService;
import com.marvin.campustrade.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;

    public LoginDTO.LoginResponse login(String email, String password) {
        Users user = userRepository.findByEmailWithUniversity(email)
                .orElseThrow(() -> new AuthenticationLoginException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationLoginException("Invalid email or password");
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!user.getIsVerified()) {
            throw new AuthenticationLoginException("User email is not verified");
        }
        // Revoke all previous valid tokens
        var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validTokens);
        }

        // Generate new Jwt
        String jwtToken = jwtUtils.generateToken(email);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        // Response
        UserResponse userResponse = userMapper.toResponse(user);
        return new LoginDTO.LoginResponse(jwtToken, userResponse);
    }

    private void saveUserToken(Users user, String jwtToken) {
        // Save Jwt to Db
        Token token = new Token();
        token.setContent(jwtToken);
        token.setUser(user);
        token.setType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);
        token.setExpiresAt(
                LocalDateTime.now()
                        .plus(Duration.ofMillis(jwtUtils.getJwtExpirationMs()))
        );
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Users user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) { return; }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
