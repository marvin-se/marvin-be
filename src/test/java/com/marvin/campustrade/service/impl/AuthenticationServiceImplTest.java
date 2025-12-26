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
import com.marvin.campustrade.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserMapper userMapper;
    @Mock private TokenRepository tokenRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    // --------------------------------------------------
    // SUCCESS CASE
    // --------------------------------------------------

    @Test
    void login_successfulLogin_returnsJwtAndUser() {

        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@itu.edu.tr");
        user.setIsVerified(true);
        user.setPasswordHash("hashed");

        when(userRepository.findByEmailWithUniversity("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        when(jwtUtils.generateToken("test@itu.edu.tr"))
                .thenReturn("jwt-token");

        when(jwtUtils.getJwtExpirationMs())
                .thenReturn(3600000);

        when(tokenRepository.findAllValidTokenByUser(1L))
                .thenReturn(List.of());

        UserResponse userResponse =
                new UserResponse(
                        1L,
                        "Test User",
                        "test@itu.edu.tr",
                        null,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        true
                );

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        LoginDTO.LoginResponse response =
                authenticationService.login("test@itu.edu.tr", "password");

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("test@itu.edu.tr", response.getUser().email());

        verify(tokenRepository).save(any(Token.class));
    }

    // --------------------------------------------------
    // FAILURE CASES
    // --------------------------------------------------

    @Test
    void login_throwsException_whenUserNotFound() {

        when(userRepository.findByEmailWithUniversity("test@itu.edu.tr"))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthenticationLoginException.class,
                () -> authenticationService.login("test@itu.edu.tr", "password")
        );
    }

    @Test
    void login_throwsException_whenPasswordDoesNotMatch() {

        Users user = new Users();
        user.setEmail("test@itu.edu.tr");
        user.setPasswordHash("hashed");

        when(userRepository.findByEmailWithUniversity("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "hashed"))
                .thenReturn(false);

        assertThrows(
                AuthenticationLoginException.class,
                () -> authenticationService.login("test@itu.edu.tr", "wrong")
        );
    }

    @Test
    void login_throwsException_whenUserNotVerified() {

        Users user = new Users();
        user.setEmail("test@itu.edu.tr");
        user.setPasswordHash("hashed");
        user.setIsVerified(false);

        when(userRepository.findByEmailWithUniversity("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        assertThrows(
                AuthenticationLoginException.class,
                () -> authenticationService.login("test@itu.edu.tr", "password")
        );
    }

    // --------------------------------------------------
    // TOKEN REVOCATION LOGIC
    // --------------------------------------------------

    @Test
    void login_revokesPreviousTokens_whenTheyExist() {

        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@itu.edu.tr");
        user.setIsVerified(true);
        user.setPasswordHash("hashed");

        Token oldToken = new Token();
        oldToken.setExpired(false);
        oldToken.setRevoked(false);

        when(userRepository.findByEmailWithUniversity("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        when(jwtUtils.generateToken("test@itu.edu.tr"))
                .thenReturn("jwt-token");

        when(jwtUtils.getJwtExpirationMs())
                .thenReturn(3600000);

        when(tokenRepository.findAllValidTokenByUser(1L))
                .thenReturn(List.of(oldToken));

        when(userMapper.toResponse(user))
                .thenReturn(new UserResponse(
                        1L,
                        "Test User",
                        "test@itu.edu.tr",
                        null,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        true
                ));

        authenticationService.login("test@itu.edu.tr", "password");

        assertTrue(oldToken.isExpired());
        assertTrue(oldToken.isRevoked());

        verify(tokenRepository, atLeastOnce()).saveAll(any());
        verify(tokenRepository).save(any(Token.class));
    }
}
