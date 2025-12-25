package com.marvin.campustrade.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message",indexes = {
        @Index(columnList = "sender_id"),
        @Index(columnList = "receiver_id"),
        @Index(columnList = "conversation_id")
})
@Setter
@Getter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead = false;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @ManyToOne(optional = true)
    @JoinColumn(name = "sender_id")
    private Users sender;

    @ManyToOne(optional = true)
    @JoinColumn(name = "receiver_id")
    private Users receiver;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

}
