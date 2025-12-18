package com.marvin.campustrade.repository;

import com.marvin.campustrade.constants.TokenType;
import com.marvin.campustrade.data.entity.Token;
import com.marvin.campustrade.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserAndType(Users user, TokenType type);
}
