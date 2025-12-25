package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.entity.Token;
import com.marvin.campustrade.exception.NoActiveSessionException;
import com.marvin.campustrade.repository.TokenRepository;
import com.marvin.campustrade.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    private final TokenRepository tokenRepository;

    @Transactional
    @Override
    public void logout(HttpServletRequest request) {
        System.out.println(">>> LOGOUT SERVICE ENTERED <<<");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NoActiveSessionException("No active session");
        }

        String jwt = authHeader.substring(7).trim();

        Token token = tokenRepository.findByContent(jwt)
                .orElseThrow(() -> new NoActiveSessionException("No active session"));

        if (token.isExpired() || token.isRevoked()) {
            throw new NoActiveSessionException("No active session");
        }

        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);

        SecurityContextHolder.clearContext();

        System.out.println(">>> LOGOUT SUCCESSFUL <<<");
    }
}
