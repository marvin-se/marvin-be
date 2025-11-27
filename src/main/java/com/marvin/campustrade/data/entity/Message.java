package com.marvin.campustrade.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MESSAGE",indexes = {
        @Index(columnList = "SENDER_ID"),
        @Index(columnList = "RECEIVER_ID"),
        @Index(columnList = "PRODUCT_ID")
})
@Setter
@Getter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "IS_READ", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead = false;

    @Column(name = "SENT_AT", nullable = false)
    private LocalDateTime sentAt;

    @ManyToOne(optional = true)
    @JoinColumn(name = "SENDER_ID")
    private User sender;

    @ManyToOne(optional = true)
    @JoinColumn(name = "RECEIVER_ID")
    private User receiver;

    @ManyToOne(optional = true)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

}
