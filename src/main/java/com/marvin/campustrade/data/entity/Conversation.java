package com.marvin.campustrade.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONVERSATION",
        indexes = {
                @Index(columnList = "USER_ONE_ID"),
                @Index(columnList = "USER_TWO_ID"),
                @Index(columnList = "PRODUCT_ID")
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
    @JoinColumn(name = "USER_ONE_ID", nullable = false)
    private Users user1;

    @ManyToOne
    @JoinColumn(name = "USER_TWO_ID", nullable = false)
    private Users user2;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
