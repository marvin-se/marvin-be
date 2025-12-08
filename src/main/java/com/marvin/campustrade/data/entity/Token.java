package com.marvin.campustrade.data.entity;

import com.marvin.campustrade.constants.TokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TOKEN",
        indexes = {
                @Index(columnList = "USER_ID"),
        })
@Setter
@Getter
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private TokenType type;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "IS_VERIFIED", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified = false;

}
