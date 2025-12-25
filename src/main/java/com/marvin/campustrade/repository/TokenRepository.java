package com.marvin.campustrade.repository;

import com.marvin.campustrade.constants.TokenType;
import com.marvin.campustrade.data.entity.Token;
import com.marvin.campustrade.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserAndType(Users user, TokenType type);

    @Query(value = """
      select t from Token t inner join Users u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.isExpired = false or t.isRevoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByContent(String content);

    @Modifying
    @Query("""
        DELETE FROM Token t
        WHERE t.type = :type
            AND (
               t.isRevoked = true
               OR t.isExpired = true
               OR t.expiresAt < CURRENT_TIMESTAMP
        )
    """)
    int deleteDeadTokens(@Param("type") TokenType type);
}
