package com.marvin.campustrade.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import java.time.LocalDateTime;

@Entity
@Table(name = "USERS",
        indexes = {
        @Index(columnList = "UNIVERSITY_ID")
})
@Setter
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE USERS SET IS_ACTIVE = false WHERE ID = ?")
@Where(clause = "IS_ACTIVE = true")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Column(name = "PROFILE_PIC_URL")
    private String profilePicUrl;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @JsonIgnore
    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "IS_ACTIVE", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "IS_VERIFIED", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIVERSITY_ID", nullable = false)
    private University university;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
