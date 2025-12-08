package com.marvin.campustrade.data.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS_BLOCK",
        indexes = {
                @Index(columnList = "BLOCKER_ID"),
                @Index(columnList = "BLOCKED_ID")
        })
@Setter
@Getter
@NoArgsConstructor
public class UsersBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "BLOCKER_ID", nullable = false)
    private Users blocker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "BLOCKED_ID", nullable = false)
    private Users blocked;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
