package com.marvin.campustrade.data.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_block",
        indexes = {
                @Index(columnList = "blocker_id"),
                @Index(columnList = "blocked_id")
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
    @JoinColumn(name = "blocker_id", nullable = false)
    private Users blocker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blocked_id", nullable = false)
    private Users blocked;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
