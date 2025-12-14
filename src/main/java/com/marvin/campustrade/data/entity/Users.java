package com.marvin.campustrade.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        indexes = {
        @Index(columnList = "university_id")
})
@Setter
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE users SET is_active = false WHERE id = ?")
//@Where(clause = "is_active = true")
@FilterDef(
        name = "activeUserFilter",
        parameters = @ParamDef(name = "isActive", type = Boolean.class)
)
@Filter(
        name = "activeUserFilter",
        condition = "is_active = :isActive"
)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "profile_pic_url")
    private String profilePicUrl;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "description", length = 1000)
    private String description;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "is_verified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
