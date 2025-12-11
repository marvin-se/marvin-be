package com.marvin.campustrade.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation",
        indexes = {
                @Index(columnList = "user_one_id"),
                @Index(columnList = "user_two_id"),
                @Index(columnList = "product_id")
        })
@Setter
@Getter
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_one_id")
    private Users user1;

    @ManyToOne
    @JoinColumn(name = "user_two_id")
    private Users user2;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
