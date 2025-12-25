package com.marvin.campustrade.scheduler;

import com.marvin.campustrade.constants.TokenType;
import com.marvin.campustrade.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final TokenRepository tokenRepository;

    /**
     * Deletes revoked or expired tokens from database.
     * Runs every hour.
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void deleteDeadTokens() {
        int deletedCount = tokenRepository.deleteDeadTokens(TokenType.BEARER);
        System.out.println("🧹 Token cleanup completed. Deleted tokens: " + deletedCount);
    }
}
